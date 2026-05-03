package com.example.cashingapp.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cashingapp.R
import com.example.cashingapp.model.Category
import com.example.cashingapp.model.Transaction
import com.example.cashingapp.viewmodel.CategoryViewModel
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddTransactionFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectedDate: String = ""
    private var categoryList: List<Category> = emptyList()

    // ID del movimiento que estamos editando (-1 si es nuevo)
    private var transactionId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Leer el argumento transactionId del nav_graph
        transactionId = arguments?.getInt("transactionId", -1) ?: -1

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val etImporte = view.findViewById<TextInputEditText>(R.id.et_importe)
        val rgTipo = view.findViewById<RadioGroup>(R.id.rg_tipo)
        val spinnerCategoria = view.findViewById<Spinner>(R.id.spinner_categoria)
        val btnFecha = view.findViewById<Button>(R.id.btn_fecha)
        val etNota = view.findViewById<TextInputEditText>(R.id.et_nota)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar)
        val btnEliminar = view.findViewById<Button>(R.id.btn_eliminar)
        val tvTitulo = view.findViewById<TextView>(R.id.tv_titulo_formulario)

        // Fecha por defecto: hoy
        selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        btnFecha.text = selectedDate

        // -----------------------------------------------------------------------
        // CARGAR CATEGORÍAS EN EL SPINNER
        // -----------------------------------------------------------------------
        categoryViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            categoryList = categorias

            val nombres = categorias.map { it.nombre }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombres
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter

            // Si estamos editando, precargar los datos del movimiento
            if (transactionId != -1) {
                transactionViewModel.todos.observe(viewLifecycleOwner) { todos ->
                    val transaction = todos.find { it.id == transactionId }
                    transaction?.let {
                        tvTitulo.text = "Editar movimiento"
                        etImporte.setText(it.importe.toString())
                        selectedDate = it.fecha
                        btnFecha.text = it.fecha
                        etNota.setText(it.nota ?: "")

                        // Seleccionar el tipo correcto
                        if (it.tipo == "INGRESO") {
                            rgTipo.check(R.id.rb_ingreso)
                        } else {
                            rgTipo.check(R.id.rb_gasto)
                        }

                        // Seleccionar la categoría correcta en el spinner
                        val categoriaIndex = categoryList.indexOfFirst { cat -> cat.id == it.categoriaId }
                        if (categoriaIndex >= 0) {
                            spinnerCategoria.setSelection(categoriaIndex)
                        }

                        // Mostrar botón eliminar
                        btnEliminar.visibility = View.VISIBLE
                    }
                }
            }
        }

        // -----------------------------------------------------------------------
        // SELECTOR DE FECHA
        // -----------------------------------------------------------------------
        btnFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, anio, mes, dia ->
                    selectedDate = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                    btnFecha.text = selectedDate
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // -----------------------------------------------------------------------
        // BOTÓN GUARDAR
        // -----------------------------------------------------------------------
        btnGuardar.setOnClickListener {
            val importeTexto = etImporte.text.toString()

            if (importeTexto.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce un importe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (categoryList.isEmpty()) {
                Toast.makeText(requireContext(), "Crea una categoría primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipo = if (rgTipo.checkedRadioButtonId == R.id.rb_ingreso) "INGRESO" else "GASTO"
            val categoriaSeleccionada = categoryList[spinnerCategoria.selectedItemPosition]

            val transaction = Transaction(
                id = if (transactionId != -1) transactionId else 0,
                importe = importeTexto.toDouble(),
                tipo = tipo,
                categoriaId = categoriaSeleccionada.id,
                fecha = selectedDate,
                nota = etNota.text.toString().ifEmpty { null }
            )

            if (transactionId != -1) {
                transactionViewModel.actualizar(transaction)
            } else {
                transactionViewModel.insertar(transaction)
            }

            findNavController().popBackStack()
        }

        // -----------------------------------------------------------------------
        // BOTÓN ELIMINAR
        // -----------------------------------------------------------------------
        btnEliminar.setOnClickListener {
            transactionViewModel.todos.value?.find { it.id == transactionId }?.let {
                transactionViewModel.eliminar(it)
            }
            findNavController().popBackStack()
        }
    }
}