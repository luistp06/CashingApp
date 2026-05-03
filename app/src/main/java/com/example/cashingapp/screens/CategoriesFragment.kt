package com.example.cashingapp.screens

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        adapter = CategoryAdapter(
            onEditar = { category -> showEditDialog(category) },
            onEliminar = { category -> viewModel.eliminar(category) }
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_categorias)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewModel.categorias.observe(viewLifecycleOwner) { list ->
            adapter.actualizarLista(list)
        }

        view.findViewById<FloatingActionButton>(R.id.fab_añadir_categoria).setOnClickListener {
            showAddDialog()
        }
    }

    private fun showAddDialog() {
        val colors = listOf(
            "#7C3AED", "#E53E3E", "#38A169", "#3182CE",
            "#DD6B20", "#D69E2E", "#00B5D8", "#ED64A6"
        )
        var selectedColor = colors[0]

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_nueva_categoria, null)

        val etName = dialogView.findViewById<EditText>(R.id.et_nombre_categoria)
        val etIcon = dialogView.findViewById<EditText>(R.id.et_icono_categoria)
        val colorContainer = dialogView.findViewById<LinearLayout>(R.id.contenedor_colores)

        // Create a circle for each color
        colors.forEach { color ->
            val circle = View(requireContext())
            val size = 80
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(8, 0, 8, 0)
            circle.layoutParams = params

            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(Color.parseColor(color))
            circle.background = drawable

            circle.setOnClickListener {
                selectedColor = color
                // Highlight selected color with white border
                for (i in 0 until colorContainer.childCount) {
                    val v = colorContainer.getChildAt(i)
                    val d = v.background as GradientDrawable
                    d.setStroke(if (colors[i] == selectedColor) 6 else 0, Color.WHITE)
                }
            }
            colorContainer.addView(circle)
        }

        AlertDialog.Builder(requireContext(), R.style.Theme_CashingApp)

            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = etName.text.toString()
                val icon = etIcon.text.toString().ifEmpty { "📁" }
                if (name.isNotEmpty()) {
                    viewModel.insertar(Category(nombre = name, icono = icon, color = selectedColor))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDialog(category: Category) {
        val colors = listOf(
            "#7C3AED", "#E53E3E", "#38A169", "#3182CE",
            "#DD6B20", "#D69E2E", "#00B5D8", "#ED64A6"
        )
        var selectedColor = category.color

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_nueva_categoria, null)

        val etName = dialogView.findViewById<EditText>(R.id.et_nombre_categoria)
        val etIcon = dialogView.findViewById<EditText>(R.id.et_icono_categoria)
        val colorContainer = dialogView.findViewById<LinearLayout>(R.id.contenedor_colores)

        // Precargar los datos actuales de la categoría
        etName.setText(category.nombre)
        etIcon.setText(category.icono)

        // Create a circle for each color
        colors.forEach { color ->
            val circle = View(requireContext())
            val size = 80
            val params = LinearLayout.LayoutParams(size, size)
            params.setMargins(8, 0, 8, 0)
            circle.layoutParams = params

            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.OVAL
            drawable.setColor(Color.parseColor(color))
            // Marcar el color actual de la categoría
            if (color == selectedColor) {
                drawable.setStroke(6, Color.WHITE)
            }
            circle.background = drawable

            circle.setOnClickListener {
                selectedColor = color
                for (i in 0 until colorContainer.childCount) {
                    val v = colorContainer.getChildAt(i)
                    val d = v.background as GradientDrawable
                    d.setStroke(if (colors[i] == selectedColor) 6 else 0, Color.WHITE)
                }
            }
            colorContainer.addView(circle)
        }

        AlertDialog.Builder(requireContext(), R.style.Theme_CashingApp)
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = etName.text.toString()
                val icon = etIcon.text.toString().ifEmpty { "📁" }
                if (name.isNotEmpty()) {
                    viewModel.actualizar(category.copy(nombre = name, icono = icon, color = selectedColor))
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}