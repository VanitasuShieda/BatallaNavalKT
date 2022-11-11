package com.proyectofinal.batallanavalkt.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.models.Player

//dialog en el que se pide elegir entre piedra, papael o tijera
class DialogDecideTurn (context: Context, private val player: Player): DialogFragment() {
    //interface de listener para la info que se recupera del dialog
    private lateinit var listener: DialogDecideTurnListener
    interface DialogDecideTurnListener{
        fun applyDecideTurn(player: Player)
    }
    //Variables para el efecto de sonido
    private val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private  val sp: SoundPool =
        SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()
    private val pop: Int = sp.load(context,R.raw.pop,1)
    override fun onCreateDialog( savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //vínculo con el layout decideturn_dialog.xml
            val binding = inflater.inflate(R.layout.decideturn_dialog, null)
            val paper_image =binding.findViewById<ImageView>(R.id.paper_image)
            val scissors_image =binding.findViewById<ImageView>(R.id.scissors_image)
            val rock_image =binding.findViewById<ImageView>(R.id.rock_image)
            //Se asigna el evento click que selecciona una imagen
            paper_image.setOnClickListener { onItemSelect(paper_image,scissors_image,rock_image) }
            scissors_image.setOnClickListener { onItemSelect(scissors_image,paper_image,rock_image) }
            rock_image.setOnClickListener { onItemSelect(rock_image,paper_image,scissors_image) }
            //Botones del dialog
            builder.setView(binding)
                .setTitle("¿Piedra, papel o tijera?")
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.applyDecideTurn(player)
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    //Al iniciar el dialog se deshabilita el botón 'ok'
    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.visibility= View.INVISIBLE
        }
    }
    //para funcionamiento del listener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as DialogDecideTurnListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException((context.toString() +
                    " must implementDialogSelectMonsterListener"))
        }
    }
    fun onItemSelect(button: ImageView, buttonNotSelected1: ImageView, buttonNotSelected2: ImageView){
        //Se reproduce el sonido
        sp.play(pop, 1f, 1f, 1, 0, 1f)
        if(button.background==null){
            //deselecciona los otros botones
            buttonNotSelected1.background = null
            buttonNotSelected2.background = null
            player.setPpt(-1)
            //selecciona el botón al que se le hizo click
            button.setBackgroundResource(R.drawable.seleccionado)
            when(button.id){
                R.id.rock_image -> player.setPpt(0)
                R.id.paper_image -> player.setPpt(1)
                R.id.scissors_image -> player.setPpt(2)
            }
        }//quita la selección si se vuelve a hacer click
        else{
            button.background = null
            player.setPpt(-1)
        }
        //Sí hay una elección se habilita el botón 'ok'
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            if(player.getPpt()==-1){
                positiveButton.visibility=View.INVISIBLE
            }else{positiveButton.visibility=View.VISIBLE}
        }
    }
}