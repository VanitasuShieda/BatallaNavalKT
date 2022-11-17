package com.proyectofinal.batallanavalkt.dialogs

import android.app.ActionBar
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.media.Image
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.proyectofinal.batallanavalkt.R
import com.proyectofinal.batallanavalkt.adapters.AdaptadorCuadrante
import com.proyectofinal.batallanavalkt.databinding.DialogSetnavesBinding
import com.proyectofinal.batallanavalkt.models.Player

class DialogSetNaves(context: Context, private val player: Player) : DialogFragment() {

    private lateinit var listener: setNavesListener

    private lateinit var selectName: String
    private var cantPA = player.getcantPA()
    private var cantB = player.getcantB()
    private var cantSB = player.getcantSB()
    private var cantC = player.getcantC()
    private var cantL = player.getcantL()
    private var pos = 0
    private var isSelect = false
    private var direccion = "Horizontal"
    private var angle = 0

    private var arrayPA = arrayOf(
        6,
        7,
        8,
        9,
        10,
        16,
        17,
        18,
        19,
        20,
        26,
        27,
        28,
        29,
        30,
        36,
        37,
        38,
        39,
        40,
        46,
        47,
        48,
        49,
        50,
        56,
        57,
        58,
        59,
        60,
        66,
        67,
        68,
        69,
        70,
        76,
        77,
        78,
        79,
        80,
        86,
        87,
        88,
        89,
        90,
        96,
        97,
        98,
        99
    )
    private var arrayB = arrayOf(
        7,
        8,
        9,
        10,
        17,
        18,
        19,
        20,
        27,
        28,
        29,
        30,
        37,
        38,
        39,
        40,
        47,
        48,
        49,
        50,
        57,
        58,
        59,
        60,
        67,
        68,
        69,
        70,
        77,
        78,
        79,
        80,
        87,
        88,
        89,
        90,
        97,
        98,
        99
    )
    private var arraySB = arrayOf(
        8,
        9,
        10,
        18,
        19,
        20,
        28,
        29,
        30,
        38,
        39,
        40,
        48,
        49,
        50,
        58,
        59,
        60,
        68,
        69,
        70,
        78,
        79,
        80,
        88,
        89,
        90,
        98,
        99
    )
    private var arrayC =
        arrayOf(9, 10, 19, 20, 29, 30, 39, 40, 49, 50, 59, 60, 69, 70, 79, 80, 89, 90, 99)

