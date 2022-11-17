package com.proyectofinal.batallanavalkt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Cuadrante
import com.proyectofinal.batallanavalkt.models.Player

class AdaptadorCuadrante(private val player: Player, private val clickListener: (Int) -> Unit):
    RecyclerView.Adapter<AdaptadorCuadrante.CuadranteViewHolder>() {
        class CuadranteViewHolder(item: View): RecyclerView.ViewHolder(item){
        //Se crea el RecyclerView almacenando los items de la lista en el layout
            val imagen = item.findViewById(R.id.itemmar) as ImageView
            fun bindMonster(cuadrante: Cuadrante){
                if(cuadrante.isImpact) {
                    imagen.setImageResource(R.drawable.impact)
                }
                else if(cuadrante.isImpactMar) {
                    imagen.setImageResource(R.drawable.impactmar)
                }
                else{
                    imagen.setImageResource(R.drawable.cuadrante)
                }
             }
    }

    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): CuadranteViewHolder {
        var item: View? = null

            item = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cuadrante, parent, false) as LinearLayout

        return CuadranteViewHolder(item!!)
    }
    //Para determinar el evento click de cada item
    override fun onBindViewHolder (holder: CuadranteViewHolder, position: Int) {
        val cuadra=player.getMyTablero()[position]
        holder.bindMonster(cuadra)
        holder.itemView.setOnClickListener{clickListener(position)

            if(cuadra.isenable)
            if(cuadra.isImpact) {
                cuadra.isImpactMar=true
                cuadra.isImpact=false
                cuadra.isImpactMar=false
            }
            else if(cuadra.isImpactMar) {
                cuadra.isImpactMar=false
                cuadra.isImpact=true
                cuadra.isImpactMar=false
            }
            else{
                cuadra.isImpactMar=false
                cuadra.isImpact=false
                cuadra.isImpactMar=true
            }

            holder.bindMonster(cuadra)
            println("Posicion" + position)
        }
    }

    override fun getItemCount() = player.getMyTablero().size

}