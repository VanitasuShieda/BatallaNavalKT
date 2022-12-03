package com.proyectofinal.batallanavalkt.Activitys

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.AdaptadorCuadrante
import com.proyectofinal.batallanavalkt.adapters.MessageAdapter
import com.proyectofinal.batallanavalkt.databinding.ActivityMultiplayerGameBinding
import com.proyectofinal.batallanavalkt.dialogs.*
import com.proyectofinal.batallanavalkt.models.Cuadrante
import com.proyectofinal.batallanavalkt.models.Gameplay
import com.proyectofinal.batallanavalkt.models.Message
import com.proyectofinal.batallanavalkt.models.Player
import java.io.File


class MultiplayerGame : AppCompatActivity(), DialogDuelTurn.DialogDuelTurnListener,
    DialogDecideTurn.DialogDecideTurnListener,
    DialogSelectNavesMultiplayer.selectNavesMultiplayerListener,
    DialogSetNaves.setNavesListener, DialogSelectCuadrante.setapplyAttackListener, DialogResults.DialogResultsListener{

    private lateinit var binding: ActivityMultiplayerGameBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)


    private var nick = ""
    private var user = ""
    private var rivalnick = ""
    private var rivaluser = ""
    private var player1 = Player()
    private var player2 = Player()
    private var numTurns = 0 //Número de turnos (contador)
    private var gameId = ""
    private var navesp1 = 0
    private var navesp2 = 0

    private var db = Firebase.firestore
    private var db0 = FirebaseFirestore.getInstance()

    private lateinit var mStorage: FirebaseStorage
    private lateinit var mReference: StorageReference
    private var bitmap: Bitmap? = null

    private var chronoRunning = false //Determina si el cronómetro esta corriendo

    private lateinit var audioAttributes: AudioAttributes
    private lateinit var sp: SoundPool
    private lateinit var mp: MediaPlayer
    private var cartas: ArrayList<Int> = arrayListOf()

    data class Turn(
        var turn: Int = 0,
        var usr: String = ""
    )

    data class Animation(
        var type: Int = 0,
        var usr: String = ""
    )


    data class Data(var items: ArrayList<Cuadrante>)


    @SuppressLint("InlinedApi")
    private val hidePart2Runnable = Runnable {
        // Delayed removal of status and navigation bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
    }
    private val showPart2Runnable = Runnable {
        // Delayed display of UI elements
        supportActionBar?.show()
        fullscreenContentControls.visibility = View.VISIBLE
    }
    private var isFullscreen: Boolean = false

    private val hideRunnable = Runnable { hide() }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private val delayHideTouchListener = View.OnTouchListener { view, motionEvent ->
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS)
            }
            MotionEvent.ACTION_UP -> view.performClick()
            else -> {
            }
        }
        false
    }


    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent(this, Lobby::class.java)
        intent.putExtra("User", user)
        intent.putExtra("Nick", nick)
        startActivity(intent)
        mp.stop()
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMultiplayerGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isFullscreen = true

        // Set up the user interaction to manually show or hide the system UI.
        fullscreenContent = binding.fullscreenContent
        fullscreenContent.setOnClickListener { toggle() }

        fullscreenContentControls = binding.fullscreenContentControls

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        binding.dummyButton.setOnTouchListener(delayHideTouchListener)
        //todo despues de esta linea

        intent.getStringExtra("gameId")?.let { gameId = it }
        intent.getStringExtra("User")?.let { user = it }
        intent.getStringExtra("Nick")?.let { nick = it }

        this.player1.setNickname(nick)

        mStorage = FirebaseStorage.getInstance()
        mReference = mStorage.reference


        audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        sp = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()
        mp = MediaPlayer.create(this, R.raw.games)
        mp.setVolume(0.5f, 0.5f)
        mp.start()
        mp.isLooping = true

        //obtiene datos extras necesarios para mostrar en pantalla
        val ref2 = db.collection("users").document(user).collection("games").document(gameId).get()
        ref2.addOnSuccessListener { document ->
            if (document != null) {
                val parts = document.data?.get("users").toString().split(',', ']')
                val otherUser = parts[1].replace(" ", "").lowercase()
                val otherref = db.collection("users").document(otherUser).get()
                    .addOnSuccessListener { userdoc ->
                        if (userdoc != null) {
                            rivaluser = userdoc.data?.get("email").toString().lowercase()
                            rivalnick = userdoc.data?.get("nick").toString()
                        }

                        player2.setNickname(rivalnick)


                        binding.numNaves1.text = nick
                        binding.numNaves2.text = rivalnick

                        player2.setNickname(rivalnick)
                        player2.setUsermail(rivaluser)
                        val imgRef = mReference.child("images/$rivaluser")
                        val localfile2 = File.createTempFile("tempImg", "jpg")
                        imgRef.getFile(localfile2).addOnSuccessListener {
                            bitmap = BitmapFactory.decodeFile(localfile2.absolutePath)
                            val options = RequestOptions()
                            options.centerCrop().fitCenter()
                            Glide.with(this@MultiplayerGame).load(bitmap).apply(options)
                                .into(binding.user2Image2)
                        }


                        player1.setNickname(nick)
                        player1.setUsermail(user)
                        val imgRef2 = mReference.child("images/$user")
                        val localfile = File.createTempFile("tempImg", "jpg")
                        imgRef2.getFile(localfile).addOnSuccessListener {
                            bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                            val options = RequestOptions()
                            options.centerCrop().fitCenter()
                            Glide.with(this@MultiplayerGame).load(bitmap).apply(options)
                                .into(binding.user1Image2)
                        }


                        data class online(var online: Boolean = true)

                        var isonlie = online(online = true)

                        db.collection("games").document(gameId).collection("online").document(user)
                            .set(isonlie)

                        val dAlert = DialogMsg("Esperando conexion de $rivalnick")
                        dAlert.isCancelable = false

                        db.collection("games").document(gameId).collection("online").get()
                            .addOnSuccessListener { documents ->
                                if (documents != null) {
                                    if (documents.size() == 2) {
                                        db.collection("games").document(gameId)
                                            .collection("definenaves").get()
                                            .addOnSuccessListener { doc ->
                                                if (doc != null) {
                                                    if (doc.size() == 2) {
                                                        db.collection("games").document(gameId).collection("Tablero").document(user).collection(user).get().addOnSuccessListener { doc ->
                                                            var list: MutableList<Cuadrante> = arrayListOf()
                                                            for (i in 0 until 100) {
                                                                list.add(
                                                                    Cuadrante(
                                                                        id = doc.documents[i].getLong("id")?.toInt()!!,
                                                                        name = doc.documents[i].get("name").toString(),
                                                                        pznave = doc.documents[i].get("pznave").toString(),
                                                                        isMar = doc.documents[i].get("mar") as Boolean,
                                                                        isImpactMar = doc.documents[i].get("impactMar") as Boolean,
                                                                        isImpact = doc.documents[i].get("impact") as Boolean,
                                                                        isenable = doc.documents[i].get("isenable") as Boolean,
                                                                        angle = doc.documents[i].getLong("angle")?.toInt()!!,
                                                                        isnave = doc.documents[i].get("isnave") as Boolean,
                                                                        bg = doc.documents[i].getLong("bg")?.toInt()!!,
                                                                    )
                                                                )
                                                            }

                                                            this.player1.setMyTablero(list)
                                                        }

                                                        db.collection("games").document(gameId).collection("Tablero").document(rivaluser).collection(rivaluser).get().addOnSuccessListener { doc ->
                                                            var list: MutableList<Cuadrante> = arrayListOf()
                                                            for (i in 0 until 100) {
                                                                list.add(
                                                                    Cuadrante(
                                                                        id = doc.documents[i].getLong("id")?.toInt()!!,
                                                                        name = doc.documents[i].get("name").toString(),
                                                                        pznave = doc.documents[i].get("pznave").toString(),
                                                                        isMar = doc.documents[i].get("mar") as Boolean,
                                                                        isImpactMar = doc.documents[i].get("impactMar") as Boolean,
                                                                        isImpact = doc.documents[i].get("impact") as Boolean,
                                                                        isenable = doc.documents[i].get("isenable") as Boolean,
                                                                        angle = doc.documents[i].getLong("angle")?.toInt()!!,
                                                                        isnave = doc.documents[i].get("isnave") as Boolean,
                                                                        bg = doc.documents[i].getLong("bg")?.toInt()!!,
                                                                    )
                                                                )
                                                            }

                                                            this.player2.setMyTablero(list)
                                                        }


                                                        db.collection("games").document(gameId)
                                                            .collection("setnaves").get()
                                                            .addOnSuccessListener { doc2 ->
                                                                if (doc2 != null) {
                                                                    if (doc2.size() == 2) {

                                                                        val dMsgturno =
                                                                            DialogMsg("Esperando Decision de $rivalnick")
                                                                        dMsgturno.isCancelable =
                                                                            false

                                                                        db.collection("games")
                                                                            .document(gameId)
                                                                            .collection("setturn")
                                                                            .get()
                                                                            .addOnSuccessListener { doc ->
                                                                                if (doc != null) {
                                                                                    if (doc.size() == 2) {
                                                                                        db.collection(
                                                                                            "games"
                                                                                        ).document(
                                                                                            gameId
                                                                                        )
                                                                                            .collection(
                                                                                                "turn"
                                                                                            )
                                                                                            .document(
                                                                                                gameId
                                                                                            ).get()
                                                                                            .addOnSuccessListener { docend ->
                                                                                                if (docend != null) {
                                                                                                    if (!docend.exists()) {
                                                                                                        //optener turnos e iniciar animacion
                                                                                                        if (doc.documents[0].id == user) {
                                                                                                            this.player2.setPpt(
                                                                                                                doc.documents[1].getLong(
                                                                                                                    "turn"
                                                                                                                )
                                                                                                                    ?.toInt()!!
                                                                                                            )
                                                                                                            this.player1.setPpt(
                                                                                                                doc.documents[0].getLong(
                                                                                                                    "turn"
                                                                                                                )
                                                                                                                    ?.toInt()!!
                                                                                                            )
                                                                                                        } else {
                                                                                                            this.player2.setPpt(
                                                                                                                doc.documents[0].getLong(
                                                                                                                    "turn"
                                                                                                                )
                                                                                                                    ?.toInt()!!
                                                                                                            )
                                                                                                            this.player2.setPpt(
                                                                                                                doc.documents[1].getLong(
                                                                                                                    "turn"
                                                                                                                )
                                                                                                                    ?.toInt()!!
                                                                                                            )
                                                                                                        }

                                                                                                        if ((this.player1.getPpt() == 0 && this.player2.getPpt() == 1) || (this.player1.getPpt() == 1 && this.player2.getPpt() == 2) || (this.player1.getPpt() == 2 && this.player2.getPpt() == 0)) {
                                                                                                            this.player1.setIsFirst(
                                                                                                                false
                                                                                                            )
                                                                                                            this.player2.setIsFirst(
                                                                                                                true
                                                                                                            )
                                                                                                        }
                                                                                                        if ((this.player1.getPpt() == 0 && this.player2.getPpt() == 2) || (this.player1.getPpt() == 1 && this.player2.getPpt() == 0) || (this.player1.getPpt() == 2 && this.player2.getPpt() == 1)) {
                                                                                                            this.player1.setIsFirst(
                                                                                                                true
                                                                                                            )
                                                                                                            this.player2.setIsFirst(
                                                                                                                false
                                                                                                            )
                                                                                                        }

                                                                                                        if (this.player1.getIsFirst()) {
                                                                                                            numTurns++
                                                                                                            db.collection("games").document(gameId).collection("turn").document(gameId)
                                                                                                                .set(Turn(numTurns, nick))
                                                                                                            db.collection("games").document(gameId).collection("animation").document(gameId)
                                                                                                                .set(Animation(-1, nick))
                                                                                                        }

                                                                                                        inicarjuego()
                                                                                                    } else {
                                                                                                        inicarjuego()
                                                                                                    }
                                                                                                }
                                                                                            }


                                                                                    } else if (doc.size() == 1) {
                                                                                        dMsgturno.show(
                                                                                            supportFragmentManager,
                                                                                            "Waiting"
                                                                                        )
                                                                                        if (doc.documents[0].id == user) {
                                                                                            db.collection(
                                                                                                "games"
                                                                                            )
                                                                                                .document(
                                                                                                    gameId
                                                                                                )
                                                                                                .collection(
                                                                                                    "setturn"
                                                                                                )
                                                                                                .addSnapshotListener { value, error ->
                                                                                                    if (error == null) {
                                                                                                        value?.let {
                                                                                                            if (value.size() == 2) {
                                                                                                                dMsgturno.dismiss()
                                                                                                                if (value.documents[0].id == user) {
                                                                                                                    this.player2.setPpt(
                                                                                                                        value.documents[1].getLong(
                                                                                                                            "turn"
                                                                                                                        )
                                                                                                                            ?.toInt()!!
                                                                                                                    )
                                                                                                                } else {
                                                                                                                    this.player2.setPpt(
                                                                                                                        value.documents[0].getLong(
                                                                                                                            "turn"
                                                                                                                        )
                                                                                                                            ?.toInt()!!
                                                                                                                    )
                                                                                                                }

                                                                                                                val dDecideTurn =
                                                                                                                    DialogDuelTurn(
                                                                                                                        this,
                                                                                                                        this.player1,
                                                                                                                        this.player2
                                                                                                                    )
                                                                                                                dDecideTurn.isCancelable =
                                                                                                                    false
                                                                                                                dDecideTurn.show(
                                                                                                                    supportFragmentManager,
                                                                                                                    "Duel Turn"
                                                                                                                )

                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                        } else {
                                                                                            val decidetun =
                                                                                                DialogDecideTurn(
                                                                                                    this,
                                                                                                    this.player1
                                                                                                )
                                                                                            decidetun.isCancelable =
                                                                                                false
                                                                                            decidetun.show(
                                                                                                supportFragmentManager,
                                                                                                "decide turn"
                                                                                            )
                                                                                        }
                                                                                    } else if (doc.size() == 0) {
                                                                                        val decidetun = DialogDecideTurn(this, this.player1)
                                                                                        decidetun.isCancelable = false
                                                                                        decidetun.show(supportFragmentManager, "decide turn")
                                                                                    }
                                                                                }
                                                                            }

                                                                    } else if (doc2.size() == 1) {
                                                                        println("Entro anidado size 1 $doc2")
                                                                        if (doc2.documents[0].id == user) {
                                                                            // brinco al siguiente paso espara de turno
                                                                            //antes del turno
                                                                        } else {
                                                                            db.collection("games")
                                                                                .document(gameId)
                                                                                .collection("naves")
                                                                                .document(gameId)
                                                                                .get()
                                                                                .addOnSuccessListener { tablero ->
                                                                                    if (tablero?.exists() == true) {
                                                                                        tablero.let {
                                                                                            this.player1.setcantPA(
                                                                                                tablero.getDouble(
                                                                                                    "pa"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                            this.player1.setcantB(
                                                                                                tablero.getDouble(
                                                                                                    "b"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                            this.player1.setcantSB(
                                                                                                tablero.getDouble(
                                                                                                    "sb"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                            this.player1.setcantC(
                                                                                                tablero.getDouble(
                                                                                                    "c"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                            this.player1.setcantL(
                                                                                                tablero.getDouble(
                                                                                                    "l"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                            this.player1.setusetable(
                                                                                                tablero.getDouble(
                                                                                                    "usetable"
                                                                                                )
                                                                                                    ?.toInt()!!
                                                                                            )
                                                                                        }

                                                                                        val dSetNaves =
                                                                                            DialogSetNaves(
                                                                                                this,
                                                                                                this.player1
                                                                                            )
                                                                                        dSetNaves.isCancelable =
                                                                                            false
                                                                                        dSetNaves.show(
                                                                                            supportFragmentManager,
                                                                                            "SetNaves Tab"
                                                                                        )

                                                                                    }
                                                                                }
                                                                        }

                                                                    } else if (doc2.size() == 0) {
                                                                        //update get naves
                                                                        db.collection("games")
                                                                            .document(gameId)
                                                                            .collection("naves")
                                                                            .document(gameId).get()
                                                                            .addOnSuccessListener { tablero ->
                                                                                if (tablero?.exists() == true) {
                                                                                    tablero.let {
                                                                                        this.player1.setcantPA(
                                                                                            tablero.getDouble(
                                                                                                "pa"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                        this.player1.setcantB(
                                                                                            tablero.getDouble(
                                                                                                "b"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                        this.player1.setcantSB(
                                                                                            tablero.getDouble(
                                                                                                "sb"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                        this.player1.setcantC(
                                                                                            tablero.getDouble(
                                                                                                "c"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                        this.player1.setcantL(
                                                                                            tablero.getDouble(
                                                                                                "l"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                        this.player1.setusetable(
                                                                                            tablero.getDouble(
                                                                                                "usetable"
                                                                                            )
                                                                                                ?.toInt()!!
                                                                                        )
                                                                                    }

                                                                                    val dSetNaves =
                                                                                        DialogSetNaves(
                                                                                            this,
                                                                                            this.player1
                                                                                        )
                                                                                    dSetNaves.isCancelable =
                                                                                        false
                                                                                    dSetNaves.show(
                                                                                        supportFragmentManager,
                                                                                        "SetNaves Tab"
                                                                                    )

                                                                                }
                                                                            }
                                                                    }
                                                                }
                                                            }
                                                    } else if (doc.size() == 1) {
                                                        if (doc.documents[0].id == user) {
                                                            val dAlertwait =
                                                                DialogMsg("Esperando Confirmacion de $rivalnick")
                                                            dAlertwait.isCancelable = false
                                                            dAlertwait.show(
                                                                supportFragmentManager,
                                                                "Waiting"
                                                            )
                                                            db.collection("games").document(gameId)
                                                                .collection("definenaves")
                                                                .addSnapshotListener { doc2, error ->
                                                                    if (error == null) {
                                                                        doc2?.let {
                                                                            if (doc2.size() == 2) {
                                                                                db.collection("games")
                                                                                    .document(gameId)
                                                                                    .collection("definenaves")
                                                                                    .addSnapshotListener { doc2, error ->
                                                                                        if (error == null) {
                                                                                            doc2?.let {
                                                                                                if (doc2.size() == 2) {
                                                                                                    dAlertwait.dismiss()
                                                                                                    val dSetNaves =
                                                                                                        DialogSetNaves(
                                                                                                            this,
                                                                                                            this.player1
                                                                                                        )
                                                                                                    dSetNaves.isCancelable =
                                                                                                        false
                                                                                                    dSetNaves.show(
                                                                                                        supportFragmentManager,
                                                                                                        "SetNaves Tab"
                                                                                                    )
                                                                                                }
                                                                                            }
                                                                                        }

                                                                                    }
                                                                            }
                                                                        }
                                                                    }

                                                                }
                                                        } else {

                                                            db.collection("games").document(gameId)
                                                                .collection("naves")
                                                                .document(gameId).get()
                                                                .addOnSuccessListener { tablero ->
                                                                    if (tablero?.exists() == true) {
                                                                        tablero.let {
                                                                            this.player1.setcantPA(
                                                                                tablero.getDouble("pa")
                                                                                    ?.toInt()!!
                                                                            )
                                                                            this.player1.setcantB(
                                                                                tablero.getDouble("b")
                                                                                    ?.toInt()!!
                                                                            )
                                                                            this.player1.setcantSB(
                                                                                tablero.getDouble("sb")
                                                                                    ?.toInt()!!
                                                                            )
                                                                            this.player1.setcantC(
                                                                                tablero.getDouble("c")
                                                                                    ?.toInt()!!
                                                                            )
                                                                            this.player1.setcantL(
                                                                                tablero.getDouble("l")
                                                                                    ?.toInt()!!
                                                                            )
                                                                            this.player1.setusetable(
                                                                                tablero.getDouble("usetable")
                                                                                    ?.toInt()!!
                                                                            )
                                                                        }

                                                                        val dSelectNaves =
                                                                            DialogSelectNavesMultiplayer(
                                                                                this,
                                                                                this.player1,
                                                                                gameId
                                                                            )
                                                                        dSelectNaves.isCancelable =
                                                                            false
                                                                        dSelectNaves.show(
                                                                            supportFragmentManager,
                                                                            "SetNaves Tab"
                                                                        )

                                                                    }
                                                                }

                                                        }


                                                    } else if (doc.size() == 0) {
                                                        val decideNaves =
                                                            DialogSelectNavesMultiplayer(
                                                                this,
                                                                this.player1,
                                                                gameId
                                                            )
                                                        decideNaves.isCancelable = false
                                                        decideNaves.show(
                                                            supportFragmentManager,
                                                            "Decidir Naves"
                                                        )
                                                    }

                                                }

                                            }

                                    } else if (documents.size() == 1) {
                                        dAlert.show(supportFragmentManager, "Waiting")
                                        db.collection("games").document(gameId)
                                            .collection("online")
                                            .addSnapshotListener { doc, error ->
                                                if (error == null) {
                                                    doc?.let {
                                                        println("collection listener sice:${doc.size()} conte$doc")
                                                        if (doc.size() == 2) {
                                                            dAlert.dismiss()

                                                            val decideNaves =
                                                                DialogSelectNavesMultiplayer(
                                                                    this,
                                                                    this.player1,
                                                                    gameId
                                                                )
                                                            decideNaves.isCancelable = false
                                                            decideNaves.show(
                                                                supportFragmentManager,
                                                                "Decidir Naves"
                                                            )

                                                        }

                                                    }
                                                }

                                            }
                                    }
                                }
                            }
                    }

            } else {
                println("Error newGameUserNick")
            }
        }.addOnFailureListener { exeption ->
            println(exeption)
        }

    }

    private fun sendMessage() {
        val message = Message(
            message = binding.messageTextField.text.toString(),
            from = user
        )

        db.collection("games").document(gameId).collection("messages").document().set(message)

        binding.messageTextField.setText("")
    }

    private fun defWiner(otherplayer: String, result: String) {
        val partida = Gameplay(
            vs = otherplayer,
            status = result,
        )
        db.collection("users").document(user).collection("Multiplayergames").document(gameId)
            .set(partida)
        db.collection("games").document(gameId).collection("Multiplayergames").document(gameId)
            .set(partida)

    }

    override fun applyNavesMultiplayer(player: Player) {
        this.player1 = player

        data class applynave(var setnave: Boolean = true)

        var apply = applynave(setnave = true)

        db.collection("games").document(gameId).collection("definenaves").document(user).set(apply)

        val dAlert = DialogMsg("Esperando Confirmacion de $rivalnick")
        dAlert.isCancelable = false


        db.collection("games").document(gameId).collection("definenaves").get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    if (doc.size() == 2) {

                        val dSetNaves = DialogSetNaves(this, this.player1)
                        dSetNaves.isCancelable = false
                        dSetNaves.show(supportFragmentManager, "SetNaves Tab")
                    } else if (doc.size() == 1) {
                        dAlert.show(supportFragmentManager, "Waiting")
                        db.collection("games").document(gameId).collection("definenaves")
                            .addSnapshotListener { doc2, error ->
                                if (error == null) {
                                    doc2?.let {
                                        if (doc2.size() == 2) {
                                            dAlert.dismiss()
                                            db.collection("games").document(gameId)
                                                .collection("definenaves")
                                                .addSnapshotListener { doc2, error ->
                                                    if (error == null) {
                                                        doc2?.let {
                                                            if (doc2.size() == 2) {
                                                                val dSetNaves = DialogSetNaves(
                                                                    this,
                                                                    this.player1
                                                                )
                                                                dSetNaves.isCancelable = false
                                                                dSetNaves.show(
                                                                    supportFragmentManager,
                                                                    "SetNaves Tab"
                                                                )
                                                            }
                                                        }
                                                    }

                                                }
                                        }
                                    }
                                }

                            }
                    }

                }

            }


    }

    override fun applyNavesSet(player: Player) {
        this.player1 = player
        this.player2.setcantPA(this.player1.getcantPA())
        this.player2.setcantB(this.player1.getcantB())
        this.player2.setcantSB(this.player1.getcantSB())
        this.player2.setcantC(this.player1.getcantC())
        this.player2.setcantL(this.player1.getcantL())

        data class applynave(var setnave: Boolean = true)

        var apply = applynave(setnave = true)

        db.collection("games").document(gameId).collection("setnaves").document(user).set(apply)

        var data = Data(items = arrayListOf())

        for (it in this.player1.getMyTablero()) {
            db.collection("games").document(gameId).collection("Tablero").document(user).collection(user).document(it.id.toString()).set(it)
        }


        val dAlert = DialogMsg("Esperando Diseño de tablero de $rivalnick")
        dAlert.isCancelable = false


        db.collection("games").document(gameId).collection("setnaves").get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    if (doc.size() == 2) {

                        db.collection("games").document(gameId).collection("Tablero").document(rivaluser).collection(rivaluser).get().addOnSuccessListener { doc ->
                            var list: MutableList<Cuadrante> = arrayListOf()
                            for (i in 0 until 100) {
                                list.add(
                                    Cuadrante(
                                        id = doc.documents[i].getLong("id")?.toInt()!!,
                                        name = doc.documents[i].get("name").toString(),
                                        pznave = doc.documents[i].get("pznave").toString(),
                                        isMar = doc.documents[i].get("mar") as Boolean,
                                        isImpactMar = doc.documents[i].get("impactMar") as Boolean,
                                        isImpact = doc.documents[i].get("impact") as Boolean,
                                        isenable = doc.documents[i].get("isenable") as Boolean,
                                        angle = doc.documents[i].getLong("angle")?.toInt()!!,
                                        isnave = doc.documents[i].get("isnave") as Boolean,
                                        bg = doc.documents[i].getLong("bg")?.toInt()!!,
                                    )
                                )
                            }

                            this.player2.setMyTablero(list)
                        }

                        val decidetun = DialogDecideTurn(this, this.player1)
                        decidetun.isCancelable = false
                        decidetun.show(supportFragmentManager, "decide turn")

                    } else if (doc.size() == 1) {
                        dAlert.show(supportFragmentManager, "Waiting")
                        db.collection("games").document(gameId).collection("setnaves")
                            .addSnapshotListener { doc2, error ->
                                if (error == null) {
                                    doc2?.let {
                                        if (doc2.size() == 2) {

                                            db.collection("games").document(gameId).collection("Tablero").document(rivaluser).collection(rivaluser).addSnapshotListener { doc, error ->
                                                if (error == null) {
                                                    print(doc?.size())
                                                    if (doc?.size() == 100) {
                                                        db.collection("games").document(gameId).collection("Tablero").document(rivaluser).collection(rivaluser).get().addOnSuccessListener { doc ->
                                                            var list: MutableList<Cuadrante> = arrayListOf()
                                                            for (i in 0 until 100) {
                                                                list.add(
                                                                    Cuadrante(
                                                                        id = doc.documents[i].getLong("id")?.toInt()!!,
                                                                        name = doc.documents[i].get("name").toString(),
                                                                        pznave = doc.documents[i].get("pznave").toString(),
                                                                        isMar = doc.documents[i].get("mar") as Boolean,
                                                                        isImpactMar = doc.documents[i].get("impactMar") as Boolean,
                                                                        isImpact = doc.documents[i].get("impact") as Boolean,
                                                                        isenable = doc.documents[i].get("isenable") as Boolean,
                                                                        angle = doc.documents[i].getLong("angle")?.toInt()!!,
                                                                        isnave = doc.documents[i].get("isnave") as Boolean,
                                                                        bg = doc.documents[i].getLong("bg")?.toInt()!!,
                                                                    )
                                                                )
                                                            }

                                                            this.player2.setMyTablero(list)
                                                        }
                                                    }
                                                }

                                            }



                                            dAlert.dismiss()
                                            db.collection("games").document(gameId)
                                                .collection("setnaves")
                                                .addSnapshotListener { doc2, error ->
                                                    if (error == null) {
                                                        doc2?.let {
                                                            if (doc2.size() == 2) {
                                                                val decidetun = DialogDecideTurn(
                                                                    this,
                                                                    this.player1
                                                                )
                                                                decidetun.isCancelable = false
                                                                decidetun.show(
                                                                    supportFragmentManager,
                                                                    "decide turn"
                                                                )
                                                            }
                                                        }
                                                    }

                                                }
                                        }
                                    }
                                }
                            }
                    }

                }

            }
    }

    override fun applyDecideTurn(player: Player) {
        this.player1 = player

        data class Turn(var turn: Int = 0)

        var rpschoise = Turn(turn = this.player1.getPpt())

        db.collection("games").document(gameId).collection("setturn").document(user).set(rpschoise)


        val dMsg = DialogMsg("Esperando Decision de $rivalnick")
        dMsg.isCancelable = false

        db.collection("games").document(gameId).collection("setturn").get()
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    if (doc.size() == 2) {
                        if (doc.documents[0].id == user) {
                            this.player2.setPpt(doc.documents[1].getLong("turn")?.toInt()!!)
                        } else {
                            this.player2.setPpt(doc.documents[0].getLong("turn")?.toInt()!!)
                        }
                        val dDecideTurn = DialogDuelTurn(this, this.player1, this.player2)
                        dDecideTurn.isCancelable = false
                        dDecideTurn.show(supportFragmentManager, "Duel Turn")

                    } else if (doc.size() == 1) {
                        dMsg.show(supportFragmentManager, "Waiting")
                        db.collection("games").document(gameId).collection("setturn")
                            .addSnapshotListener { value, error ->
                                if (error == null) {
                                    value?.let {
                                        if (value.size() == 2) {
                                            dMsg.dismiss()
                                            if (value.documents[0].id == user) {
                                                this.player2.setPpt(
                                                    value.documents[1].getLong("turn")?.toInt()!!
                                                )
                                            } else {
                                                this.player2.setPpt(
                                                    value.documents[0].getLong("turn")?.toInt()!!
                                                )
                                            }

                                            val dDecideTurn =
                                                DialogDuelTurn(this, this.player1, this.player2)
                                            dDecideTurn.isCancelable = false
                                            dDecideTurn.show(supportFragmentManager, "Duel Turn")

                                        }
                                    }
                                }
                            }
                    }
                }
            }


    }


    override fun applyDuelTurn(player1: Player, player2: Player, isDraw: Boolean) {
        this.player1 = player1
        this.player2 = player2

        if (isDraw) {
            db.collection("games").document(gameId).collection("setturn").document(user).delete()
                .addOnSuccessListener {
                    db.collection("games").document(gameId).collection("setturn").get()
                        .addOnSuccessListener { doc ->
                            if (doc != null) {
                                if (doc.size() == 1) {
                                    db.collection("games").document(gameId).collection("setturn")
                                        .addSnapshotListener { value, error ->
                                            if (error == null) {
                                                value?.let {
                                                    if (value.size() == 0) {
                                                        val decidetun =
                                                            DialogDecideTurn(this, this.player1)
                                                        decidetun.isCancelable = false
                                                        decidetun.show(
                                                            supportFragmentManager,
                                                            "decide turn"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                } else if (doc.size() == 0) {
                                    val decidetun = DialogDecideTurn(this, this.player1)
                                    decidetun.isCancelable = false
                                    decidetun.show(supportFragmentManager, "decide turn")
                                }
                            }
                        }
                }
        } else {
            if (this.player1.getIsFirst()) {
                numTurns++
                db.collection("games").document(gameId).collection("turn").document(gameId)
                    .set(Turn(numTurns, nick))
                db.collection("games").document(gameId).collection("animation").document(gameId)
                    .set(Animation(-1, nick))
            }

            inicarjuego()
        }

    }

    override fun applyDialogResults(res: String) {
        var intent = Intent(this, Lobby::class.java)
        intent.putExtra("User", user)
        intent.putExtra("Nick", nick)
        mp.stop()

        //subir resultados
        startActivity(intent)
    }

    private fun inicarjuego() {

        for(i in this.player1.getMyTablero()){
            if(i.isnave && !i.isImpact){
                this.player1.setusetable(this.player1.getusetable()+1)
            }
        }

        for(i in this.player2.getMyTablero()){
            if(i.isnave && !i.isImpact){
                this.player2.setusetable(this.player2.getusetable()+1)
            }
        }

        binding.crono.base = SystemClock.elapsedRealtime()

        println("Use Tab "+ this.player1.getusetable())

        initTabs()

        binding.crono.start()
        when (numTurns) {
            1 -> {
                if (this.player1.getIsFirst()) {
                    checktab(3)
                }
            }
        }


        //listener de jeugo
        db.collection("games").document(gameId).collection("turn").document(gameId)
            .addSnapshotListener { turno, error ->
                if (error == null) {
                    turno?.let {
                        numTurns = turno.getLong("turn")?.toInt()!!
                        binding.numTurnosTextView.text = "Turno $numTurns"
                        if (turno.get("usr").toString() == nick) {
                            binding.attacklayout.visibility = View.VISIBLE
                        } else {
                            binding.attacklayout.visibility = View.GONE
                        }
                    }
                }
            }

        //listener de animaciones

        db.collection("games").document(gameId).collection("animation").document(gameId)
            .addSnapshotListener { animation, error ->
                if (error == null) {
                    animation?.let {
                        var impact = DialogAnimations(this, 2, "$rivalnick")
                        var notimpact = DialogAnimations(this, 3, "$rivalnick")

                        if (animation.get("usr").toString() == rivalnick) {
                            when (animation.getLong("type")?.toInt()!!) {
                                1 -> {
                                    impact.show(supportFragmentManager, "acert Impact")
                                    this.player1.setusetable(this.player1.getusetable()-1)
                                    checktab(0)
                                }
                                2 -> {
                                    notimpact.show(supportFragmentManager, "Fail Impact")
                                    checktab(1)
                                }
                                10 -> {//se pierde
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 1)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                11 -> {//se gana
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 0)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                12 -> {//Empate
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 2)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                else -> {}
                            }
                        }else{
                            when (animation.getLong("type")?.toInt()!!) {
                                10 -> {//se pierde
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 1)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                11 -> {//se gana
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 0)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                12 -> {//Empate
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val resultsDialog = DialogResults(this, player1, player2, 2)
                                        resultsDialog.isCancelable = false
                                        resultsDialog.show(supportFragmentManager, "Resultados")
                                    }, 800)
                                }
                                else -> {}
                            }
                        }

                    }
                }
            }


    }

    private fun initTabs() {
        binding.numTurnosTextView.text = "Turno $numTurns"
        binding.btnattack.setOnClickListener {
            val atacar = DialogSelectCuadrante(this, this.player2)
            atacar.isCancelable = false
            atacar.show(supportFragmentManager, "Seleccionar Cuadrante")

        }

        binding.chatbtn.setOnClickListener {
            if (binding.chatgame.visibility == View.VISIBLE) {
                binding.chatgame.visibility = View.GONE
            } else {
                binding.chatgame.visibility = View.VISIBLE
            }
        }

        binding.closechatbtn.setOnClickListener {
            if (binding.chatgame.visibility == View.VISIBLE) {
                binding.chatgame.visibility = View.GONE
            } else {
                binding.chatgame.visibility = View.VISIBLE
            }
        }

        binding.txtterminarjuego.setOnClickListener {
            defWiner(rivalnick, "Derrota")
            val increment = FieldValue.increment(1)
            val database = FirebaseFirestore.getInstance()
            val p2 =  FirebaseFirestore.getInstance().collection("users").document(rivaluser)
            val p1 =  FirebaseFirestore.getInstance().collection("users").document(user)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(p2)
                transaction.update(p1, "winspvp", increment)
                transaction.update(p2, "losespvp", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }

            db0.collection("users").document(rivaluser).collection("games").document(gameId)
                .update("status", "Victoria")
            db0.collection("users").document(user).collection("games").document(gameId)
                .update("status", "Derrota")
            db0.collection("games").document(gameId).collection("GameStatus").document(rivaluser)
                .update("status", "Terminada")

            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(10, nick))

            var intent = Intent(this, Lobby::class.java)
            intent.putExtra("User", user)
            intent.putExtra("Nick", nick)
            startActivity(intent)
            finish()
        }

        binding.sendMessageButton.setOnClickListener { sendMessage() }

        binding.messagesRecylerView.layoutManager = LinearLayoutManager(this)
        binding.messagesRecylerView.adapter = MessageAdapter(player1.getNickname())

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


        db.collection("games").document(gameId).collection("Tablero").document(user).collection(user)
            .addSnapshotListener { doc, error ->
                if (error == null) {
                    if (doc != null) {
                        var list: MutableList<Cuadrante> = arrayListOf()
                        for (i in 0 until 100) {
                            list.add(
                                Cuadrante(
                                    id = doc.documents[i].getLong("id")?.toInt()!!,
                                    name = doc.documents[i].get("name").toString(),
                                    pznave = doc.documents[i].get("pznave").toString(),
                                    isMar = doc.documents[i].get("mar") as Boolean,
                                    isImpactMar = doc.documents[i].get("impactMar") as Boolean,
                                    isImpact = doc.documents[i].get("impact") as Boolean,
                                    isenable = doc.documents[i].get("isenable") as Boolean,
                                    angle = doc.documents[i].getLong("angle")?.toInt()!!,
                                    isnave = doc.documents[i].get("isnave") as Boolean,
                                    bg = doc.documents[i].getLong("bg")?.toInt()!!,
                                )
                            )
                        }

                        list.sortBy { it.id }

                        this.player1.setMyTablero(list)
                        aptersupdate()
                    }
                }
            }




        navesp1 = this.player1.getusetable()
        this.player2.setusetable(player1.getusetable())
        navesp2 = this.player2.getusetable()



//        this.player1.getMyTablero().sortWith(compareBy { it.name.toInt() })
        this.player2.getMyTablero().sortBy { it.id }
        aptersupdate()



        for (i in 0 until player2.getMyTablero().size) {
            player2.getMyTablero()[i].isenable = true
        }


    }


    private fun checktab(turn: Int) {
        val increment = FieldValue.increment(1)
        val database = FirebaseFirestore.getInstance()
        val p2 =  FirebaseFirestore.getInstance().collection("users").document(rivaluser)
        val p1 =  FirebaseFirestore.getInstance().collection("users").document(user)

        if (numTurns == 150) {
            defWiner(rivalnick, "Empate")
            defWiner(nick, "Empate")

            db0.collection("users").document(rivaluser).collection("games").document(gameId)
                .update("status", "Empate")
            db0.collection("users").document(user).collection("games").document(gameId)
                .update("status", "Empate")
            db0.collection("games").document(gameId).collection("GameStatus").document(rivaluser)
                .update("status", "Empate")

            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(12, nick))
        }

        if (this.player2.getusetable() <= 0) {

            defWiner(rivalnick, "Derrota")

            database.runTransaction { transaction ->
                val snapshot = transaction.get(p2)
                transaction.update(p1, "winspvp", increment)
                transaction.update(p2, "losespvp", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }

                db0.collection("users").document(rivaluser).collection("games").document(gameId)
                    .update("status", "Derrota")
                db0.collection("users").document(user).collection("games").document(gameId)
                    .update("status", "Victoria")
                db0.collection("games").document(gameId).collection("GameStatus").document(rivaluser)
                    .update("status", "Derrota")

            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(10, nick))
        }

        if (this.player1.getusetable() <= 0) {
            database.runTransaction { transaction ->
                val snapshot = transaction.get(p2)
                transaction.update(p2, "winspvp", increment)
                transaction.update(p1, "losespvp", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }
            defWiner(nick, "Victoria")
            //rival, resultado

                db0.collection("users").document(rivaluser).collection("games").document(gameId)
                    .update("status", "Victoria")
                db0.collection("users").document(user).collection("games").document(gameId)
                    .update("status", "Derrota")

                db0.collection("games").document(gameId).collection("GameStatus").document(user)
                    .update("status", "Derrota")

            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(11, nick))
        }

        when (turn) {
            0 -> {//turno de Jugador 2
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player2)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
                }, 13000)
            }
            1 -> {//turno del jugador
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player1)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
                }, 13000)
            }
            3 -> {//incia del jugador
                binding.attacklayout.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player1)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
                }, 800)
            }

        }

    }


    private fun aptersupdate() {

        var tablero1 = AdaptadorCuadrante(this.player1, 1) {
            val items: RecyclerView = binding.recViewMytab
            items.setHasFixedSize(true)
        }

        binding.recViewMytab.layoutManager = GridLayoutManager(this, 10)
        binding.recViewMytab.adapter = tablero1
        binding.recViewMytab.adapter

        var adaptador = AdaptadorCuadrante(this.player2, 2) {
            val items: RecyclerView = binding.recView2Enemytab
            items.setHasFixedSize(true)

        }
        binding.recView2Enemytab.layoutManager = GridLayoutManager(this, 10)
        binding.recView2Enemytab.adapter = adaptador


        binding.tablero.visibility = View.GONE
        binding.tablero.visibility = View.VISIBLE

    }


    override fun applyAttack(player: Player, isImpact: Boolean) {
        this.player2 = player
        aptersupdate()
        for (it in this.player2.getMyTablero()) {
            db.collection("games").document(gameId).collection("Tablero").document(rivaluser).collection(rivaluser).document(it.id.toString()).set(it)
        }

        var impact = DialogAnimations(this, 2, "$nick")
        var notimpact = DialogAnimations(this, 3, "$nick")

        if (isImpact) {
            numTurns++
            impact.show(supportFragmentManager, "acert Impact")
            db.collection("games").document(gameId).collection("turn").document(gameId)
                .set(Turn(numTurns, nick))
            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(1, nick))
            checktab(1)
        } else {
            numTurns++
            notimpact.show(supportFragmentManager, "Fail Impact")
            db.collection("games").document(gameId).collection("turn").document(gameId)
                .set(Turn(numTurns, rivalnick))
            db.collection("games").document(gameId).collection("animation").document(gameId)
                .set(Animation(2, nick))
            checktab(0)
        }
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (isFullscreen) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        fullscreenContentControls.visibility = View.GONE
        isFullscreen = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        hideHandler.removeCallbacks(showPart2Runnable)
        hideHandler.postDelayed(hidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            fullscreenContent.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            fullscreenContent.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        isFullscreen = true

        // Schedule a runnable to display UI elements after a delay
        hideHandler.removeCallbacks(hidePart2Runnable)
        hideHandler.postDelayed(showPart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    /**
     * Schedules a call to hide() in [delayMillis], canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private const val UI_ANIMATION_DELAY = 300
    }
}