package com.example.cashingapp.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cashingapp.R
import com.example.cashingapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryFragment : Fragment() {


    private lateinit var viewModel: TransactionViewModel


    // onCreateView
    // - Infla el layout fragment_summary.xml

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }


    // onViewCreated
    // - Conectamos el ViewModel y observamos los totales del mes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el mes actual en formato "2026-03"
        val mesActual = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        // Mostrar el mes actual en el título
        view.findViewById<TextView>(R.id.tv_mes_resumen).text =
            SimpleDateFormat("MMMM yyyy", Locale("es")).format(Date())


        // OBSERVAR TOTAL DE INGRESOS
        // - Actualiza el TextView de ingresos cuando cambie el total

        viewModel.totalIngresosMes(mesActual).observe(viewLifecycleOwner) { total ->
            val ingresos = total ?: 0.0
            view.findViewById<TextView>(R.id.tv_total_ingresos).text = "$ingresos €"
            actualizarBalance(view, ingresos, obtenerGastos(view))
        }


        // OBSERVAR TOTAL DE GASTOS
        // - Actualiza el TextView de gastos cuando cambie el total

        viewModel.totalGastosMes(mesActual).observe(viewLifecycleOwner) { total ->
            val gastos = total ?: 0.0
            view.findViewById<TextView>(R.id.tv_total_gastos).text = "$gastos €"
            actualizarBalance(view, obtenerIngresos(view), gastos)
        }
    }


    // obtenerIngresos / obtenerGastos
    // - Leen el valor actual del TextView para calcular el balance

    private fun obtenerIngresos(view: View): Double {
        return view.findViewById<TextView>(R.id.tv_total_ingresos)
            .text.toString().replace(" €", "").toDoubleOrNull() ?: 0.0
    }

    private fun obtenerGastos(view: View): Double {
        return view.findViewById<TextView>(R.id.tv_total_gastos)
            .text.toString().replace(" €", "").toDoubleOrNull() ?: 0.0
    }


    // actualizarBalance
    // - Calcula el balance (ingresos - gastos) y lo muestra en pantalla

    private fun actualizarBalance(view: View, ingresos: Double, gastos: Double) {
        val balance = ingresos - gastos
        view.findViewById<TextView>(R.id.tv_balance).text = "$balance €"
    }
}