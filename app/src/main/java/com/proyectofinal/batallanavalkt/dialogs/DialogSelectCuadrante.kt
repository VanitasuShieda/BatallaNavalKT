package com.proyectofinal.batallanavalkt.dialogs

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.AdaptadorCuadrante
import com.proyectofinal.batallanavalkt.models.Player

class DialogSelectCuadrante(context: Context, private val player: Player) : DialogFragment() {

    private lateinit var listener: setapplyAttackListener
    private var impact = false
    private var choise = -1
    private var pos = 0


    interface setapplyAttackListener {
        fun applyAttack(player: Player, impact: Boolean)
    }



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val binding = inflater.inflate(R.layout.dialog_selectcuadrante, null)

            val adaptador = AdaptadorCuadrante(player, 1) {
                onItemSelect(it, binding)
            }
            val recview = binding.findViewById<RecyclerView>(R.id.recView_mytab)
            recview.layoutManager = GridLayoutManager(context, 10)
            recview.adapter = adaptador

            //Botones del dialog
            builder.setView(binding).setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, id ->

                    player.getMyTablero()[pos].isenable = false
                    impact = player.getMyTablero()[pos].isnave
                    player.getMyTablero()[pos].isImpact = impact
                    player.getMyTablero()[pos].isImpactMar = !impact
                    if(impact){
                        player.setusetable(player.getusetable()-1)
                    }

                    listener.applyAttack(player, impact)
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }//en on create

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog!!.window!!.attributes
        params.width = ActionBar.LayoutParams.MATCH_PARENT
        params.height = ActionBar.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params as WindowManager.LayoutParams
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as setapplyAttackListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement setnaves")
            )
        }
    }



    fun onItemSelect(it: Int, binding: View) {
        val items: RecyclerView = binding.findViewById(R.id.recView_mytab)

        if (player.getMyTablero()[it].isenable) {
            if(items[it].background == null){
                for (i in 0 until items.size) {
                    items[i].background = null
                }
                items[it].setBackgroundResource(R.drawable.seleccionado)
                pos = it
                choise = 1
            }
        } else {
            pos = -1
            items[it].background = null
            choise = -1
        }

        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            if (choise != 1) {
                positiveButton.visibility = View.INVISIBLE
            } else {
                positiveButton.visibility = View.VISIBLE
            }

        }
    }

    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.visibility = View.GONE

        }
    }


}
