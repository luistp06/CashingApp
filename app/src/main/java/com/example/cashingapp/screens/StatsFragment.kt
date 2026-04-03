package com.example.cashingapp.screens

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cashingapp.R
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {


    private lateinit var viewModel: TransactionViewModel
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart


    // onCreateView
    // - Infla el layout fragment_stats.xml

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }


    // onViewCreated
    // - Inicializa los gráficos y los conecta con los datos del ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mesActual = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        pieChart = view.findViewById(R.id.pie_chart)
        barChart = view.findViewById(R.id.bar_chart)

        configurarPieChart()
        configurarBarChart()


        // OBSERVAR MOVIMIENTOS DEL MES
        // - Cuando cambian los movimientos actualizamos ambos gráficos

        viewModel.obtenerPorMes(mesActual).observe(viewLifecycleOwner) { lista ->

            // GRÁFICO CIRCULAR - gastos agrupados por categoriaId
            val gastos = lista.filter { it.tipo == "GASTO" }
            val agrupados = gastos.groupBy { it.categoriaId }

            val entriesPie = agrupados.map { (categoriaId, movimientos) ->
                PieEntry(movimientos.sumOf { it.importe }.toFloat(), "Cat $categoriaId")
            }

            if (entriesPie.isNotEmpty()) {
                val dataSetPie = PieDataSet(entriesPie, "")
                dataSetPie.colors = ColorTemplate.MATERIAL_COLORS.toList()
                dataSetPie.valueTextColor = Color.WHITE
                dataSetPie.valueTextSize = 12f
                pieChart.data = PieData(dataSetPie)
                pieChart.invalidate()
            }

            // GRÁFICO DE BARRAS - ingresos vs gastos del mes
            val totalIngresos = lista.filter { it.tipo == "INGRESO" }.sumOf { it.importe }.toFloat()
            val totalGastos = lista.filter { it.tipo == "GASTO" }.sumOf { it.importe }.toFloat()

            val entriesBar = listOf(
                BarEntry(0f, totalIngresos),
                BarEntry(1f, totalGastos)
            )

            val dataSetBar = BarDataSet(entriesBar, "")
            dataSetBar.colors = listOf(
                resources.getColor(R.color.ingreso, null),
                resources.getColor(R.color.gasto, null)
            )
            dataSetBar.valueTextColor = Color.WHITE
            dataSetBar.valueTextSize = 12f

            barChart.data = BarData(dataSetBar)
            barChart.invalidate()
        }
    }

    // -----------------------------------------------------------------------
    // configurarPieChart
    // - Configuración visual del gráfico circular
    // -----------------------------------------------------------------------
    private fun configurarPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.holeRadius = 40f
        pieChart.legend.textColor = Color.WHITE
        pieChart.setEntryLabelColor(Color.WHITE)
    }

    // -----------------------------------------------------------------------
    // configurarBarChart
    // - Configuración visual del gráfico de barras
    // -----------------------------------------------------------------------
    private fun configurarBarChart() {
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false
        barChart.xAxis.textColor = Color.WHITE
        barChart.setFitBars(true)
    }
}