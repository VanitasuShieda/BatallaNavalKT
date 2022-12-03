package com.proyectofinal.batallanavalkt.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.proyectofinal.batallanavalkt.R

class DialogMsg(private  val msg: String) : DialogFragment(){
    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //vínculo con el layout turnmsg_dialog.xml
            val binding = inflater.inflate(R.layout.turnmsg_dialog, null)
            val msgTurn=binding.findViewById<TextView>(R.id.msgTurn_textView)
            msgTurn.text = msg
            //botones del dialog (No hay botones)
            builder.setView(binding)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}