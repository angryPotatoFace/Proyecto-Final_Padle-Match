package com.example.padle_match.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.padle_match.entities.Tournament
import com.example.padle_match.R
import com.example.padle_match.entities.Categoria

class TournamentAdapter(var tournamentList: MutableList<Tournament> ): RecyclerView.Adapter<TournamentAdapter.TournamentHolder >()  {

    class TournamentHolder (v: View) : RecyclerView.ViewHolder(v) {
        private var view: View

        init {
            this.view = v
        }

        fun setTitle(name: String) {
            val txt: TextView = view.findViewById(R.id.tournament_title )
            txt.text = name
        }

        fun setCategory(name: Categoria) {
            val txt: TextView = view.findViewById(R.id.tournament_category )
            txt.text = name.toString()
        }

        fun setDate(name: String) {
            val txt: TextView = view.findViewById(R.id.tournament_date )
            txt.text = name
        }

        fun setHour(name: String) {
            val txt: TextView = view.findViewById(R.id.tournament_hour)
            txt.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TournamentHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.tournament_item,parent,false)
        return (TournamentHolder(view))
    }

    override fun getItemCount(): Int {
        return tournamentList.size
    }

    override fun onBindViewHolder(holder: TournamentHolder, position: Int) {
        holder.setTitle(tournamentList[position].titulo )
        holder.setCategory(tournamentList[position].categoría )
        holder.setDate(tournamentList[position].fecha )
        holder.setHour(tournamentList[position].hora )
    }
}