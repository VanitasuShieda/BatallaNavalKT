package com.proyectofinal.batallanavalkt.dialogs

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.databinding.SelectNavesBinding
import com.proyectofinal.batallanavalkt.models.Player


class DialogSelectNaves(context: Context, private val player: Player) : DialogFragment() {

    private lateinit var listener: selectNavesListener
    interface selectNavesListener {
        fun applyNaves(player: Player)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val binding = SelectNavesBinding.inflate(layoutInflater)

        fun checknaves(){
            when(player.getusetable()){
                25 -> {
                    if(player.getcantPA() < 5)
                        binding.nave1mas.isEnabled = true
                    if(player.getcantB() < 5)
                        binding.nave2mas.isEnabled = true
                    if(player.getcantSB() < 5)
                        binding.nave3mas.isEnabled = true
                    if(player.getcantC() < 5)
                        binding.nave4mas.isEnabled = true
                    if(player.getcantL() < 5)
                        binding.nave5mas.isEnabled = true
                }
                26 -> {
                    binding.nave1mas.isEnabled = false
                    if(player.getcantB() < 5)
                        binding.nave2mas.isEnabled = true
                    if(player.getcantSB() < 5)
                        binding.nave3mas.isEnabled = true
                    if(player.getcantC() < 5)
                        binding.nave4mas.isEnabled = true
                    if(player.getcantL() < 5)
                        binding.nave5mas.isEnabled = true
                }
                27 -> {
                    binding.nave2mas.isEnabled = false
                    if(player.getcantSB() < 5)
                        binding.nave3mas.isEnabled = true
                    if(player.getcantC() < 5)
                        binding.nave4mas.isEnabled = true
                    if(player.getcantL() < 5)
                        binding.nave5mas.isEnabled = true
                }
                28 -> {
                    binding.nave3mas.isEnabled = false
                    if(player.getcantC() < 5)
                        binding.nave4mas.isEnabled = true
                    if(player.getcantL() < 5)
                        binding.nave5mas.isEnabled = true
                }
                29 -> {
                    binding.nave4mas.isEnabled = false
                    if(player.getcantL() < 5)
                        binding.nave5mas.isEnabled = true
                }
                30 -> {
                    binding.nave1mas.isEnabled = false
                    binding.nave2mas.isEnabled = false
                    binding.nave3mas.isEnabled = false
                    binding.nave4mas.isEnabled = false
                    binding.nave5mas.isEnabled = false
                }
            }
        }

        if(player.getcantPA() == 0)
            binding.nave1menos.isEnabled = false
        if(player.getcantB() == 0)
            binding.nave2menos.isEnabled = false
        if(player.getcantSB() == 0)
            binding.nave3menos.isEnabled = false
        if(player.getcantC() == 0)
            binding.nave4menos.isEnabled = false
        if(player.getcantL() == 0)
            binding.nave5menos.isEnabled = false

//Botones mas y menos
        //nave 1 Portaviones
        binding.nave1menos.setOnClickListener {
            when(player.getcantPA()){
                1 -> {
                    player.setcantPA(player.getcantPA() - 1)
                    binding.nave1menos.isEnabled = false
                }
                5 -> {
                    player.setcantPA(player.getcantPA() - 1)
                    binding.nave1mas.isEnabled = true
                }
                else -> player.setcantPA(player.getcantPA() - 1)
            }
            player.setusetable(player.getusetable() - 5)
            checknaves()
            binding.cantidadnave1.text = player.getcantPA().toString()
        }

        binding.nave1mas.setOnClickListener {
            if(player.getcantPA() < 5) {
                player.setcantPA(player.getcantPA() + 1)
                player.setusetable(player.getusetable() + 5)
            }

            when(player.getcantPA()){
                1-> binding.nave1menos.isEnabled = true
                5-> binding.nave1mas.isEnabled = false
            }


            checknaves()
            binding.cantidadnave1.text = player.getcantPA().toString()

        }
        //nave 2 Buque
        binding.nave2menos.setOnClickListener {
            when(player.getcantB()){
                1 -> {
                    player.setcantB(player.getcantB() - 1)
                    binding.nave2menos.isEnabled = false
                }
                5 -> {
                    player.setcantB(player.getcantB() - 1)
                    binding.nave2mas.isEnabled = true
                }
                else -> player.setcantB(player.getcantB() - 1)
            }

            player.setusetable(player.getusetable() - 4)
            checknaves()
            binding.cantidadnave2.text = player.getcantB().toString()
        }

