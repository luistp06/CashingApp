package com.example.cashingapp.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashingapp.R
import com.example.cashingapp.viewmodel.CategoryViewModel
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        transactionViewModel = ViewModelProvider(this)[TransactionViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = TransactionAdapter(
            onEditar = { transaction ->
                // TODO: navigate to edit form
            },
            onEliminar = { transaction ->
                transactionViewModel.eliminar(transaction)
            }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_movimientos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe categories and pass them to the adapter
        categoryViewModel.categorias.observe(viewLifecycleOwner) { categories ->
            adapter.actualizarCategorias(categories)
        }

        // Observe transactions for current month
        transactionViewModel.obtenerPorMes(currentMonth).observe(viewLifecycleOwner) { list ->
            adapter.actualizarLista(list)
        }

        // Observe totals
        transactionViewModel.totalIngresosMes(currentMonth).observe(viewLifecycleOwner) { total ->
            view.findViewById<TextView>(R.id.tv_ingresos).text = "${total ?: 0.0} €"
        }

        transactionViewModel.totalGastosMes(currentMonth).observe(viewLifecycleOwner) { total ->
            view.findViewById<TextView>(R.id.tv_gastos).text = "${total ?: 0.0} €"
        }

        view.findViewById<FloatingActionButton>(R.id.fab_añadir).setOnClickListener {
            findNavController().navigate(R.id.addTransactionFragment)
        }
    }
}