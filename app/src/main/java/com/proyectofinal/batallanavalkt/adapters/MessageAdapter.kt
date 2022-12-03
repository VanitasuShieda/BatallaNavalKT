package com.proyectofinal.batallanavalkt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Message

class MessageAdapter(private val user: String): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private var messages: List<Message> = emptyList()

    fun setData(list: List<Message>){
        messages = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_msg,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]




        if(user == message.from){
            if(message.message != "TURNO") {
                holder.itemView.findViewById<ConstraintLayout>(R.id.myMessageLayout).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<ConstraintLayout>(R.id.otherMessageLayout).visibility =
                    View.GONE
                holder.itemView.findViewById<TextView>(R.id.myMessageTextView).text =
                    message.message
                holder.itemView.findViewById<TextView>(R.id.fromuser).text = message.from
            }else{
                holder.itemView.findViewById<ConstraintLayout>(R.id.myMessageLayout).visibility =
                    View.GONE
                holder.itemView.findViewById<ConstraintLayout>(R.id.otherMessageLayout).visibility =
                    View.GONE
            }
        } else {
            if(message.message != "TURNO"){
                holder.itemView.findViewById<ConstraintLayout>(R.id.myMessageLayout).visibility = View.GONE
                holder.itemView.findViewById<ConstraintLayout>(R.id.otherMessageLayout).visibility = View.VISIBLE
                holder.itemView.findViewById<TextView>(R.id.othersMessageTextView).text = message.message
                holder.itemView.findViewById<TextView>(R.id.fromuser2).text = message.from
            }else{
                holder.itemView.findViewById<ConstraintLayout>(R.id.myMessageLayout).visibility = View.GONE
                holder.itemView.findViewById<ConstraintLayout>(R.id.otherMessageLayout).visibility = View.GONE
            }
        }


    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}