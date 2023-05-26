package com.example.padle_match.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.R
import com.example.padle_match.entities.Club


class ClubAdapter (
    private var clubList: MutableList<Club>,
    val context: Context,
    val onItemCLick: (Int) -> Unit
    ): RecyclerView.Adapter<ClubAdapter.ClubHolder >() {

    class ClubHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setNombre(name: String) {
            val txt: TextView = view.findViewById(R.id.club_title)
            txt.text = name
        }

        fun setPartido(name: String) {
            val txt: TextView = view.findViewById(R.id.club_partido)
            txt.text = name
        }
        fun setDireccion(name: String) {
            val txt: TextView = view.findViewById(R.id.club_direccion)
            txt.text = name
        }
        fun setTelefono(name: String) {
            val txt: TextView = view.findViewById(R.id.club_telefono)
            txt.text = name
        }

        fun getCardLayout(): CardView {
            return view.findViewById(R.id.item_club)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.club_item, parent, false)

        return (ClubHolder(view))
    }

    override fun getItemCount(): Int {
        return clubList.size
    }

    override fun onBindViewHolder(holder: ClubHolder, position: Int) {

        holder.setNombre(clubList[position].nombre)
        holder.setPartido(clubList[position].partido)
        holder.setDireccion(clubList[position].domicilio)
        holder.setTelefono(clubList[position].telefonos)
        holder.getCardLayout().setOnClickListener {
            onItemCLick(position)
        }
    }
}








