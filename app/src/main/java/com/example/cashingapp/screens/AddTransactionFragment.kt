package com.example.cashingapp.screens

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.cashingapp.R
import com.example.cashingapp.model.Transaction
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
class AddTransactionFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private var fechaSeleccionada: String = ""


    // onCreateView
    // - Infla el layout fragment_add_transaction.xml

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false)
    }


    // onViewCreated
    // - Conectamos todos los elementos del formulario con su lógica

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Referencias a los elementos del formulario
        val etImporte = view.findViewById<TextInputEditText>(R.id.et_importe)
        val rgTipo = view.findViewById<RadioGroup>(R.id.rg_tipo)
        val btnFecha = view.findViewById<Button>(R.id.btn_fecha)
        val etNota = view.findViewById<TextInputEditText>(R.id.et_nota)
        val btnGuardar = view.findViewById<Button>(R.id.btn_guardar)

        // Fecha por defecto: hoy
        fechaSeleccionada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        btnFecha.text = fechaSeleccionada


        // SELECTOR DE FECHA

        // - Guarda la fecha seleccionada en formato "yyyy-MM-dd"

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


        // BOTÓN GUARDAR
        // - Valida que el importe no esté vacío
        // - Crea un objeto Transaction con los datos del formulario
        // - Lo guarda en la base de datos a través del ViewModel
        // - Vuelve a la pantalla anterior

        btnGuardar.setOnClickListener {

            val importeTexto = etImporte.text.toString()


            if (importeTexto.isEmpty()) {
                Toast.makeText(requireContext(), "Introduce un importe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Determinar el tipo según el RadioButton seleccionado
            val tipo = if (rgTipo.checkedRadioButtonId == R.id.rb_ingreso) "INGRESO" else "GASTO"

            // Crear el objeto Transaction
            val transaction = Transaction(
                importe = importeTexto.toDouble(),
                tipo = tipo,
                categoriaId = 1, // TODO: usar categoría seleccionada por el usuario
                fecha = fechaSeleccionada,
                nota = etNota.text.toString().ifEmpty { null }
            )

            // Guardar en la base de datos
            viewModel.insertar(transaction)

            // Volver a la pantalla anterior
            findNavController().popBackStack()
        }
    }
}