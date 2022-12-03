package com.proyectofinal.batallanavalkt.dialogs

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.MessageAdapter
import com.proyectofinal.batallanavalkt.databinding.DialogSelectnavesmultiplayerBinding
import com.proyectofinal.batallanavalkt.models.Gameplay
import com.proyectofinal.batallanavalkt.models.Message
import com.proyectofinal.batallanavalkt.models.Player
import com.proyectofinal.batallanavalkt.models.Tablero
import java.io.File


class DialogSelectNavesMultiplayer(context: Context, private val player: Player, private var gameId: String) : DialogFragment() {

    private lateinit var listener: selectNavesMultiplayerListener
    private var db = Firebase.firestore

    interface selectNavesMultiplayerListener {
        fun applyNavesMultiplayer(player: Player)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSelectnavesmultiplayerBinding.inflate(layoutInflater)

        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)
        val incrementPA = FieldValue.increment(5)
        val incrementB = FieldValue.increment(4)
        val incrementSB = FieldValue.increment(3)
        val incrementC = FieldValue.increment(2)
        val incrementL = FieldValue.increment(1)

        val decrementPA = FieldValue.increment(-5)
        val decrementB = FieldValue.increment(-4)
        val decrementSB = FieldValue.increment(-3)
        val decrementC = FieldValue.increment(-2)
        val decrementL = FieldValue.increment(-1)

        val uid = FirebaseAuth.getInstance().currentUser?.email
        val navesRef =
            FirebaseFirestore.getInstance().collection("games").document(gameId).collection("naves")
                .document(gameId)

        val database = FirebaseFirestore.getInstance()

        val ref2 = db.collection("games").document(gameId).collection("naves").document(gameId).get()
        ref2.addOnSuccessListener { document ->
            if (!document.exists()) {
                db.collection("games").document(gameId).collection("naves").document(gameId).set(
                    Tablero()
                )
            }
        }.addOnFailureListener { exeption ->
            println(exeption)
        }

        fun checknaves(){
//            when(player.getusetable()){
//                25 -> {
//                    if(player.getcantPA() < 5)
//                        binding.nave1mas.isEnabled = true
//                    if(player.getcantB() < 5)
//                        binding.nave2mas.isEnabled = true
//                    if(player.getcantSB() < 5)
//                        binding.nave3mas.isEnabled = true
//                    if(player.getcantC() < 5)
//                        binding.nave4mas.isEnabled = true
//                    if(player.getcantL() < 5)
//                        binding.nave5mas.isEnabled = true
//                }
//                26 -> {
//                    binding.nave1mas.isEnabled = false
//                    if(player.getcantB() < 5)
//                        binding.nave2mas.isEnabled = true
//                    if(player.getcantSB() < 5)
//                        binding.nave3mas.isEnabled = true
//                    if(player.getcantC() < 5)
//                        binding.nave4mas.isEnabled = true
//                    if(player.getcantL() < 5)
//                        binding.nave5mas.isEnabled = true
//                }
//                27 -> {
//                    binding.nave2mas.isEnabled = false
//                    if(player.getcantSB() < 5)
//                        binding.nave3mas.isEnabled = true
//                    if(player.getcantC() < 5)
//                        binding.nave4mas.isEnabled = true
//                    if(player.getcantL() < 5)
//                        binding.nave5mas.isEnabled = true
//                }
//                28 -> {
//                    binding.nave3mas.isEnabled = false
//                    if(player.getcantC() < 5)
//                        binding.nave4mas.isEnabled = true
//                    if(player.getcantL() < 5)
//                        binding.nave5mas.isEnabled = true
//                }
//                29 -> {
//                    binding.nave4mas.isEnabled = false
//                    if(player.getcantL() < 5)
//                        binding.nave5mas.isEnabled = true
//                }
//                30 -> {
//                    binding.nave1mas.isEnabled = false
//                    binding.nave2mas.isEnabled = false
//                    binding.nave3mas.isEnabled = false
//                    binding.nave4mas.isEnabled = false
//                    binding.nave5mas.isEnabled = false
//                }
//                else -> {
//                    binding.nave1mas.isEnabled = false
//                    binding.nave2mas.isEnabled = false
//                    binding.nave3mas.isEnabled = false
//                    binding.nave4mas.isEnabled = false
//                    binding.nave5mas.isEnabled = false
//                }
//            }
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
    //listener naves update
        db.collection("games").document(gameId).collection("naves").document(gameId)
            .addSnapshotListener { tablero, error ->
                if(error == null){
                    if(tablero?.exists() == true){
                        tablero.let{
                            player.setcantPA(tablero.getDouble("pa")?.toInt()!!)
                            player.setcantB(tablero.getDouble("b")?.toInt()!!)
                            player.setcantSB(tablero.getDouble("sb")?.toInt()!!)
                            player.setcantC(tablero.getDouble("c")?.toInt()!!)
                            player.setcantL(tablero.getDouble("l")?.toInt()!!)
                            player.setusetable(tablero.getDouble("usetable")?.toInt()!!)
                            binding.cantidadnave1.text = player.getcantPA().toString()
                            binding.cantidadnave2.text = player.getcantB().toString()
                            binding.cantidadnave3.text = player.getcantSB().toString()
                            binding.cantidadnave4.text = player.getcantC().toString()
                            binding.cantidadnave5.text = player.getcantL().toString()
                        }
                    }
                }
            }




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

            database.runTransaction { transaction ->
                val snapshot = transaction.get(navesRef)
                val PAcount = snapshot.getLong("pa")
                if (PAcount != null) {
                    if (PAcount >= 0) {
                        transaction.update(navesRef, "pa", decrement)
                        transaction.update(navesRef, "usetable", decrementPA)
                    }
                }
            }.addOnFailureListener {
                throw Exception(it.message)
            }

            checknaves()
            binding.cantidadnave1.text = player.getcantPA().toString()
        }

