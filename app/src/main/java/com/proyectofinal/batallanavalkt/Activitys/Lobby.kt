package com.proyectofinal.batallanavalkt.Activitys

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.GamesListAdapter
import com.proyectofinal.batallanavalkt.adapters.UsersAdapter
import com.proyectofinal.batallanavalkt.databinding.ActivityLobbyBinding
import com.proyectofinal.batallanavalkt.models.Game
import com.proyectofinal.batallanavalkt.models.User
import java.io.File
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class Lobby : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private lateinit var fullscreenContent: TextView
    private lateinit var fullscreenContentControls: LinearLayout
    private val hideHandler = Handler(Looper.myLooper()!!)


    private var db = Firebase.firestore
    private var user = ""
    private var nick = ""
    private var listGames = mutableListOf(Game("","", "", emptyList()))
    private var listUsers = mutableListOf(User(java.lang.Boolean.TRUE,"","","", 0,0,0,0 ))
    private var itemant = -1
    private var itemuser = -1
    private lateinit var mp: MediaPlayer

    private val adaptador = GamesListAdapter(listGames){
        val items: RecyclerView = findViewById(R.id.listGamesRecyclerView)
        if( items[it].background==null){
            //Solo permite una seleccion a su vez
            // nueva seleccion
            items[it].setBackgroundResource(R.drawable.seleccionado)

            if(itemant != -1)
                items[itemant].setBackground(null)

            itemant = it
        }else if(items[it].background != null){
            //doble click inicia el juego
            itemant= -1
            //quita la seleccion
            items[it].setBackground(null)

            gameSelected(listGames[it])
        }
    }

    private val adaptadorUsers = UsersAdapter(listUsers){
        val items: RecyclerView = findViewById(R.id.listaUsuarios)
        if( items[it].background==null){
            //Solo permite una seleccion a su vez
            // nueva seleccion
            items[it].setBackgroundResource(R.drawable.seleccionado)

            if(itemuser != -1)
                items[itemuser].setBackground(null)

            itemuser = it
        }else if(items[it].background != null){
            //doble click inicia el juego
            itemuser= -1
            //quita la seleccion
            items[it].setBackground(null)

            userSelected(listUsers[it])
        }
    }

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
        intent.putExtra("Nick", nick)
        intent.putExtra("User", user)
        startActivity(intent)
        mp.stop()
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLobbyBinding.inflate(layoutInflater)
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


        intent.getStringExtra("User")?.let { user = it }
        intent.getStringExtra("Nick")?.let { nick = it }
        listGames.removeAt(0)
        listUsers.removeAt(0)
        //Reproducción de música
        mp = MediaPlayer.create(this, R.raw.menus)
        mp.setVolume(0.5f, 0.5f)
        mp.start()
        mp.isLooping = true
        val gamesRef = db.collection("users").document(user).collection("games").get()
            .addOnSuccessListener { documentos ->
                if(documentos != null){
                    for(gameuser in documentos){
                        gameuser.data
                        val game = Game(
                            id  = gameuser.data?.get("id").toString(),
                            name = gameuser.data?.get("name").toString(),
                            status = gameuser.data?.get("status").toString(),
                            users = listOf(gameuser.data?.get("users").toString())
                        )
                        listGames.add(game)
                    }
                }else{
                    Log.d("User", "No hay, en teoria")
                }
            }

        val UsersRef = db.collection("users").get()
            .addOnSuccessListener { documentos ->
                if(documentos != null){
                    for(users in documentos){
                        users.data
                        val usernew = User(
                            online = users.data?.get("online") as Boolean,
                            id = users.data?.get("id").toString(),
                            nick = users.data?.get("nick").toString(),
                            email = users.data?.get("email").toString(),
                            wins = users.getLong("wins")?.toInt()!!,
                            loses = users.getLong("loses")?.toInt()!!,
                            winspvp = users.getLong("winspvp")?.toInt()!!,
                            losespvp = users.getLong("losespvp")?.toInt()!!,
                        )
                        if(usernew.email.lowercase() != user){
                            listUsers.add(usernew)
                        }

                    }
                }else{
                    Log.d("User", "No hay, en teoria")
                }
            }

        //imagen del usuario
        var mStorage = FirebaseStorage.getInstance()
        var mReference = mStorage.reference
        val refnick = db.collection("users").document(user).get()

        refnick.addOnSuccessListener { document ->
            if(document != null){
                binding.usernick.text = nick
                val imgRef = mReference.child("images/$user")
                val localfile = File.createTempFile("tempImg","jpg")

                imgRef.getFile(localfile).addOnSuccessListener {
                    var bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
                    val options = RequestOptions()
                    options.centerCrop().fitCenter()
                    Glide.with(this@Lobby).load(bitmap).apply(options).into(binding.myimagemenu)

                }
            }else{
                println("Este es print de error")
            }
        }.addOnFailureListener{ exeption ->
            println(exeption)
        }


        if (user.isNotEmpty()){
            initViews()
        }


    }

    private fun initViews(){

        // binding.newGameButton.setOnClickListener { newGame() }

        binding.listGamesRecyclerView.setHasFixedSize(false)
        binding.listGamesRecyclerView.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.listGamesRecyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.listGamesRecyclerView.adapter = adaptador

        binding.listaUsuarios.setHasFixedSize(false)
        binding.listaUsuarios.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL, false)
        binding.listaUsuarios.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        binding.listaUsuarios.adapter = adaptadorUsers


        val gameRef = db.collection("users").document(user)

        gameRef.collection("games")
            .get()
            .addOnSuccessListener { games ->
                val listChats = games.toObjects(Game::class.java)
                (binding.listGamesRecyclerView.adapter as GamesListAdapter).setData(listChats)
            }

        gameRef.collection("games")
            .addSnapshotListener { games, error ->
                if(error == null){
                    games?.let {
                        val listChats = it.toObjects(Game::class.java)

                        (binding.listGamesRecyclerView.adapter as GamesListAdapter).setData(listChats)
                    }
                }
            }



        val userRef = db.collection("registros")

        userRef.get()
            .addOnSuccessListener { userdoc ->
                val listnewUsers = userdoc.toObjects(User::class.java)
                listnewUsers.removeIf { it.email.lowercase() == user }

                (binding.listaUsuarios.adapter as UsersAdapter).setData(listnewUsers)
            }

        userRef.addSnapshotListener { userdoc, error ->
            if(error == null){
                userdoc?.let {
                    val listnewUsers = it.toObjects(User::class.java)
                    listnewUsers.removeIf { it.email.lowercase() == user }
                    (binding.listaUsuarios.adapter as UsersAdapter).setData(listnewUsers)
                }
            }
        }

    }

    private fun gameSelected(game: Game){
        val intent = Intent(this, MultiplayerGame::class.java)

        if(game.status != "En Proceso"){
            newGame(game)
        }else{
            intent.putExtra("gameId", game.id)
            intent.putExtra("User", user)
            intent.putExtra("Nick", nick)
            mp.stop()
            startActivity(intent)
        }

    }

    private fun userSelected(userpick: User){

        val gameId = UUID.randomUUID().toString()
        val otherUser = userpick.email.lowercase()
        val users = listOf(user, otherUser)
        val users2 = listOf(otherUser, user)


        var game = Game()
        var gameother = Game()

        val ref = db.collection("users").document(otherUser).get()
        ref.addOnSuccessListener { document ->
            if(document != null){
                game = Game(
                    id = gameId,
                    name = "Partida Contra ${document.data?.get("nick").toString()}",
                    status = "En Proceso",
                    users = users
                )

                db.collection("games").document(gameId).set(game)
                db.collection("users").document(user).collection("games").document(gameId).set(game)
            }else{
                println("Error newGameUserNick")
            }
        }.addOnFailureListener{ exeption ->
            println(exeption)
        }

        val refother = db.collection("users").document(user).get()
        refother.addOnSuccessListener { document ->
            if(document != null){
                gameother = Game(
                    id = gameId,
                    name = "Partida Contra ${document.data?.get("nick").toString()}",
                    status = "En Proceso",
                    users = users2
                )
                db.collection("users").document(otherUser).collection("games").document(gameId).set(gameother)
            }else{
                println("Error newGameUserNick")
            }
        }.addOnFailureListener{ exeption ->
            println(exeption)
        }

        val intent = Intent(this, MultiplayerGame::class.java)
        intent.putExtra("gameId", gameId)
        intent.putExtra("User", user)
        intent.putExtra("Nick", nick)
        mp.stop()
        startActivity(intent)

    }

    private fun newGame(game: Game){
        val gameId = UUID.randomUUID().toString()
        val parts = game.users.last().split(',',']')
        val otherUser = parts[1].replace(" ","").lowercase()
        val users = listOf(user, otherUser)
        val users2 = listOf(otherUser, user)

        //sacar nicks
        var game = Game()
        var gameother = Game()

        val ref = db.collection("users").document(otherUser).get()
        ref.addOnSuccessListener { document ->
            if(document != null){
                game = Game(
                    id = gameId,
                    name = "Partida Contra ${document.data?.get("nick").toString()}",
                    status = "En Proceso",
                    users = users
                )

                db.collection("games").document(gameId).set(game)
                db.collection("users").document(user).collection("games").document(gameId).set(game)
            }else{
                println("Error newGameUserNick")
            }
        }.addOnFailureListener{ exeption ->
            println(exeption)
        }

        val refother = db.collection("users").document(user).get()
        refother.addOnSuccessListener { document ->
            if(document != null){
                gameother = Game(
                    id = gameId,
                    name = "Partida Contra ${document.data?.get("nick").toString()}",
                    status = "En Proceso",
                    users = users2
                )
                db.collection("users").document(otherUser).collection("games").document(gameId).set(gameother)
            }else{
                println("Error newGameUserNick")
            }
        }.addOnFailureListener{ exeption ->
            println(exeption)
        }

        //iniciacion del juego



        val intent = Intent(this, MultiplayerGame::class.java)
        intent.putExtra("gameId", gameId)
        intent.putExtra("User", user)
        intent.putExtra("Nick", nick)
        mp.stop()
        startActivity(intent)
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