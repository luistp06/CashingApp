package com.example.cashingapp.screens

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashingapp.R
import com.example.cashingapp.model.Category
import com.example.cashingapp.viewmodel.CategoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : Fragment() {

    private lateinit var viewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter


    // onCreateView
    // - Infla el layout fragment_categories.xml

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }


    // onViewCreated
    // - Conectamos el ViewModel, RecyclerView y el FAB

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        // Inicializar Adapter
        adapter = CategoryAdapter(
            onEditar = { category ->
                mostrarDialogoEditar(category)
            },
            onEliminar = { category ->
                viewModel.eliminar(category)
            }
        )

        // Configurar RecyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_categorias)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observar categorías y actualizar la lista cuando cambien
        viewModel.categorias.observe(viewLifecycleOwner) { lista ->
            adapter.actualizarLista(lista)
        }

        // FAB → mostrar diálogo para añadir categoría
        view.findViewById<FloatingActionButton>(R.id.fab_añadir_categoria).setOnClickListener {
            mostrarDialogoAnadir()
        }
    }


    // mostrarDialogoAnadir
    // - Muestra un AlertDialog con un campo de texto para el nombre
    // - Al confirmar crea una nueva categoría y la guarda en la BD

    private fun mostrarDialogoAnadir() {
        val input = EditText(requireContext())
        input.hint = "Nombre de la categoría"

        AlertDialog.Builder(requireContext())
            .setTitle("Nueva categoría")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = input.text.toString()
                if (nombre.isNotEmpty()) {
                    viewModel.insertar(Category(nombre = nombre, icono = "📁", color = "#7C3AED"))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    // mostrarDialogoEditar
    // - Muestra un AlertDialog con el nombre actual para editarlo
    // - Al confirmar actualiza la categoría en la BD

    private fun mostrarDialogoEditar(category: Category) {
        val input = EditText(requireContext())
        input.setText(category.nombre)

        AlertDialog.Builder(requireContext())
            .setTitle("Editar categoría")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = input.text.toString()
                if (nombre.isNotEmpty()) {
                    viewModel.actualizar(category.copy(nombre = nombre))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}