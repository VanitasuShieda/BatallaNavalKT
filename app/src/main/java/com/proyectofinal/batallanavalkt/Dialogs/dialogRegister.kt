package com.proyectofinal.batallanavalkt.Dialogs

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.databinding.DialogRegisterBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.BuildConfig



class dialogRegister : DialogFragment() {

    private lateinit var listener: dialgRegisterListener
    interface dialgRegisterListener {
        fun applyReg(nick: String, email: String, pass: String, theUri: Uri)
    }

    private val pkN = BuildConfig.APPLICATION_ID
    private var uri = Uri.parse("android.resource://$pkN/${R.drawable.user}")
    private var db = Firebase.firestore

    private lateinit var mStorage: FirebaseStorage
    private lateinit var mReference: StorageReference



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val binding =  DialogRegisterBinding.inflate(layoutInflater)
        var theUri: Uri?=null

        val getContent = registerForActivityResult(ActivityResultContracts.GetContent())  { uri2: Uri? ->
            binding.imgreg.setImageBitmap(null)
            binding.imgreg.setImageURI(null)
            binding.imgreg.setImageURI(uri2)
            if (uri2 != null) {
                theUri=uri2
            }
        }

        binding.imgreg.setOnClickListener{
            getContent.launch("image/*")
        }
        binding.imgselectreg.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btncheck.setOnClickListener {
            val newemail= binding.emailcapture.text.toString()
            val newpas = binding.passcapture1.text.toString()
            val newpas2 = binding.passcapture2.text.toString()
            val newnick= binding.nickcapture.text.toString()
            val checkpass2 = binding.checkpass2
            val checkemail = binding.checkemail
            val alertemail = binding.alertemail
            val alertpass2 = binding.alerpass
            val alertavatar = binding.alertavatar
            val checkavatar = binding.checkavatar
            var exist = false

            //comprobacion
            if(newemail != ""){
                val refnick = db.collection("users").get()
                refnick.addOnSuccessListener { documentos ->
                    for(user in documentos){
                        if(newemail.lowercase() == user.id){
                            exist=true
                            break
                        }
                    }
                    if(exist){
                        alertemail.text = "El correo ya existe"
                        alertemail.visibility = View.VISIBLE
                    }else{
                        alertemail.visibility = View.GONE
                        checkemail.visibility = View.VISIBLE
                    }
                }.addOnFailureListener{ exeption ->
                    println(exeption)
                }
            }else if(newemail == ""){
                alertemail.text = "El Correo No puede estar en blanco"
                alertemail.visibility = View.VISIBLE
            }

            if(newpas == newpas2 && newpas != "" && newpas2 != ""){
                checkpass2.visibility = View.VISIBLE
                alertpass2.visibility = View.GONE
            }else{
                alertpass2.visibility = View.VISIBLE
                if(newpas == ""){
                    alertpass2.text = "La contrasenia No puede estar Vacia"
                }else if(newpas2 == ""){
                    alertpass2.text = "Porfavor Repite La contrasenia"
                }
            }

            if(!(theUri != null)){
                alertavatar.visibility = View.VISIBLE
            }else{
                alertavatar.visibility = View.GONE
                checkavatar.visibility = View.VISIBLE
            }

            if(newnick != "" && newemail != "" && binding.checkpass2.isVisible && theUri != null){
                alertemail.visibility = View.GONE
                alertpass2.visibility = View.GONE
                binding.btnregister.visibility=View.VISIBLE
            }else{
                binding.btnregister.visibility=View.INVISIBLE
            }
        }

        binding.btnregister.setOnClickListener{
            val newnick= binding.nickcapture.text.toString()
            val newemail= binding.emailcapture.text.toString()
            val newpas = binding.passcapture1.text.toString()

            listener.applyReg(newnick,newemail,newpas, theUri!!)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as dialgRegisterListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implementAnadirDialogListener"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_register, container, false)
    }

}