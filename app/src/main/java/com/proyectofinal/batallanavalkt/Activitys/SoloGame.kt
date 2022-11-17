package com.proyectofinal.batallanavalkt.Activitys

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.AdaptadorCuadrante
import com.proyectofinal.batallanavalkt.databinding.ActivitySoloGameBinding
import com.proyectofinal.batallanavalkt.dialogs.DialogDecideTurn
import com.proyectofinal.batallanavalkt.dialogs.DialogDuelTurn
import com.proyectofinal.batallanavalkt.dialogs.DialogSelectNaves
import com.proyectofinal.batallanavalkt.dialogs.DialogSetNaves
import com.proyectofinal.batallanavalkt.models.Player
import java.io.File


class SoloGame : AppCompatActivity(), DialogDuelTurn.DialogDuelTurnListener,
    DialogDecideTurn.DialogDecideTurnListener, DialogSelectNaves.selectNavesListener, DialogSetNaves.setNavesListener {

    private lateinit var binding: ActivitySoloGameBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)

    private var player1 = Player()
    private var player2 = Player()
    private var usermail = ""
    private var boxing: Int = 0

    private val auth = Firebase.auth
    private var db = FirebaseFirestore.getInstance()
    private var bitmap: Bitmap? = null
    private lateinit var mStorage: FirebaseStorage
    private lateinit var mReference: StorageReference

    private lateinit var audioAttributes: AudioAttributes
    private lateinit var sp: SoundPool


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
        startActivity(Intent(this, Menu::class.java))
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySoloGameBinding.inflate(layoutInflater)
        intent.getStringExtra("User")?.let { usermail = it }

     setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        intent.getStringExtra("User")?.let { usermail = it }
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

        mStorage = FirebaseStorage.getInstance()
        mReference = mStorage.reference
        val imgRef = mReference.child("images/$usermail")
        val localfile = File.createTempFile("tempImg", "jpg")
        imgRef.getFile(localfile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
        }
        val currentUser = auth.currentUser
        usermail= currentUser?.email.toString()

        print(usermail)
//

        player2.setNickname("CPU")
        audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        sp = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()




        val decideNaves = DialogSelectNaves(this, player1)
        decideNaves.isCancelable = false
        decideNaves.show(supportFragmentManager, "Decidir Naves")


    }//end on create

    override fun applyNaves(player: Player) {
        this.player1 = player
        val setNaves = DialogSetNaves(this, player1)
        setNaves.isCancelable = false
        setNaves.show(supportFragmentManager, "Set Naves")
    }

    override fun applyNavesSet(player: Player) {
        this.player1 = player
        val decideTurnDialog = DialogDecideTurn(this, player1)
        decideTurnDialog.isCancelable = false
        decideTurnDialog.show(supportFragmentManager, "Decidir turno")
    }


    override fun applyDecideTurn(player: Player) {
        sp.play(boxing, 0.3f, 0.3f, 1, 0, 1f)
        this.player1 = player
        player2.setPpt((0..2).random())

        val duelTurnDialog = DialogDuelTurn(this, player1, player2)
        duelTurnDialog.isCancelable = false
        duelTurnDialog.show(supportFragmentManager, "Decidir turno")

    }

    override fun applyDuelTurn(player1: Player, player2: Player, isDraw: Boolean) {
        this.player1 = player1
        this.player2 = player2
        //Si hay empate en el piedra, papael o tijeras se repite el juego
        if (isDraw == true) {
            val decideTurnDialog = DialogDecideTurn(this, player1)
            decideTurnDialog.isCancelable = false
            decideTurnDialog.show(supportFragmentManager, "Decidir turno")
        } else {
            startGame()
        }
    }

    fun startGame(){
        binding.tablero.visibility = View.VISIBLE
        binding.crono.base = SystemClock.elapsedRealtime()


        binding.btnendgame.setOnClickListener {
            //Finalizar y cargar
            startActivity(Intent(this, Menu::class.java))
            finish()
        }

        var tablero1 = AdaptadorCuadrante(player1){
            val items: RecyclerView = binding.recViewMytab
            items.setHasFixedSize(true)
        }
        binding.recViewMytab.layoutManager = GridLayoutManager(this, 10)
        binding.recViewMytab.adapter = tablero1


        binding.crono.start()


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