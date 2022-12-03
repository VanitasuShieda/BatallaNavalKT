package com.proyectofinal.batallanavalkt.Activitys

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.VideoView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.AdaptadorCuadrante
import com.proyectofinal.batallanavalkt.databinding.ActivitySoloGameBinding
import com.proyectofinal.batallanavalkt.dialogs.*
import com.proyectofinal.batallanavalkt.models.Player
import java.io.File


class SoloGame : AppCompatActivity(), DialogDuelTurn.DialogDuelTurnListener,
    DialogDecideTurn.DialogDecideTurnListener, DialogSelectNaves.selectNavesListener,
    DialogSetNaves.setNavesListener, DialogSelectCuadrante.setapplyAttackListener, DialogResults.DialogResultsListener {

    private lateinit var binding: ActivitySoloGameBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)

    private var player1 = Player()
    private var player2 = Player()
    private var navesp1 = 0
    private var navesp2 = 0
    private var acerto = -1
    private var arraycreado = false
    private var recorrido = 0
    private var fallidos = 0
    private var direccion = 0
    private var direccionaux = 0
    private var posiblenave = arrayOf(-1, -10, 1, 10)
    private var arraytocheck: MutableList<Int> = arrayListOf(0)


    private var usermail = ""
    private var nick = ""
    private var boxing: Int = 0

    private val auth = Firebase.auth
    private var db = FirebaseFirestore.getInstance()
    private var bitmap: Bitmap? = null
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mReference: StorageReference

    private lateinit var mp: MediaPlayer
    private lateinit var audioAttributes: AudioAttributes
    private lateinit var sp: SoundPool
    private lateinit var bgVideo: VideoView;
    private var numTurns = 0



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
            var intent = Intent(this, Menu::class.java)
            intent.putExtra("User", usermail)
            intent.putExtra("Nick", nick)
            mp.stop()
            startActivity(intent)


        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySoloGameBinding.inflate(layoutInflater)
        intent.getStringExtra("Nick")?.let { nick = it }
        intent.getStringExtra("User")?.let { usermail = it }

        audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        sp = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()
        mp = MediaPlayer.create(this, R.raw.games)
        mp.setVolume(0.5f, 0.5f)
        mp.start()
        mp.isLooping = true

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

        //PREPARATIVOS==========================================================================================================
        val currentUser = auth.currentUser
        usermail = currentUser?.email.toString()
        mStorage = FirebaseStorage.getInstance()
        mReference = mStorage.reference

        binding.numTurnosTextView.text = "Turno $numTurns"

        val imgRef = mReference.child("images/$usermail")
        val localfile = File.createTempFile("tempImg", "jpg")
        imgRef.getFile(localfile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
        }

        this.player1.setNickname(nick)


        binding.attacklayout.visibility = View.GONE
        binding.endturnlayout.visibility = View.GONE

        player2.setNickname("CPU")
        audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        sp = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()


        val decideNaves = DialogSelectNaves(this, this.player1)
        decideNaves.isCancelable = false
        decideNaves.show(supportFragmentManager, "Decidir Naves")


    }//end on create

    override fun applyAttack(player: Player, isImpact: Boolean) {
        this.player2 = player

        aptersupdate()

        var impact = DialogAnimations(this, 2, "$usermail")
        var notimpact = DialogAnimations(this, 3, "$usermail")

        if (isImpact) {
            impact.show(supportFragmentManager, "acert Impact")
            checktab(1)
        } else {
            notimpact.show(supportFragmentManager, "Fail Impact")
            checktab(0)
        }

    }

    override fun applyNaves(player: Player) {
        this.player1 = player
        val setNaves = DialogSetNaves(this, this.player1)
        setNaves.isCancelable = false
        setNaves.show(supportFragmentManager, "Set Naves")
    }

    override fun applyNavesSet(player: Player) {
        this.player1 = player
        this.player2.setcantPA(this.player1.getcantPA())
        this.player2.setcantB(this.player1.getcantB())
        this.player2.setcantSB(this.player1.getcantSB())
        this.player2.setcantC(this.player1.getcantC())
        this.player2.setcantL(this.player1.getcantL())

        val decideTurnDialog = DialogDecideTurn(this, this.player1)
        decideTurnDialog.isCancelable = false
        decideTurnDialog.show(supportFragmentManager, "Decidir turno")
    }


    override fun applyDecideTurn(player: Player) {
        sp.play(boxing, 0.3f, 0.3f, 1, 0, 1f)
        this.player1 = player
        this.player2.setPpt((0..2).random())

        val duelTurnDialog = DialogDuelTurn(this, this.player1, this.player2)
        duelTurnDialog.isCancelable = false
        duelTurnDialog.show(supportFragmentManager, "Decidir turno")

    }

    override fun applyDuelTurn(player1: Player, player2: Player, isDraw: Boolean) {
        this.player1 = player1
        this.player2 = player2
        //Si hay empate en el piedra, papael o tijeras se repite el juego
        if (isDraw == true) {
            val decideTurnDialog = DialogDecideTurn(this, this.player1)
            decideTurnDialog.isCancelable = false
            decideTurnDialog.show(supportFragmentManager, "Decidir turno")
        } else {
            startGame()
        }
    }

    fun startGame() {
        binding.tablero.visibility = View.VISIBLE
        binding.crono.base = SystemClock.elapsedRealtime()

        navesp1 = this.player1.getusetable()
        this.player2.setusetable(player1.getusetable())
        navesp2 = this.player2.getusetable()

        binding.numNaves1.text = navesp1.toString() + "/" + player1.getusetable().toString()
        binding.numNaves2.text = navesp2.toString() + "/" + player2.getusetable().toString()


        randomicePC()

        aptersupdate()

        for (i in 0 until player2.getMyTablero().size) {
            player2.getMyTablero()[i].isenable = true
        }

        binding.crono.start()



        if (player2.getIsFirst()) {
            checktab(2)
        } else {
            checktab(3)
        }

        binding.btnattack.setOnClickListener {
            val atacar = DialogSelectCuadrante(this, this.player2)
            atacar.isCancelable = false
            atacar.show(supportFragmentManager, "Seleccionar Cuadrante")

        }




        binding.btnendgame.setOnClickListener {
            //Finalizar y cargar

            val increment = FieldValue.increment(1)
            val database = FirebaseFirestore.getInstance()
            val p1 =  FirebaseFirestore.getInstance().collection("users").document(usermail)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(p1)
                transaction.update(p1, "win", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }


            val resultsDialog = DialogResults(this, player1, player2, 2)
            resultsDialog.isCancelable = false
            resultsDialog.show(supportFragmentManager, "Resultados")


        }
    }

    override fun applyDialogResults(res: String) {
        var intent = Intent(this, Menu::class.java)
        intent.putExtra("User", usermail)
        intent.putExtra("Nick", nick)
        startActivity(intent)
        mp.stop()

        //si pierde o gana subir estadistica
        finish()
    }



    private fun aptersupdate() {
        var adaptador = AdaptadorCuadrante(this.player2, 2) {
            val items: RecyclerView = binding.recView2Enemytab
            items.setHasFixedSize(true)
        }

        binding.recView2Enemytab.layoutManager = GridLayoutManager(this, 10)
        binding.recView2Enemytab.adapter = adaptador

        var tablero1 = AdaptadorCuadrante(this.player1, 1) {
            val items: RecyclerView = binding.recViewMytab
            items.setHasFixedSize(true)
        }
        binding.recViewMytab.layoutManager = GridLayoutManager(this, 10)
        binding.recViewMytab.adapter = tablero1

        binding.recViewMytab.adapter
        binding.tablero.visibility = View.GONE
        binding.tablero.visibility = View.VISIBLE

    }

    private fun turnoCPU() {
        var impact = DialogAnimations(this, 2, "CPU")
        var notimpact = DialogAnimations(this, 3, "CPU")

        var end = 99
        var start = 0
        var pos = (Math.random() * (end - start + 1)).toInt()

        if(fallidos==2){
            fallidos=0
            direccionaux=direccion
            arraycreado = false
        }

        if (acerto == -1) {




            if (this.player1.getMyTablero()[pos].isnave) {
                impact.show(supportFragmentManager, "acert Impact")
                this.player1.getMyTablero()[pos].isImpact = true
                this.player1.setusetable(this.player1.getusetable() - 1)
                acerto = pos
                checktab(0)
            } else {
                notimpact.show(supportFragmentManager, "Fail Impact")
                this.player1.getMyTablero()[pos].isImpactMar = true
                checktab(1)
            }
        } else {

            direccion = (Math.random() * (3 - 0 + 1)).toInt()

            if(direccion ==  direccionaux){
                direccion = (Math.random() * (3 - 0 + 1)).toInt()
                if(direccion ==  direccionaux){
                    direccion = (Math.random() * (3 - 0 + 1)).toInt()
                    if(direccion ==  direccionaux){
                       direccion = (Math.random() * (3 - 0 + 1)).toInt()
                        if(direccion ==  direccionaux){
                            direccion = (Math.random() * (3 - 0 + 1)).toInt()
                            if(direccion ==  direccionaux){
                                direccion = (Math.random() * (3 - 0 + 1)).toInt()
                            }
                        }
                    }
                }
            }

            if(!arraycreado){
                    arraytocheck.removeAll(arraytocheck)
                recorrido=0
//                arrayOf(-1, -10, 1, 10)
                when(direccion){
                    0->{
                        if(acerto>5) {
                            arraytocheck.add(acerto - 1)
                            arraytocheck.add(acerto - 2)
                            arraytocheck.add(acerto - 3)
                            arraytocheck.add(acerto - 4)
                        }else{
                            when(acerto){
                                0-> {
                                    arraytocheck.add(acerto+1)
                                    arraytocheck.add(acerto+2)
                                    arraytocheck.add(acerto+3)
                                    arraytocheck.add(acerto+4)
                                }
                                1->{
                                    arraytocheck.add(acerto-1)
                                    arraytocheck.add(acerto+1)
                                    arraytocheck.add(acerto+2)
                                    arraytocheck.add(acerto+3)

                                }
                                2->{
                                    arraytocheck.add(acerto-2)
                                    arraytocheck.add(acerto-1)
                                    arraytocheck.add(acerto+1)
                                    arraytocheck.add(acerto+2)
                                }
                            }
                        }
                        arraycreado=true
                    }
                    1->{
                        if(acerto > 40) {
                            arraytocheck.add(acerto - 10)
                            arraytocheck.add(acerto - 20)
                            arraytocheck.add(acerto - 30)
                            arraytocheck.add(acerto - 40)

                        }else{
                            if(acerto>10){
                                arraytocheck.add(acerto-1)
                                arraytocheck.add(acerto-2)
                                arraytocheck.add(acerto-3)
                                arraytocheck.add(acerto-4)
                            }else if(acerto > 20){
                                arraytocheck.add(acerto - 10)
                                arraytocheck.add(acerto - 20)
                            }else if (acerto > 30){
                                arraytocheck.add(acerto - 10)
                                arraytocheck.add(acerto - 20)
                                arraytocheck.add(acerto - 30)
                            }
                        }

                        arraycreado = true
                    }
                    2->{
                        if(acerto<96){
                            arraytocheck.add(acerto + 1)
                            arraytocheck.add(acerto + 2)
                            arraytocheck.add(acerto + 3)
                            arraytocheck.add(acerto + 4)
                        }else{
                            when(acerto){
                                97-> {
                                    arraytocheck.add(acerto+1)
                                    arraytocheck.add(acerto+2)
                                    arraytocheck.add(acerto-1)
                                    arraytocheck.add(acerto-2)
                                }
                                98->{
                                    arraytocheck.add(acerto-1)
                                    arraytocheck.add(acerto+1)
                                    arraytocheck.add(acerto-2)
                                    arraytocheck.add(acerto-3)

                                }
                                99->{
                                    arraytocheck.add(acerto-2)
                                    arraytocheck.add(acerto-1)
                                    arraytocheck.add(acerto-3)
                                    arraytocheck.add(acerto-4)
                                }
                            }
                        }
                        arraycreado=true
                    }
                    3->{
                        if(acerto<60){
                            arraytocheck.add(acerto + 10)
                            arraytocheck.add(acerto + 20)
                            arraytocheck.add(acerto + 30)
                            arraytocheck.add(acerto + 40)
                        }else if(acerto in 60..69){
                            arraytocheck.add(acerto - 10)
                            arraytocheck.add(acerto + 10)
                            arraytocheck.add(acerto + 20)
                            arraytocheck.add(acerto + 30)
                        }else if(acerto in 70..79){
                            arraytocheck.add(acerto + 10)
                            arraytocheck.add(acerto + 20)
                            arraytocheck.add(acerto - 10)
                            arraytocheck.add(acerto - 20)
                        }
                        else if(acerto in 80..89){
                            arraytocheck.add(acerto + 10)
                            arraytocheck.add(acerto - 20)
                            arraytocheck.add(acerto - 10)
                            arraytocheck.add(acerto - 30)
                        }else if(acerto in 90..99){
                            arraytocheck.add(acerto - 10)
                            arraytocheck.add(acerto - 20)
                            arraytocheck.add(acerto - 30)
                            arraytocheck.add(acerto - 40)
                        }
                        arraycreado=true
                    }
                }
            }

            if(recorrido==arraytocheck.size){
                recorrido--
                pos = arraytocheck[recorrido]
                arraycreado=false
            }else{
                pos = arraytocheck[recorrido]
                if(this.player1.getMyTablero()[pos].isImpact || this.player1.getMyTablero()[pos].isImpactMar){
                    recorrido++
                    if(recorrido==arraytocheck.size){
                        arraycreado=false
                        pos = (Math.random() * (end - start + 1)).toInt()
                        acerto = -1
                    }      else{
                        pos = arraytocheck[recorrido]
                    }

                }else{
                    recorrido++
                }
            }


            if (this.player1.getMyTablero()[pos].isnave) {
                impact.show(supportFragmentManager, "acert Impact")
                this.player1.getMyTablero()[pos].isImpact = true
                this.player1.setusetable(this.player1.getusetable() - 1)
                checktab(0)
            } else {
                notimpact.show(supportFragmentManager, "Fail Impact")
                this.player1.getMyTablero()[pos].isImpactMar = true
                fallidos++
                checktab(1)
            }

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


    private fun checktab(turn: Int) {

        if (numTurns == 70) {
            val resultsDialog = DialogResults(this, player1, player2, 2)
            resultsDialog.isCancelable = false
            resultsDialog.show(supportFragmentManager, "Resultados")
        }

        if (this.player2.getusetable() == 0) {
            val increment = FieldValue.increment(1)
            val database = FirebaseFirestore.getInstance()
            val p1 =  FirebaseFirestore.getInstance().collection("users").document(usermail)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(p1)
                transaction.update(p1, "wins", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }

            val resultsDialog = DialogResults(this, player1, player2, 0)
            resultsDialog.isCancelable = false
            resultsDialog.show(supportFragmentManager, "Resultados")
        }

        if (this.player1.getusetable() == 0) {
            val increment = FieldValue.increment(1)
            val database = FirebaseFirestore.getInstance()
            val p1 =  FirebaseFirestore.getInstance().collection("users").document(usermail)
            database.runTransaction { transaction ->
                val snapshot = transaction.get(p1)
                transaction.update(p1, "loses", increment)
            }.addOnFailureListener {
                throw Exception(it.message)
            }

            val resultsDialog = DialogResults(this, player1, player2, 1)
            resultsDialog.isCancelable = false
            resultsDialog.show(supportFragmentManager, "Resultados")
        }

        when (turn) {
            0 -> {//turno de CPU
                aptersupdate()
//                binding.endturnlayout.visibility = View.GONE

                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player2)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
                    Handler(Looper.getMainLooper()).postDelayed({
                        turnoCPU()
                    }, 4000)
                    numTurns++
                    binding.attacklayout.visibility = View.GONE
                    binding.numTurnosTextView.text = "Turno $numTurns"
                    navesp1 = player1.getusetable()
                    navesp2 = player2.getusetable()
                    binding.numNaves1.text =
                        navesp1.toString() + "/" + player1.getusetable().toString()
                    binding.numNaves2.text =
                        navesp2.toString() + "/" + player2.getusetable().toString()
                }, 14200)
            }
            1 -> {//turno del jugador
                binding.endturnlayout.visibility = View.GONE
                binding.attacklayout.visibility = View.VISIBLE
                aptersupdate()
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player1)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
                    numTurns++
                    binding.numTurnosTextView.text = "Turno $numTurns"
                    navesp1 = player1.getusetable()
                    navesp2 = player2.getusetable()
                    binding.numNaves1.text =
                        navesp1.toString() + "/" + player1.getusetable().toString()
                    binding.numNaves2.text =
                        navesp2.toString() + "/" + player2.getusetable().toString()
                }, 14200)
            }
            2 -> {//incia PC
                aptersupdate()
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player2)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
//                    binding.endturnlayout.visibility = View.GONE
                    binding.attacklayout.visibility = View.GONE
                    Handler(Looper.getMainLooper()).postDelayed({
                        turnoCPU()
                    }, 2000)

                    numTurns++
                    binding.numTurnosTextView.text = "Turno $numTurns"
                    navesp1 = player1.getusetable()
                    navesp2 = player2.getusetable()
                    binding.numNaves1.text =
                        navesp1.toString() + "/" + player1.getusetable().toString()
                    binding.numNaves2.text =
                        navesp2.toString() + "/" + player2.getusetable().toString()
                }, 800)
            }
            3 -> {//incia del jugador
                aptersupdate()
                binding.attacklayout.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val turnMsgDialog = DialogTurnMsg(this, player1)
                    turnMsgDialog.show(supportFragmentManager, "mensaje de turno")
//                    binding.endturnlayout.visibility = View.GONE

                    numTurns++
                    binding.numTurnosTextView.text = "Turno $numTurns"
                    navesp1 = player1.getusetable()
                    navesp2 = player2.getusetable()
                    binding.numNaves1.text =
                        navesp1.toString() + "/" + player1.getusetable().toString()
                    binding.numNaves2.text =
                        navesp2.toString() + "/" + player2.getusetable().toString()
                }, 800)
            }
        }

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


    private fun randomicePC() {

        var PA = this.player2.getcantPA()
        var B = this.player2.getcantB()
        var SB = this.player2.getcantSB()
        var C = this.player2.getcantC()
        var L = this.player2.getcantL()
        var start = 0
        var end = 99
        var pos = 0
        var direction = 0
        var direction2 = 0
        var angle: Int = 0
        var colocar = true
        var a = 0
        var b = 0
        var c = 0
        var d = 0


        while (PA != 0 || B != 0 || SB != 0 || C != 0 || L != 0) {

            if (PA != 0) {
                colocar = true
                pos = (Math.random() * (end - start + 1)).toInt()
                direction = (Math.random() * (3 - 0 + 1)).toInt()
                direction2 = (Math.random() * (1)).toInt()
                when (direction) {
                    0 -> angle = 0
                    1 -> {
                        if (direction2 == 0)
                            angle = 90
                        else
                            angle = -90
                    }
                    2 -> {
                        if (direction2 == 0)
                            angle = 120
                        else
                            angle = -120
                    }
                    3 -> {
                        if (direction2 == 0)
                            angle = 270
                        else
                            angle = -270
                    }
                }
                when (angle) {
                    90, -270 -> {
                        if (pos > 59) {
                            pos = pos % 10 + 50
                        }
                        // abajo  90, -270
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    -90, 270 -> {
                        if (pos < 39) {
                            pos = pos % 10 + 40
                        }
                        // arriba -90, 270
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    180, -180 -> { //izquierda
                        if (pos % 10 < 5) {
                            when (pos % 10) {
                                0 -> pos += 4
                                1 -> pos += 3
                                2 -> pos += 2
                                3 -> pos += 1
                            }
                        }
                        //izquierda 180, -180
                        a = -1
                        b = -2
                        c = -3
                        d = -4
                    }
                    0 -> { //derecha
                        if (pos % 10 > 5) {
                            when (pos % 10) {
                                6 -> pos -= 1
                                7 -> pos -= 2
                                8 -> pos -= 3
                                9 -> pos -= 4
                            }
                        }
                        angle = 0
                        a = 1
                        b = 2
                        c = 3
                        d = 4
                    }
                }// fin when separador de lados

                for (k in 0 until 4) {

                    if (angle == 270 || angle == -90) { //arriba
                        if (this.player2.getMyTablero()[pos + (k * (-10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == -270 || angle == 90) { //abajo
                        if (this.player2.getMyTablero()[pos + (k * (10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == 180 || angle == -180) { //izquierda
                        if (this.player2.getMyTablero()[pos + (k * (-1))].isnave) {
                            colocar = false
                            break
                        }
                    } else {//derecha
                        if (this.player2.getMyTablero()[pos + k].isnave) {
                            colocar = false
                            break
                        }
                    }
                }

                if(pos + a > 99 || pos + b > 99 || pos + c > 99 || pos + d > 99 ){
                    colocar = false
                }else if(pos - a < 0 || pos - b < 0 || pos - c < 0 || pos - d < 0 ){
                    colocar = false
                }

                // 10 abajo -10 arriba 1 derecha -1 izquierda
                if (colocar) {
                    player2.getMyTablero()[pos].isnave = true
                    player2.getMyTablero()[pos].angle = angle
                    player2.getMyTablero()[pos].bg = R.drawable.portaaviones_1

                    player2.getMyTablero()[pos + a].isnave = true
                    player2.getMyTablero()[pos + a].angle = angle
                    player2.getMyTablero()[pos + a].bg = R.drawable.portaaviones_2

                    player2.getMyTablero()[pos + b].isnave = true
                    player2.getMyTablero()[pos + b].angle = angle
                    player2.getMyTablero()[pos + b].bg = R.drawable.portaaviones_3

                    player2.getMyTablero()[pos + c].isnave = true
                    player2.getMyTablero()[pos + c].angle = angle
                    player2.getMyTablero()[pos + c].bg = R.drawable.portaaviones_4

                    player2.getMyTablero()[pos + d].isnave = true
                    player2.getMyTablero()[pos + d].angle = angle
                    player2.getMyTablero()[pos + d].bg = R.drawable.portaaviones_5

                    PA--
                }// end if permite
            }//end if portaviones

            if (B != 0) {
                colocar = true
                pos = (Math.random() * (end - start + 1)).toInt()
                direction = (Math.random() * (3 - 0 + 1)).toInt()
                direction2 = (Math.random() * (1)).toInt()
                when (direction) {
                    0 -> angle = 0
                    1 -> {
                        if (direction2 == 0)
                            angle = 90
                        else
                            angle = -90
                    }
                    2 -> {
                        if (direction2 == 0)
                            angle = 120
                        else
                            angle = -120
                    }
                    3 -> {
                        if (direction2 == 0)
                            angle = 270
                        else
                            angle = -270
                    }
                }

                when (angle) {
                    90, -270 -> {//abajo
                        if (pos > 69) {
                            pos = pos % 10 + 60
                        }
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    -90, 270 -> {//arriba
                        if (pos < 29) {
                            pos = pos % 10 + 30
                        }
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    180, -180 -> { //izquierda
                        if (pos % 10 < 4) {
                            when (pos % 10) {
                                0 -> pos += 3
                                1 -> pos += 2
                                2 -> pos += 1
                            }
                        }
                        a = -1
                        b = -2
                        c = -3
                        d = -4
                    }
                    0 -> { //derecha
                        if (pos % 10 > 6) {
                            when (pos % 10) {
                                7 -> pos -= 1
                                8 -> pos -= 2
                                9 -> pos -= 3
                            }
                        }
                        angle = 0
                        a = 1
                        b = 2
                        c = 3
                        d = 4
                    }
                }// fin when separador de lados

                for (k in 0 until 3) {
                    if (angle == 270 || angle == -90) { //arriba
                        if (this.player2.getMyTablero()[pos + (k * (-10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == -270 || angle == 90) { //abajo
                        if (this.player2.getMyTablero()[pos + (k * (10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == 180 || angle == -180) { //izquierda
                        if (this.player2.getMyTablero()[pos + (k * (-1))].isnave) {
                            colocar = false
                            break
                        }
                    } else {//derecha
                        if (this.player2.getMyTablero()[pos + k].isnave) {
                            colocar = false
                            break
                        }
                    }
                }

                if(pos + a > 99 || pos + b > 99 || pos + c > 99 || pos + d > 99 ){
                    colocar = false
                }else if(pos - a < 0 || pos - b < 0 || pos - c < 0 || pos - d < 0 ){
                    colocar = false
                }

                if (colocar) {
                    this.player2.getMyTablero()[pos].isnave = true
                    this.player2.getMyTablero()[pos].angle = angle
                    this.player2.getMyTablero()[pos].bg = R.drawable.buqe_1

                    this.player2.getMyTablero()[pos + a].isnave = true
                    this.player2.getMyTablero()[pos + a].angle = angle
                    this.player2.getMyTablero()[pos + a].bg = R.drawable.buqe_2

                    this.player2.getMyTablero()[pos + b].isnave = true
                    this.player2.getMyTablero()[pos + b].angle = angle
                    this.player2.getMyTablero()[pos + b].bg = R.drawable.buqe_3

                    this.player2.getMyTablero()[pos + c].isnave = true
                    this.player2.getMyTablero()[pos + c].angle = angle
                    this.player2.getMyTablero()[pos + c].bg = R.drawable.buqe_4

                    B--
                }// end if permite
            }//end if buque

            if (SB != 0) {
                colocar = true
                pos = (Math.random() * (end - start + 1)).toInt()
                direction = (Math.random() * (3 - 0 + 1)).toInt()
                direction2 = (Math.random() * (1)).toInt()
                when (direction) {
                    0 -> angle = 0
                    1 -> {
                        if (direction2 == 0)
                            angle = 90
                        else
                            angle = -90
                    }
                    2 -> {
                        if (direction2 == 0)
                            angle = 120
                        else
                            angle = -120
                    }
                    3 -> {
                        if (direction2 == 0)
                            angle = 270
                        else
                            angle = -270
                    }
                }
                when (angle) {
                    90, -270 -> {//abajo
                        if (pos > 79) {
                            pos = pos % 10 + 70
                        }
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    -90, 270 -> {//arriba
                        if (pos < 19) {
                            pos = pos % 10 + 20
                        }
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    180, -180 -> { //izquierda
                        if (pos % 10 < 3) {
                            when (pos % 10) {
                                0 -> pos += 2
                                1 -> pos += 1
                            }
                        }
                        a = -1
                        b = -2
                        c = -3
                        d = -4
                    }
                    0 -> { //derecha
                        if (pos % 10 > 7) {
                            when (pos % 10) {
                                8 -> pos -= 1
                                9 -> pos -= 2
                            }
                        }
                        a = 1
                        b = 2
                        c = 3
                        d = 4
                    }
                }// fin when separador de lados

                for (k in 0 until 2) {
                    if (angle == 270 || angle == -90) { //arriba
                        if (this.player2.getMyTablero()[pos + (k * (-10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == -270 || angle == 90) { //abajo
                        if (this.player2.getMyTablero()[pos + (k * (10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == 180 || angle == -180) { //izquierda
                        if (this.player2.getMyTablero()[pos + (k * (-1))].isnave) {
                            colocar = false
                            break
                        }
                    } else {//derecha
                        if (this.player2.getMyTablero()[pos + k].isnave) {
                            colocar = false
                            break
                        }
                    }
                }

                if(pos + a > 99 || pos + b > 99 || pos + c > 99 || pos + d > 99 ){
                    colocar = false
                }else if(pos - a < 0 || pos - b < 0 || pos - c < 0 || pos - d < 0 ){
                    colocar = false
                }

                if (colocar) {
                    this.player2.getMyTablero()[pos].isnave = true
                    this.player2.getMyTablero()[pos].angle = angle
                    this.player2.getMyTablero()[pos].bg = R.drawable.submarino_1

                    this.player2.getMyTablero()[pos + a].isnave = true
                    this.player2.getMyTablero()[pos + a].angle = angle
                    this.player2.getMyTablero()[pos + a].bg = R.drawable.submarino_2

                    this.player2.getMyTablero()[pos + b].isnave = true
                    this.player2.getMyTablero()[pos + b].angle = angle
                    this.player2.getMyTablero()[pos + b].bg = R.drawable.submarino_3

                    SB--
                }// end if permite
            }//end if SB

            if (C != 0) {
                colocar = true
                pos = (Math.random() * (end - start + 1)).toInt()
                direction = (Math.random() * (3 - 0 + 1)).toInt()
                direction2 = (Math.random() * (1)).toInt()
                when (direction) {
                    0 -> angle = 0
                    1 -> {
                        if (direction2 == 0)
                            angle = 90
                        else
                            angle = -90
                    }
                    2 -> {
                        if (direction2 == 0)
                            angle = 120
                        else
                            angle = -120
                    }
                    3 -> {
                        if (direction2 == 0)
                            angle = 270
                        else
                            angle = -270
                    }
                }

                when (angle) {
                    90, -270 -> {//abajo
                        if (pos > 89) {
                            pos = pos % 10 + 80
                        }
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    -90, 270 -> {//arriba
                        if (pos < 9) {
                            pos = pos % 10 + 10
                        }
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    180, -180 -> { //izquierda
                        if (pos % 10 < 2) {
                            when (pos % 10) {
                                0 -> pos += 1
                            }
                        }
                        a = -1
                        b = -2
                        c = -3
                        d = -4
                    }
                    0 -> { //derecha
                        if (pos % 10 > 8) {
                            when (pos % 10) {
                                9 -> pos -= 1
                            }
                        }
                        a = 1
                        b = 2
                        c = 3
                        d = 4

                    }
                }// fin when separador de lados

                for (k in 0 until 1) {
                    if (angle == 270 || angle == -90) { //arriba
                        if (this.player2.getMyTablero()[pos + (k * (-10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == -270 || angle == 90) { //abajo
                        if (this.player2.getMyTablero()[pos + (k * (10))].isnave) {
                            colocar = false
                            break
                        }
                    } else if (angle == 180 || angle == -180) { //izquierda
                        if (this.player2.getMyTablero()[pos + (k * (-1))].isnave) {
                            colocar = false
                            break
                        }
                    } else {//derecha
                        if (this.player2.getMyTablero()[pos + k].isnave) {
                            colocar = false
                            break
                        }
                    }
                }

                if(pos + a > 99 || pos + b > 99 || pos + c > 99 || pos + d > 99 ){
                    colocar = false
                }else if(pos - a < 0 || pos - b < 0 || pos - c < 0 || pos - d < 0 ){
                    colocar = false
                }

                if (colocar) {
                    this.player2.getMyTablero()[pos].isnave = true
                    this.player2.getMyTablero()[pos].angle = angle
                    this.player2.getMyTablero()[pos].bg = R.drawable.crucero_1

                    this.player2.getMyTablero()[pos + a].isnave = true
                    this.player2.getMyTablero()[pos + a].angle = angle
                    this.player2.getMyTablero()[pos + a].bg = R.drawable.crucero_2
                    C--
                }// end if permite
            }//end if crucero

            if (L != 0) {
                colocar = true
                pos = (Math.random() * (end - start + 1)).toInt()
                direction = (Math.random() * (3 - 0 + 1)).toInt()
                direction2 = (Math.random() * (1)).toInt()
                when (direction) {
                    0 -> angle = 0
                    1 -> {
                        if (direction2 == 0)
                            angle = 90
                        else
                            angle = -90
                    }
                    2 -> {
                        if (direction2 == 0)
                            angle = 120
                        else
                            angle = -120
                    }
                    3 -> {
                        if (direction2 == 0)
                            angle = 270
                        else
                            angle = -270
                    }
                }

                if(pos + a > 99 || pos + b > 99 || pos + c > 99 || pos + d > 99 ){
                    colocar = false
                }else if(pos - a < 0 || pos - b < 0 || pos - c < 0 || pos - d < 0 ){
                    colocar = false
                }

                if (this.player2.getMyTablero()[pos].isnave) {
                    colocar = false
                }

                if (colocar) {
                    this.player2.getMyTablero()[pos].isnave = true
                    this.player2.getMyTablero()[pos].angle = angle
                    this.player2.getMyTablero()[pos].bg = R.drawable.lancha_1

                    L--
                }// end if permite

            }//end if lancha


        }//end while


    }//end radomice tablero
}