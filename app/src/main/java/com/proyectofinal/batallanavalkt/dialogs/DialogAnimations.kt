package com.proyectofinal.batallanavalkt.dialogs

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.proyectofinal.batallanavalkt.R

class DialogAnimations(context: Context, private var itemType:Int, private val nombre: String) : DialogFragment() {
    private lateinit var bgVideo: VideoView;
    private lateinit var uri: Uri

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        return  activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val binding = inflater.inflate(R.layout.dialog_animations,null)

            bgVideo = binding.findViewById(R.id.bganimation)


            uri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.preparecannon)


            bgVideo.setVideoURI(uri)

            binding.findViewById<VideoView>(R.id.bganimation)

            builder.setView(binding).setTitle("Turno de $nombre").create()


        } ?:throw IllegalStateException("Animation Fail")
    }



    override fun onStart() {
        super.onStart()

        bgVideo.start()
        val d = dialog as AlertDialog?
        if (d != null){

            bgVideo.setOnCompletionListener {
                if(itemType == 3){
                    itemType = 13
                    uri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.prepareimpact)
                    bgVideo.setVideoURI(uri)
                    bgVideo.start()
                }else if(itemType == 13){
                    itemType = 0
                    uri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.failatack)
                    bgVideo.setVideoURI(uri)
                    bgVideo.start()
                }
                else if(itemType == 2){
                    itemType = 12
                    uri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.prepareimpact)
                    bgVideo.setVideoURI(uri)
                    bgVideo.start()
                }else if(itemType == 12){
                    itemType = 0
                    uri = Uri.parse("android.resource://" + context?.packageName + "/" + R.raw.impact)
                    bgVideo.setVideoURI(uri)
                    bgVideo.start()
                }else{
                    d?.dismiss()
                }

            }
        }
    }


}