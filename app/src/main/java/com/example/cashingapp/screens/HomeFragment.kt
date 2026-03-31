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
import com.example.cashingapp.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var adapter: TransactionAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val mesActual = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())


        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]


        adapter = TransactionAdapter(
            onEditar = { transaction ->
                // TODO: navegar al formulario de edición pasando el id del movimiento
            },
            onEliminar = { transaction ->
                viewModel.eliminar(transaction)
            }
        )


        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_movimientos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter


        viewModel.obtenerPorMes(mesActual).observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }


        viewModel.totalIngresosMes(mesActual).observe(viewLifecycleOwner) { total ->
            view.findViewById<TextView>(R.id.tv_ingresos).text = "${total ?: 0.0} €"
        }


        viewModel.totalGastosMes(mesActual).observe(viewLifecycleOwner) { total ->
            view.findViewById<TextView>(R.id.tv_gastos).text = "${total ?: 0.0} €"
        }


        view.findViewById<FloatingActionButton>(R.id.fab_añadir).setOnClickListener {
            findNavController().navigate(R.id.addTransactionFragment)
        }
    }
}