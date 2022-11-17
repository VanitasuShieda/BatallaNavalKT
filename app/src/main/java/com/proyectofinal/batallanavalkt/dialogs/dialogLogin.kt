package com.proyectofinal.batallanavalkt.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.databinding.DialogLoginBinding

class dialogLogin : DialogFragment() {
    private lateinit var listener: dialogLoginListener


    interface dialogLoginListener {
        fun applyLogin(email: String, pass: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val binding =  DialogLoginBinding.inflate(layoutInflater)

            binding.loginlogin.setOnClickListener {
                val email = binding.loginEmailUser.text.toString()
                val pass = binding.loginpassword.text.toString()
                listener.applyLogin(email, pass)
                dialog?.dismiss()
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

    }

    override fun onResume() {
        super.onResume()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as dialogLoginListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implementAnadirDialogListener")
            )
        }
    }




}