        binding.nave1mas.setOnClickListener {
            if(player.getcantPA() < 5) {
                player.setcantPA(player.getcantPA() + 1)
                player.setusetable(player.getusetable() + 5)

                database.runTransaction { transaction ->
                    val snapshot = transaction.get(navesRef)
                    val PAcount = snapshot.getLong("pa")
                    if (PAcount != null) {
                        if (PAcount >= 0) {
                            transaction.update(navesRef, "pa", increment)
                            transaction.update(navesRef, "usetable", incrementPA)
                        }
                    }
                }.addOnFailureListener {
                    throw Exception(it.message)
                }

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

            database.runTransaction { transaction ->
                val snapshot = transaction.get(navesRef)
                val PAcount = snapshot.getLong("b")
                if (PAcount != null) {
                    if (PAcount >= 0) {
                        transaction.update(navesRef, "b", decrement)
                        transaction.update(navesRef, "usetable", decrementB)
                    }
                }
            }.addOnFailureListener {
                throw Exception(it.message)
            }

            checknaves()
            binding.cantidadnave2.text = player.getcantB().toString()
        }

        binding.nave2mas.setOnClickListener {
            if(player.getcantB() < 5) {
                player.setcantB(player.getcantB() + 1)
                player.setusetable(player.getusetable() + 4)
                database.runTransaction { transaction ->
                    val snapshot = transaction.get(navesRef)
                    val PAcount = snapshot.getLong("b")
                    if (PAcount != null) {
                        if (PAcount >= 0) {
                            transaction.update(navesRef, "b", increment)
                            transaction.update(navesRef, "usetable", incrementB)
                        }
                    }
                }.addOnFailureListener {
                    throw Exception(it.message)
                }
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

            database.runTransaction { transaction ->
                val snapshot = transaction.get(navesRef)
                val PAcount = snapshot.getLong("sb")
                if (PAcount != null) {
                    if (PAcount >= 0) {
                        transaction.update(navesRef, "sb", decrement)
                        transaction.update(navesRef, "usetable", decrementSB)
                    }
                }
            }.addOnFailureListener {
                throw Exception(it.message)
            }
            checknaves()
            binding.cantidadnave3.text = player.getcantSB().toString()
        }

        binding.nave3mas.setOnClickListener {
            if(player.getcantSB() < 5) {
                player.setcantSB(player.getcantSB() + 1)
                player.setusetable(player.getusetable() + 3)
                database.runTransaction { transaction ->
                    val snapshot = transaction.get(navesRef)
                    val PAcount = snapshot.getLong("sb")
                    if (PAcount != null) {
                        if (PAcount >= 0) {
                            transaction.update(navesRef, "sb", increment)
                            transaction.update(navesRef, "usetable", incrementSB)
                        }
                    }
                }.addOnFailureListener {
                    throw Exception(it.message)
                }
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
            database.runTransaction { transaction ->
                val snapshot = transaction.get(navesRef)
                val PAcount = snapshot.getLong("c")
                if (PAcount != null) {
                    if (PAcount >= 0) {
                        transaction.update(navesRef, "c", decrement)
                        transaction.update(navesRef, "usetable", decrementC)
                    }
                }
            }.addOnFailureListener {
                throw Exception(it.message)
            }
            checknaves()
            binding.cantidadnave4.text = player.getcantC().toString()
        }

        binding.nave4mas.setOnClickListener {
            if(player.getcantC() < 5) {
                player.setcantC(player.getcantC() + 1)
                player.setusetable(player.getusetable() + 2)
                database.runTransaction { transaction ->
                    val snapshot = transaction.get(navesRef)
                    val PAcount = snapshot.getLong("c")
                    if (PAcount != null) {
                        if (PAcount >= 0) {
                            transaction.update(navesRef, "c", increment)
                            transaction.update(navesRef, "usetable", incrementC)
                        }
                    }
                }.addOnFailureListener {
                    throw Exception(it.message)
                }
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
            database.runTransaction { transaction ->
                val snapshot = transaction.get(navesRef)
                val PAcount = snapshot.getLong("l")
                if (PAcount != null) {
                    if (PAcount >= 0) {
                        transaction.update(navesRef, "l", decrement)
                        transaction.update(navesRef, "usetable", decrementL)
                    }
                }
            }.addOnFailureListener {
                throw Exception(it.message)
            }
            checknaves()
            binding.cantidadnave5.text = player.getcantL().toString()
        }

        binding.nave5mas.setOnClickListener {
            if(player.getcantL() < 5) {
                player.setcantL(player.getcantL() + 1)
                player.setusetable(player.getusetable() + 1)
                database.runTransaction { transaction ->
                    val snapshot = transaction.get(navesRef)
                    val PAcount = snapshot.getLong("l")
                    if (PAcount != null) {
                        if (PAcount >= 0) {
                            transaction.update(navesRef, "l", increment)
                            transaction.update(navesRef, "usetable", incrementL)
                        }
                    }
                }.addOnFailureListener {
                    throw Exception(it.message)
                }
            }

            when(player.getcantL()){
                1-> binding.nave5menos.isEnabled = true
                5-> binding.nave5mas.isEnabled = false
            }

            checknaves()
            binding.cantidadnave5.text = player.getcantL().toString()
        }
//Jugar siempre que haya almenos una nave


//chat
        binding.chatbtn.setOnClickListener {
            if (binding.chatgame.visibility == View.VISIBLE) {
                binding.chatgame.visibility = View.GONE
//                superBinding.tablero2.visibility = View.VISIBLE
            } else {
                binding.chatgame.visibility = View.VISIBLE
//                superBinding.tablero2.visibility = View.INVISIBLE
            }
        }

        binding.closechatbtn.setOnClickListener {
            if (binding.chatgame.visibility == View.VISIBLE) {
                binding.chatgame.visibility = View.GONE
//                superBinding.tablero2.visibility = View.VISIBLE
            } else {
                binding.chatgame.visibility = View.VISIBLE
//                superBinding.tablero2.visibility = View.INVISIBLE
            }
        }


//Comenzar partida
        binding.playgame.setOnClickListener {
            if(player.getusetable() != 0) {
                listener.applyNavesMultiplayer(player)
                dialog?.dismiss()
            }

        }

        binding.messagesRecylerView.layoutManager = LinearLayoutManager(context)
        binding.messagesRecylerView.adapter = MessageAdapter(player.getNickname())


        binding.sendMessageButton.setOnClickListener {
            sendMessage(binding.messageTextField.text.toString(), player.getNickname())
            binding.messageTextField.setText("")
        }
        val gameRef = db.collection("games").document(gameId)

        //creador de mensajes
        gameRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { messages ->
                val listMessages = messages.toObjects(Message::class.java)
                (binding.messagesRecylerView.adapter as MessageAdapter).setData(listMessages)
            }

        //listener de mensajes
        gameRef.collection("messages").orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener { messages, error ->
                if (error == null) {
                    messages?.let {
                        val listMessages = it.toObjects(Message::class.java)
                        (binding.messagesRecylerView.adapter as MessageAdapter).setData(
                            listMessages
                        )
                    }
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

    private fun sendMessage(msg: String, nick: String) {
        val message = Message(
            message = msg,
            from = nick
        )

        if(message.message.isNotEmpty())
            db.collection("games").document(gameId).collection("messages").document().set(message)


    }


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
            listener = context as selectNavesMultiplayerListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implementAnadirDialogListener")
            )
        }
    }





}//end dialog