        binding.nave2mas.setOnClickListener {
            if(player.getcantB() < 5) {
                player.setcantB(player.getcantB() + 1)
                player.setusetable(player.getusetable() + 4)
            }

            when(player.getcantB()){
                1-> binding.nave2menos.isEnabled = true
                5-> binding.nave2mas.isEnabled = false
            }

            checknaves()

            binding.cantidadnave2.text = player.getcantB().toString()
        }
        //nave 3 Submarino
        binding.nave3menos.setOnClickListener {
            when(player.getcantSB()){
                1 -> {
                    player.setcantSB(player.getcantSB() - 1)
                    binding.nave3menos.isEnabled = false
                }
                5 -> {
                    player.setcantSB(player.getcantSB() - 1)
                    binding.nave3mas.isEnabled = true
                }
                else -> player.setcantSB(player.getcantSB() - 1)
            }

            player.setusetable(player.getusetable() - 3)
            checknaves()
            binding.cantidadnave3.text = player.getcantSB().toString()
        }

        binding.nave3mas.setOnClickListener {
            if(player.getcantSB() < 5) {
                player.setcantSB(player.getcantSB() + 1)
                player.setusetable(player.getusetable() + 3)
            }

            when(player.getcantSB()){
                1-> binding.nave3menos.isEnabled = true
                5-> binding.nave3mas.isEnabled = false
            }

            checknaves()

            binding.cantidadnave3.text = player.getcantSB().toString()
        }
        //Crusero
        binding.nave4menos.setOnClickListener {
            when(player.getcantC()){
                1 -> {
                    player.setcantC(player.getcantC() - 1)
                    binding.nave4menos.isEnabled = false
                }
                5 -> {
                    player.setcantC(player.getcantC() - 1)
                    binding.nave4mas.isEnabled = true
                }
                else -> player.setcantC(player.getcantC() - 1)
            }

            player.setusetable(player.getusetable() - 2)
            checknaves()
            binding.cantidadnave4.text = player.getcantC().toString()
        }

        binding.nave4mas.setOnClickListener {
            if(player.getcantC() < 5) {
                player.setcantC(player.getcantC() + 1)
                player.setusetable(player.getusetable() + 2)
            }

            when(player.getcantC()){
                1-> binding.nave4menos.isEnabled = true
                5-> binding.nave4mas.isEnabled = false
            }

            checknaves()
            binding.cantidadnave4.text = player.getcantC().toString()
        }
        //Lancha
        binding.nave5menos.setOnClickListener {
            when(player.getcantL()){
                1 -> {
                    player.setcantL(player.getcantL() - 1)
                    binding.nave5menos.isEnabled = false
                }
                5 -> {
                    player.setcantL(player.getcantL() - 1)
                    binding.nave5mas.isEnabled = true
                }
                else -> player.setcantL(player.getcantL() - 1)
            }

            player.setusetable(player.getusetable() - 1)
            checknaves()
            binding.cantidadnave5.text = player.getcantL().toString()
        }

        binding.nave5mas.setOnClickListener {
            if(player.getcantL() < 5) {
                player.setcantL(player.getcantL() + 1)
                player.setusetable(player.getusetable() + 1)
            }

            when(player.getcantL()){
                1-> binding.nave5menos.isEnabled = true
                5-> binding.nave5mas.isEnabled = false
            }

            checknaves()
            binding.cantidadnave5.text = player.getcantL().toString()
        }
//Jugar siempre que haya almenos una nave


//reiniciar
        binding.resetopcion.setOnClickListener {
            player.setcantPA(0)
            player.setcantSB(0)
            player.setcantB(0)
            player.setcantC(0)
            player.setcantL(0)

            binding.cantidadnave1.text = player.getcantPA().toString()
            binding.cantidadnave2.text = player.getcantB().toString()
            binding.cantidadnave3.text = player.getcantSB().toString()
            binding.cantidadnave4.text = player.getcantC().toString()
            binding.cantidadnave5.text = player.getcantL().toString()

            player.setusetable(0)
        }


//Comenzar partida
        binding.playgame.setOnClickListener {
            if(player.getusetable() != 0) {
                listener.applyNaves(player)
                dialog?.dismiss()
            }

        }



        val dialog = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ShapeAppearance_Capsule
        ).apply {
            setView(binding.root)
            setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                    dismiss()
                    true
                } else false
            }
        }.create()

        return dialog

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
            listener = context as selectNavesListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implementAnadirDialogListener")
            )
        }
    }





}//end dialog