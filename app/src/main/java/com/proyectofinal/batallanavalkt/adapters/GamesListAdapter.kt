package com.proyectofinal.batallanavalkt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Game

class GamesListAdapter(private val listaGames: MutableList<Game>,
                       private val gameClick: (Int) -> Unit):
    RecyclerView.Adapter<GamesListAdapter.GamesViewHolder>()  {

    var games: List<Game> = emptyList()

    fun setData(list: List<Game>){
        games = list
        notifyDataSetChanged()
    }

    class GamesViewHolder(item: View): RecyclerView.ViewHolder(item){
        val gameNick = item.findViewById(R.id.chatNameText) as TextView
        val statusgame = item.findViewById(R.id.usersTextView) as TextView
        fun bindGame(game: Game){
            gameNick.text=game.name
            statusgame.text=game.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val item = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_users, parent, false) as androidx.constraintlayout.widget.ConstraintLayout
        return GamesViewHolder(item)
    }

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        val game =  listaGames[position]
        holder.bindGame(game)
        holder.itemView.setOnClickListener{gameClick(position)}
    }

    override fun getItemCount(): Int {
        return listaGames.size
    }




}