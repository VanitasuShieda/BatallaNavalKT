package com.proyectofinal.batallanavalkt.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Player
import java.io.File

//dialog que muestra los resultados, quién ganó, tiene un itemType porque también lo puede llamar la CPU
class DialogResults (context: Context, private val player1: Player, private val player2: Player, private val itemType: Int): DialogFragment() {
    //interface de listener para la info que se recupera del dialog


    private lateinit var listener: DialogResultsListener
    interface DialogResultsListener{
        fun applyDialogResults(res: String)
    }

    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            var mReference: StorageReference
            var mStorage: FirebaseStorage
            mStorage = FirebaseStorage.getInstance()
            mReference = mStorage.reference

             var bitmap: Bitmap? = null
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //Para indicar si es una victoria o derrota para el player1
            var player1wins: Boolean = false
            //vínculo con el layout results_dialog.xml
            val binding = inflater.inflate(R.layout.results_dialog, null)
            val monster1 = binding.findViewById<ImageView>(R.id.monster1_image)
            val monster2 = binding.findViewById<ImageView>(R.id.monster2_image)
            val player1Txt = binding.findViewById<TextView>(R.id.player1_textView)
            val player2Txt = binding.findViewById<TextView>(R.id.player2_textView)
            val msgResults1 = binding.findViewById<TextView>(R.id.msgResults1_textView)
            val msgResults2 = binding.findViewById<TextView>(R.id.msgResults2_textView)
            player1Txt.text=player1.getNickname()
            player2Txt.text=player2.getNickname()


            val imgRef = mReference.child("images/${player2.getUsermail()}")
            val localfile2 = File.createTempFile("tempImg", "jpg")
            imgRef.getFile(localfile2).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localfile2.absolutePath)
                val options = RequestOptions()
                options.centerCrop().fitCenter()
                Glide.with(this@DialogResults).load(bitmap).apply(options)
                    .into(monster2)
            }


            val imgRef2 = mReference.child("images/${player1.getUsermail()}")
            val localfile = File.createTempFile("tempImg", "jpg")
            imgRef2.getFile(localfile).addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                val options = RequestOptions()
                options.centerCrop().fitCenter()
                Glide.with(this@DialogResults).load(bitmap).apply(options)
                    .into(monster1)
            }


            if(itemType==0){//1 gana
                    msgResults1.text="FELICIDADES\t 🤗"
                    msgResults2.text="HAS GANADO"
                    player1wins=true
            }else if(itemType==1){//2 pierde
                    msgResults1.text="LASTIMA\t 😖"
                    msgResults2.text="HAS PERDIDO"
                    player1wins=false
            } else if(itemType==3){// EMPATE
                msgResults1.text="S UN EMPATEO\t 😖"
                msgResults2.text="ES UN EMPATEO"
                player1wins=false
            }
            //Botones del dialog
            builder.setView(binding)
                .setPositiveButton("Terminar",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.applyDialogResults("Terminar")
                        dialog.dismiss()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    //para funcionamiento del listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as DialogResultsListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implementDialogSelectMonsterListener"))
        }
    }
}