    interface setNavesListener {
        fun applyNavesSet(player: Player)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;
            //vínculo con el layout selectmonsters_dialog.xml
            val binding = inflater.inflate(R.layout.dialog_setnaves, null)
            val lPA =
                binding.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.layoutPA)
            val lSB =
                binding.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.layoutSB)
            val lB =
                binding.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.layoutB)
            val lC =
                binding.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.layoutC)
            val lL =
                binding.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.layoutL)
            val cPA = binding.findViewById<TextView>(R.id.cantidadnave1)
            cPA.text = cantPA.toString()
            val cB = binding.findViewById<TextView>(R.id.cantidadnave2)
            cB.text = cantB.toString()
            val cSB = binding.findViewById<TextView>(R.id.cantidadnave3)
            cSB.text = cantSB.toString()
            val cC = binding.findViewById<TextView>(R.id.cantidadnave4)
            cC.text = cantC.toString()
            val cL = binding.findViewById<TextView>(R.id.cantidadnave5)
            cL.text = cantL.toString()

            val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
            val setnave = binding.findViewById<ImageButton>(R.id.setnave)
            setnave.visibility = View.INVISIBLE


            //Se crea el adaptador y se define el evento de click (para seleccionar al monstruo)
            val adaptador = AdaptadorCuadrante(player) {
                //Se asigna el evento click que selecciona una imagen
                pos = it
                onItemSelect(it, binding, player)
            }
            val recview = binding.findViewById<RecyclerView>(R.id.recView_mytab)
            recview.layoutManager = GridLayoutManager(context, 10)
            recview.adapter = adaptador

            binding.findViewById<ImageButton>(R.id.padderecha).setOnClickListener {
                move(pos, binding, "6")

            }

            binding.findViewById<ImageButton>(R.id.padizquierda).setOnClickListener {
                move(pos, binding, "4")

            }

            binding.findViewById<ImageButton>(R.id.padabajo).setOnClickListener {
                move(pos, binding, "8")
            }

            binding.findViewById<ImageButton>(R.id.padarriba).setOnClickListener {
                move(pos, binding, "2")
            }

            binding.findViewById<ImageButton>(R.id.izquierda).setOnClickListener {
                move(pos, binding, "izq")
            }

            binding.findViewById<ImageButton>(R.id.derecha).setOnClickListener {
                move(pos, binding, "der")
            }


            lPA.setOnClickListener {
                if (cantPA != 0) {
                    angle = 0
                    if (it.background == null) {
                        it.setBackgroundResource(R.drawable.seleccionado)
                        selectName = "PA"
                        isSelect = true
                        lB.background = null
                        lSB.background = null
                        lL.background = null
                        lC.background = null
                    }//quita la selección si se vuelve a hacer click
                    else {
                        isSelect = false
                        it.background = null
                        selectName = ""
                    }
                    for (i in 0 until items.size) {
                        items[i].background = null
                        if (!player.getMyTablero()[i].isnave)
                            items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    }
                    setnave.visibility = View.INVISIBLE
                }
            }
            lB.setOnClickListener {

                if (cantB != 0) {
                    angle = 0
                    if (it.background == null) {
                        it.setBackgroundResource(R.drawable.seleccionado)
                        selectName = "B"
                        lPA.background = null
                        lSB.background = null
                        lL.background = null
                        lC.background = null
                        isSelect = true
                    }//quita la selección si se vuelve a hacer click
                    else {
                        isSelect = false
                        it.background = null
                        selectName = ""
                    }
                    for (i in 0 until items.size) {
                        items[i].background = null
                        if (!player.getMyTablero()[i].isnave)
                            items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    }
                    setnave.visibility = View.INVISIBLE
                }

            }
            lSB.setOnClickListener {

                if (cantSB != 0) {
                    angle = 0
                    if (it.background == null) {
                        it.setBackgroundResource(R.drawable.seleccionado)
                        selectName = "SB"
                        lB.background = null
                        lPA.background = null
                        lL.background = null
                        lC.background = null
                        isSelect = true

                    }//quita la selección si se vuelve a hacer click
                    else {
                        isSelect = false
                        it.background = null
                        selectName = ""
                    }
                    for (i in 0 until items.size) {
                        items[i].background = null
                        if (!player.getMyTablero()[i].isnave)
                            items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    }
                    setnave.visibility = View.INVISIBLE
                }
            }
            lC.setOnClickListener {
                if (cantC != 0) {
                    angle = 0
                    if (it.background == null) {
                        it.setBackgroundResource(R.drawable.seleccionado)
                        selectName = "C"
                        lB.background = null
                        lSB.background = null
                        lL.background = null
                        lPA.background = null
                        isSelect = true
                    }//quita la selección si se vuelve a hacer click
                    else {
                        isSelect = false
                        it.background = null
                        selectName = ""
                    }
                    for (i in 0 until items.size) {
                        items[i].background = null
                        if (!player.getMyTablero()[i].isnave)
                            items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    }
                    setnave.visibility = View.INVISIBLE
                }
            }
            lL.setOnClickListener {
                if (cantL != 0) {
                    angle = 0
                    if (it.background == null) {
                        it.setBackgroundResource(R.drawable.seleccionado)
                        selectName = "L"
                        lB.background = null
                        lSB.background = null
                        lPA.background = null
                        lC.background = null
                        isSelect = true
                    }//quita la selección si se vuelve a hacer click
                    else {
                        isSelect = false
                        it.background = null
                        selectName = ""
                    }
                    for (i in 0 until items.size) {
                        items[i].background = null
                        if (!player.getMyTablero()[i].isnave)
                            items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    }
                    setnave.visibility = View.INVISIBLE
                }

            }

            setnave.setOnClickListener {
                angle = 0
                for (i in 0 until items.size) {
                    items[i].background = null
                    if (items[i].findViewById<ImageView>(R.id.itemmarnave).background != null)
                        player.getMyTablero()[i].isnave = true
                }
                setnave.visibility = View.INVISIBLE
                when (selectName) {
                    "PA" -> {
                        cantPA -= 1
                        cPA.text = cantPA.toString()
                    }
                    "B" -> {
                        cantB -= 1
                        cB.text = cantB.toString()
                    }
                    "SB" -> {
                        cantSB -= 1
                        cSB.text = cantSB.toString()
                    }
                    "C" -> {
                        cantC -= 1
                        cC.text = cantC.toString()
                    }
                    "L" -> {
                        cantL -= 1
                        cL.text = cantL.toString()
                    }

                }
                selectName = ""
                isSelect = false
                lB.background = null
                lSB.background = null
                lPA.background = null
                lC.background = null
                lL.background = null

                val d = dialog as AlertDialog?
                if (d != null) {
                    val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
                    if (cantB == 0 && cantPA == 0 && cantSB == 0 && cantC == 0 && cantL == 0) {
                        positiveButton.visibility = View.VISIBLE
                    } else {
                        positiveButton.visibility = View.GONE
                    }

                }

            }


            //Botones del dialog
            builder.setView(binding).setPositiveButton("Ok",
                DialogInterface.OnClickListener { dialog, id ->
                    listener.applyNavesSet(player)
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }//en on create

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
            listener = context as setNavesListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(
                (context.toString() +
                        " must implement setnaves")
            )
        }
    }

    fun onItemSelect(it: Int, binding: View, player: Player) {
        val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
        val colocar: ImageButton = binding.findViewById(R.id.setnave)

        var i = 0
        angle = 0


        if (isSelect) {
            colocar.visibility = View.VISIBLE
            if (items[it].background == null) {

                for (i in 0 until items.size) {
                    items[i].background = null
                    if (!player.getMyTablero()[i].isnave)
                        items[i].findViewById<ImageView>(R.id.itemmarnave).background = null

                }

                when (selectName) {
                    "PA" -> {
                        i = pos % 5
                        i *= (-1)
                        if (arrayPA.contains(pos)) {
                            for (k in i until 5) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }
                            if (colocar.visibility != View.INVISIBLE) {
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_1)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_2)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_3)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_4)
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_5)
                            }
                        } else {
                            for (k in 0 until 5) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }
                            if (colocar.visibility != View.INVISIBLE) {
                                items[it].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_1)
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_2)
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_3)
                                items[it + 3].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_4)
                                items[it + 4].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_5)
                            }
                        }
                    }
                    "B" -> {
                        i = pos % 10 % 6
                        i *= (-1)

                        if (arrayB.contains(pos)) {
                            for (k in i until 4) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_1)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_2)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_3)
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_4)
                            }
                        } else {
                            for (k in 0 until 4) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }


                            if (colocar.visibility != View.INVISIBLE) {
                                items[it].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_1)
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_2)
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_3)
                                items[it + 3].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_4)
                            }
                        }
                    }
                    "SB" -> {
                        i = pos % 2 + 1
                        i *= (-1)
                        if (arraySB.contains(pos)) {
                            for (k in i until 3) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_1)
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_2)
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_3)
                            }
                        } else {
                            for (k in 0 until 3) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }
                            if (colocar.visibility != View.INVISIBLE) {
                                items[it].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_1)
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_2)
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_3)
                            }
                        }
                    }
                    "C" -> {
                        i = pos % 2
                        i *= (-1)
                        if (arrayC.contains(pos)) {
                            for (k in i until 2) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_1)
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_2)
                            }
                        } else {
                            for (k in 0 until 2) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                items[it].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_1)
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_2)
                            }
                        }
                    }
                    "L" -> {
                        items[it].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.lancha_1)
                    }

                }

                //selecciona al monstruo al que se le hizo click
                items[it].setBackgroundResource(R.drawable.seleccionado)

            }//quita la selección si se vuelve a hacer click
            else {

                colocar.visibility = View.INVISIBLE

                items[it].background = null
                for (i in 0 until items.size) {
                    items[i].background = null
                    if (!player.getMyTablero()[i].isnave)
                        items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                }
            }
        }


    }

    override fun onStart() {
        super.onStart()
        val d = dialog as AlertDialog?
        if (d != null) {
            val positiveButton: Button = d.getButton(Dialog.BUTTON_POSITIVE) as Button
            positiveButton.visibility = View.GONE

        }
    }

    fun move(it: Int, binding: View, move: String) {

        val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
        val colocar: ImageButton = binding.findViewById(R.id.setnave)

        var i = 0
        var a = 0
        var b = 0
        var c = 0
        var d = 0
        println("Incia: $pos")
        if (isSelect) {
            colocar.visibility = View.VISIBLE

            //elimina los fondos de las demas casillas que no sean nada
            for (i in 0 until items.size) {
                items[i].background = null
                if (!player.getMyTablero()[i].isnave) {
                    items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                    items[i].findViewById<ImageView>(R.id.itemmarnave).rotation = 0.toFloat()
                }

            }

            when (move) {
                "2" -> {
                    when (selectName) {
                        "PA" -> {
                            if (direccion == "Horizontal") {
                                if (pos > 10) {
                                    pos -= 10
                                } else if (pos < 10) {
                                    pos += 90
                                }
                            } else {
                                

                            }
                        }
                        "B" -> {
                            if (pos == 0) {
                                pos += 7
                            } else if (pos % 10 == 0) {
                                pos += 7
                            } else if (pos % 10 > 6) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }

                        }
                        "SB" -> {
                            if (pos == 0) {
                                pos += 8
                            } else if (pos % 10 == 0) {
                                pos += 8
                            } else if (pos % 10 > 7) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }
                        }
                        "C" -> {
                            if (pos == 0) {
                                pos += 9
                            } else if (pos % 10 == 0) {
                                pos += 9
                            } else if (pos % 10 > 8) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }
                        }
                        "L" -> {
                            if (pos == 0) {
                                pos += 10
                            } else if (pos % 10 == 0)
                                pos += 10

                            pos--
                        }
                    }
                }
                "4" -> {
                    when (selectName) {
                        "PA" -> {
                            if (direccion == "Horizontal") {
                                if (pos == 0) {
                                    pos += 6
                                } else if (pos % 10 == 0) {
                                    pos += 6
                                } else if (pos % 10 > 5) {
                                    pos -= (pos % 10 % 5)
                                }
                            } else {
                                if (pos == 0) {
                                    pos += 10
                                } else if (pos % 10 == 0)
                                    pos += 10
                            }
                            pos--
                        }
                        "B" -> {
                            if (pos == 0) {
                                pos += 7
                            } else if (pos % 10 == 0) {
                                pos += 7
                            } else if (pos % 10 > 6) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }

                        }
                        "SB" -> {
                            if (pos == 0) {
                                pos += 8
                            } else if (pos % 10 == 0) {
                                pos += 8
                            } else if (pos % 10 > 7) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }
                        }
                        "C" -> {
                            if (pos == 0) {
                                pos += 9
                            } else if (pos % 10 == 0) {
                                pos += 9
                            } else if (pos % 10 > 8) {
                                pos -= (pos % 10 % 5)
                                pos--
                            }
                        }
                        "L" -> {
                            if (pos == 0) {
                                pos += 10
                            } else if (pos % 10 == 0)
                                pos += 10

                            pos--
                        }
                    }
                }
                "6" -> {
                    pos++
                    when (selectName) {
                        "PA" -> {
                            if (pos % 10 > 5)
                                pos -= 6
                        }
                        "B" -> {
                            if (pos % 10 > 6)
                                pos -= 7
                        }
                        "SB" -> {
                            if (pos % 10 > 7)
                                pos -= 8
                        }
                        "C" -> {
                            if (pos % 10 > 8)
                                pos -= 9
                        }
                        "L" -> {
                            if (pos % 10 == 0)
                                pos -= 10
                        }

                    }//end position nave

                }
                "8" -> {
                    if (pos < 90)
                        pos += 10
                    else if (pos > 90)
                        pos -= 90
                }
                "izq" -> {
                    if (direccion == "Horizontal") {
                        direccion = "Vertical"
                    } else {
                        direccion = "Horizontal"
                    }

                    angle -= 90
                }
                "der" -> {
                    if (direccion == "Vertical") {
                        direccion = "Horizontal"
                    } else {
                        direccion = "Vertical"
                    }

                    angle += 90
                }

            }//end tipo de movimiento

            if (move == "der" || move == "izq") {
                when (angle) {
                    90, (-180), (-270) -> {
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    (-90), 180, 270 -> {
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    360, -360 -> {
                        angle = 0
                    }
                }// fin when angulos
            }//condicionales de giro


            when (selectName) {
                "PA" -> {
                    //hacia abajo 90, -270  separa de abajo
                    if (angle == 90 || angle == -270) {
                        if (pos > 59) {
                            pos = pos % 10 + 50
                        }
                    }

                    //hacia arriba -90, 270, separa derecha
                    if (angle == 270 || angle == -90 ) {
                        if (pos < 39) {
                            pos = pos % 10 + 40
                        }
                    }

                    when (direccion) {
                        "Horizontal" -> {
                            i = pos % 10 % 5
                            i *= (-1)
                            if (arrayPA.contains(pos)) {
                                for (k in i until 3) {
                                    println("K: $k  pos+k= ${pos + k}")
                                    if (k == 3)
                                        break

                                    if (player.getMyTablero()[pos + k].isnave) {
                                        colocar.visibility = View.INVISIBLE
                                        break
                                    }
                                }
                                if (colocar.visibility != View.INVISIBLE) {
                                    items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_1)
                                    items[pos + (i - 1)].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_2)
                                    items[pos + (i - 1)].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_3)
                                    items[pos + (i - 1)].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_4)
                                    items[pos + (i - 1)].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + i].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_5)
                                    items[pos + i].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                }//if si es posible colocar naves
                                //if si esta en rango de lateral derecho
                            } else {
                                for (k in 0 until 4) {
                                    if (player.getMyTablero()[pos + k].isnave) {
                                        colocar.visibility = View.INVISIBLE
                                        break
                                    }
                                }
                                if (colocar.visibility != View.INVISIBLE) {
                                    items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                            .setBackgroundResource(R.drawable.portaaviones_1)
                                    items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + 1].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_2)
                                    items[pos + 1].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + 2].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_3)
                                    items[pos + 2].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + 3].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_4)
                                    items[pos + 3].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                    items[pos + 4].findViewById<ImageView>(R.id.itemmarnave)
                                        .setBackgroundResource(R.drawable.portaaviones_5)
                                    items[pos + 4].findViewById<ImageView>(R.id.itemmarnave)
                                        .rotation = angle.toFloat()
                                }
                            }//end if pertenece a cuadrantes else horizontal
                        }
                        "Vertical" -> {

                            for (k in 0 until 4) {
                                if (angle == 270 || angle == -90) {
                                    if (player.getMyTablero()[pos + (k * (-10))].isnave) {
                                        colocar.visibility = View.INVISIBLE
                                        break
                                    }
                                } else {
                                    if (player.getMyTablero()[pos + (k * 10)].isnave) {
                                        colocar.visibility = View.INVISIBLE
                                        break
                                    }
                                }

                            }

                            if (colocar.visibility != View.INVISIBLE) {

                                items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_1)
                                items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                    .rotation = angle.toFloat()
                                items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_2)
                                items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                                    .rotation = angle.toFloat()
                                items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_3)
                                items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                                    .rotation = angle.toFloat()
                                items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_4)
                                items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                                    .rotation = angle.toFloat()
                                items[pos + d].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_5)
                                items[pos + d].findViewById<ImageView>(R.id.itemmarnave)
                                    .rotation = angle.toFloat()

                            }
                        }//end vertical
                    }//end when direccion PA
                }//end PA
                "B" -> {
                    i = pos % 10 % 6
                    i *= (-1)

                    if (arrayB.contains(pos)) {
                        for (k in i until 3) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }

                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_1)
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_2)
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_3)
                            items[pos + i].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_4)
                        }
                    } else {
                        for (k in 0 until 4) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }


                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_1)
                            items[pos + 1].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_2)
                            items[pos + 2].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_3)
                            items[pos + 3].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.buqe_4)
                        }
                    }
                }
                "SB" -> {
                    i = pos % 2 + 1
                    i *= (-1)
                    if (arraySB.contains(pos)) {
                        for (k in i until 3) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }

                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_1)
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_2)
                            items[pos + i].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_3)
                        }
                    } else {
                        for (k in 0 until 3) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }
                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_1)
                            items[pos + 1].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_2)
                            items[pos + 2].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.submarino_3)
                        }
                    }
                }
                "C" -> {
                    i = pos % 2
                    i *= (-1)
                    if (arrayC.contains(pos)) {
                        for (k in i until 2) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }

                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos + i++].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.crucero_1)
                            items[pos + i].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.crucero_2)
                        }
                    } else {
                        for (k in 0 until 2) {
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }

                        if (colocar.visibility != View.INVISIBLE) {
                            items[pos].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.crucero_1)
                            items[pos + 1].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.crucero_2)
                        }
                    }
                }
                "L" -> {
                    items[pos].findViewById<ImageView>(R.id.itemmarnave)
                        .setBackgroundResource(R.drawable.lancha_1)
                }
            }//end tipo de nave
        }//if select nave
        println("Final: $pos")
    }//fin fun move


}
