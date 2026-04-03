package com.example.cashingapp.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashingapp.R
import com.example.cashingapp.model.Category


// onEditar: se ejecuta cuando el usuario pulsa una categoría
// onEliminar: se ejecuta cuando el usuario pulsa el botón eliminar

class CategoryAdapter(
    private val lista: MutableList<Category> = mutableListOf(),
    private val onEditar: (Category) -> Unit,
    private val onEliminar: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {


    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcono: TextView = itemView.findViewById(R.id.tv_icono_categoria)
        val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre_categoria)
        val tvEliminar: TextView = itemView.findViewById(R.id.tv_eliminar_categoria)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_categoria, parent, false)
        return VH(view)
    }


    // onBindViewHolder
    // - Pinta cada fila con los datos de la categoría

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]

        holder.tvIcono.text = item.icono
        holder.tvNombre.text = item.nombre

        // Pulsación en la fila → editar
        holder.itemView.setOnClickListener {
            onEditar(item)
        }

        // Pulsación en el icono de eliminar → eliminar
        holder.tvEliminar.setOnClickListener {
            onEliminar(item)
        }
    }

    override fun getItemCount(): Int = lista.size

    // actualizarLista
    // - Actualiza los datos cuando cambia la base de datos

    fun actualizarLista(nuevaLista: List<Category>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }
}