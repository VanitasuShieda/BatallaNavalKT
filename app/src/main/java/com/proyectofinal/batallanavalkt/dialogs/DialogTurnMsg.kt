package com.proyectofinal.batallanavalkt.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Player

//muestra un mensaje indicando de quien es el turno
class DialogTurnMsg(context: Context, private val player: Player): DialogFragment() {

    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //vínculo con el layout turnmsg_dialog.xml
            val binding = inflater.inflate(R.layout.turnmsg_dialog, null)
            val msgTurn=binding.findViewById<TextView>(R.id.msgTurn_textView)
            msgTurn.text="Turno de ${player.getNickname()}"
            Handler(Looper.getMainLooper()).postDelayed({
                val d = dialog as AlertDialog?
                d?.dismiss()
            }, 1800)
            //botones del dialog (No hay botones)
            builder.setView(binding)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}