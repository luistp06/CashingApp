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
    private var fechaSeleccionada: String = ""

    // Lista de categorías disponibles (se rellena cuando la BD responde)
    private var listaCategorias: List<Category> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModels
        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        // Referencias a los elementos del formulario
        val etImporte = view.findViewById<TextInputEditText>(R.id.et_importe)
        val rgTipo = view.findViewById<RadioGroup>(R.id.rg_tipo)
        val spinnerCategoria = view.findViewById<Spinner>(R.id.spinner_categoria)
        val btnFecha = view.findViewById<Button>(R.id.btn_fecha)
        val etNota = view.findViewById<TextInputEditText>(R.id.et_nota)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar)

        // Fecha por defecto: hoy
        fechaSeleccionada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        btnFecha.text = fechaSeleccionada

        // -----------------------------------------------------------------------
        // CARGAR CATEGORÍAS EN EL SPINNER
        // - Observamos la lista de categorías de la BD
        // - Cuando llegan, creamos un ArrayAdapter con los nombres
        // - El Spinner muestra los nombres pero guardamos el objeto Category entero
        // -----------------------------------------------------------------------
        categoryViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            listaCategorias = categorias

            val nombres = categorias.map { it.nombre }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                nombres
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategoria.adapter = adapter
        }

        // -----------------------------------------------------------------------
        // SELECTOR DE FECHA
        // -----------------------------------------------------------------------
        btnFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, anio, mes, dia ->
                    fechaSeleccionada = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
                    btnFecha.text = fechaSeleccionada
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

            // Validar que haya al menos una categoría seleccionada
            if (listaCategorias.isEmpty()) {
                Toast.makeText(requireContext(), "Crea una categoría primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tipo = if (rgTipo.checkedRadioButtonId == R.id.rb_ingreso) "INGRESO" else "GASTO"

            // Obtener la categoría seleccionada en el Spinner
            val categoriaSeleccionada = listaCategorias[spinnerCategoria.selectedItemPosition]

            val transaction = Transaction(
                importe = importeTexto.toDouble(),
                tipo = tipo,
                categoriaId = categoriaSeleccionada.id,
                fecha = fechaSeleccionada,
                nota = etNota.text.toString().ifEmpty { null }
            )

            transactionViewModel.insertar(transaction)
            findNavController().popBackStack()
        }
    }
}
