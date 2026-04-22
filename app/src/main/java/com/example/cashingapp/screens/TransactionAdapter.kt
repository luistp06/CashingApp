package com.example.cashingapp.screens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cashingapp.R
import com.example.cashingapp.model.Category
import com.example.cashingapp.model.Transaction

class TransactionAdapter(
    private val lista: MutableList<Transaction> = mutableListOf(),
    private var listaCategorias: List<Category> = emptyList(),
    private val onEditar: (Transaction) -> Unit,
    private val onEliminar: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvIcono: TextView = itemView.findViewById(R.id.tv_icono)
        val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        val tvCategoriaFecha: TextView = itemView.findViewById(R.id.tv_categoria_fecha)
        val tvImporte: TextView = itemView.findViewById(R.id.tv_importe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_movimiento, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = lista[position]

        // Buscar la categoría del movimiento para mostrar su icono y nombre
        val categoria = listaCategorias.find { it.id == item.categoriaId }

        holder.tvIcono.text = categoria?.icono ?: "📁"
        holder.tvNombre.text = categoria?.nombre ?: "Sin categoría"
        holder.tvCategoriaFecha.text = item.fecha

        holder.tvImporte.text = if (item.tipo == "INGRESO") {
            "+${item.importe} €"
        } else {
            "-${item.importe} €"
        }

        holder.tvImporte.setTextColor(
            if (item.tipo == "INGRESO")
                holder.itemView.context.getColor(R.color.ingreso)
            else
                holder.itemView.context.getColor(R.color.gasto)
        )

        holder.itemView.setOnClickListener { onEditar(item) }
        holder.itemView.setOnLongClickListener {
            onEliminar(item)
            true
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Transaction>) {
        lista.clear()
        lista.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    // Actualiza las categorías y redibuja la lista
    fun actualizarCategorias(nuevasCategorias: List<Category>) {
        listaCategorias = nuevasCategorias
        notifyDataSetChanged()
    }
}