package com.example.cashingapp.screens

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.cashingapp.R
import com.example.cashingapp.model.Category
import com.example.cashingapp.model.Transaction
import com.example.cashingapp.viewmodel.CategoryViewModel
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    private var listaCategorias: List<Category> = emptyList()
    private var movimientosMes: List<Transaction> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mesActual = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        pieChart = view.findViewById(R.id.pie_chart)
        barChart = view.findViewById(R.id.bar_chart)

        configurarPieChart()
        configurarBarChart()

        // -----------------------------------------------------------------------
        // OBSERVAR CATEGORÍAS
        // - Cuando llegan guardamos la lista y redibujamos los gráficos
        // -----------------------------------------------------------------------
        categoryViewModel.categorias.observe(viewLifecycleOwner) { categorias ->
            listaCategorias = categorias
            actualizarGraficos()
        }

        // -----------------------------------------------------------------------
        // OBSERVAR MOVIMIENTOS DEL MES
        // - Cuando llegan guardamos la lista y redibujamos los gráficos
        // -----------------------------------------------------------------------
        transactionViewModel.obtenerPorMes(mesActual).observe(viewLifecycleOwner) { lista ->
            movimientosMes = lista
            actualizarGraficos()
        }
    }

    // -----------------------------------------------------------------------
    // actualizarGraficos
    // - Solo dibuja cuando ya tenemos tanto categorías como movimientos
    // -----------------------------------------------------------------------
    private fun actualizarGraficos() {
        if (listaCategorias.isEmpty()) return

        // GRÁFICO CIRCULAR - gastos por categoría
        val gastos = movimientosMes.filter { it.tipo == "GASTO" }
        val agrupados = gastos.groupBy { it.categoriaId }

        val entriesPie = agrupados.map { (categoriaId, movimientos) ->
            val nombreCategoria = listaCategorias
                .find { it.id == categoriaId }?.nombre ?: "Sin categoría"
            PieEntry(movimientos.sumOf { it.importe }.toFloat(), nombreCategoria)
        }

        if (entriesPie.isNotEmpty()) {
            val dataSetPie = PieDataSet(entriesPie, "")
            val pieColors = agrupados.keys.map { categoriaId ->
                val colorHex = listaCategorias.find { it.id == categoriaId }?.color ?: "#7C3AED"
                Color.parseColor(colorHex)
            }
            dataSetPie.colors = pieColors
            dataSetPie.valueTextColor = Color.WHITE
            dataSetPie.valueTextSize = 12f
            pieChart.data = PieData(dataSetPie)
            pieChart.invalidate()
        }

        // GRÁFICO DE BARRAS - ingresos vs gastos del mes
        val totalIngresos = movimientosMes.filter { it.tipo == "INGRESO" }.sumOf { it.importe }.toFloat()
        val totalGastos = movimientosMes.filter { it.tipo == "GASTO" }.sumOf { it.importe }.toFloat()

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

    private fun configurarPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.holeRadius = 40f
        pieChart.legend.textColor = Color.WHITE
        pieChart.setEntryLabelColor(Color.WHITE)
    }

    private fun configurarBarChart() {
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.axisLeft.textColor = Color.WHITE
        barChart.axisRight.isEnabled = false
        barChart.axisLeft.axisMinimum = 0f
        barChart.setFitBars(true)

        // Etiquetas del eje X: "Ingresos" y "Gastos"
        val etiquetas = listOf("Ingresos", "Gastos")
        barChart.xAxis.textColor = Color.WHITE
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return etiquetas.getOrElse(value.toInt()) { "" }
            }
        }
    }
}