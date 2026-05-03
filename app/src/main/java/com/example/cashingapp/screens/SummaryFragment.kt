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

class SummaryFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        var totalIngresos = 0.0
        var totalGastos = 0.0

        // OBSERVAR TOTAL DE INGRESOS HISTÓRICO
        viewModel.totalIngresos().observe(viewLifecycleOwner) { total ->
            totalIngresos = total ?: 0.0
            view.findViewById<TextView>(R.id.tv_total_ingresos).text =
                String.format("%.2f €", totalIngresos).replace(".", ",")
            view.findViewById<TextView>(R.id.tv_balance).text =
                String.format("%.2f €", totalIngresos - totalGastos).replace(".", ",")
        }

        // OBSERVAR TOTAL DE GASTOS HISTÓRICO
        viewModel.totalGastos().observe(viewLifecycleOwner) { total ->
            totalGastos = total ?: 0.0
            view.findViewById<TextView>(R.id.tv_total_gastos).text =
                String.format("%.2f €", totalGastos).replace(".", ",")
            view.findViewById<TextView>(R.id.tv_balance).text =
                String.format("%.2f €", totalIngresos - totalGastos).replace(".", ",")
        }
    }
}