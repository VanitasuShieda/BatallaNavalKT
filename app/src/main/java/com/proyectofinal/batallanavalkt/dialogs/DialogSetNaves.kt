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
    var a = 0
    var b = 0
    var c = 0
    var d = 0

    private var arrayPA = arrayOf(
        6, 7, 8, 9, 16, 17, 18, 19,
        26, 27, 28, 29, 36, 37, 38, 39,
        46, 47, 48, 49, 56, 57, 58, 59,
        66, 67, 68, 69, 76, 77, 78, 79,
        86, 87, 88, 89, 96, 97, 98, 99
    )
    private var arrayB = arrayOf(
        7, 8, 9, 17, 18, 19, 27, 28, 29,
        37, 38, 39, 47, 48, 49, 57, 58, 59,
        67, 68, 69, 77, 78, 79, 87, 88, 89,
        97, 98, 99
    )
    private var arraySB = arrayOf(
        8,
        9,
        18,
        19,
        28,
        29,
        38,
        39,
        48,
        49,
        58,
        59,
        68,
        69,
        78,
        79,
        88,
        89,
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

            val binding = inflater.inflate(R.layout.dialog_setnaves, null)
            val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
            val setnave = binding.findViewById<ImageButton>(R.id.setnave)

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


            //Se crea el adaptador y se define el evento de click (para seleccionar al monstruo)
            val adaptador = AdaptadorCuadrante(player, 1) {
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
                    reset(binding)
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

                }
            }
            lB.setOnClickListener {
                if (cantB != 0) {
                    reset(binding)
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
                }

            }
            lSB.setOnClickListener {
                if (cantSB != 0) {
                    reset(binding)
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
                }
            }
            lC.setOnClickListener {
                if (cantC != 0) {
                    reset(binding)
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
                }
            }
            lL.setOnClickListener {
                if (cantL != 0) {
                    reset(binding)
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
                }
            }

            setnave.setOnClickListener {

                for (i in 0 until items.size) {
                    items[i].background = null
                    if (items[i].findViewById<ImageView>(R.id.itemmarnave).background != null) {
                        player.getMyTablero()[i].isnave = true
                        player.getMyTablero()[i].angle = this.angle

                    }

                }

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
                reset(binding)

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

    private fun reset(binding: View) {
        val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
        val setnave = binding.findViewById<ImageButton>(R.id.setnave)
        a = 0
        b = 0
        c = 0
        d = 0
        this.angle = 0
        setnave.visibility = View.INVISIBLE
        for (i in 0 until items.size) {
            items[i].background = null
            if (!player.getMyTablero()[i].isnave) {
                items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                items[i].findViewById<ImageView>(R.id.itemmarnave).rotation = 0.toFloat()
                player.getMyTablero()[i].bg = 0
            }

        }
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
        reset(binding)


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
                            for (k in i until 4) {
                                if (k == 3)
                                    break
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }
                            if (colocar.visibility != View.INVISIBLE) {
                                player.getMyTablero()[it + i].bg = R.drawable.portaaviones_1
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_1)
                                player.getMyTablero()[it + i].bg = R.drawable.portaaviones_2
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_2)
                                player.getMyTablero()[it + i].bg = R.drawable.portaaviones_3
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_3)
                                player.getMyTablero()[it + i].bg = R.drawable.portaaviones_4
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_4)
                                player.getMyTablero()[it + i].bg = R.drawable.portaaviones_5
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_5)
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
                                    .setBackgroundResource(R.drawable.portaaviones_1)
                                player.getMyTablero()[it].bg = R.drawable.portaaviones_1
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_2)
                                player.getMyTablero()[it + 1].bg = R.drawable.portaaviones_2
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_3)
                                player.getMyTablero()[it + 2].bg = R.drawable.portaaviones_3
                                items[it + 3].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_4)
                                player.getMyTablero()[it + 3].bg = R.drawable.portaaviones_4
                                items[it + 4].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.portaaviones_5)
                                player.getMyTablero()[it + 4].bg = R.drawable.portaaviones_5
                            }
                        }
                    }
                    "B" -> {
                        i = pos % 10 % 6
                        i *= (-1)

                        if (arrayB.contains(pos)) {

                            for (k in i until 3) {
                                if (k == 2) {
                                    break
                                }

                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                player.getMyTablero()[it + i].bg = R.drawable.buqe_1
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_1)
                                player.getMyTablero()[it + i].bg = R.drawable.buqe_2
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_2)
                                player.getMyTablero()[it + i].bg = R.drawable.buqe_3
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_3)
                                player.getMyTablero()[it + i].bg = R.drawable.buqe_4
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_4)
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
                                    .setBackgroundResource(R.drawable.buqe_1)
                                player.getMyTablero()[it].bg = R.drawable.buqe_1
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_2)
                                player.getMyTablero()[it + 1].bg = R.drawable.buqe_2
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_3)
                                player.getMyTablero()[it + 2].bg = R.drawable.buqe_3
                                items[it + 3].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.buqe_4)
                                player.getMyTablero()[it + 3].bg = R.drawable.buqe_4
                            }
                        }
                    }
                    "SB" -> {
                        i = pos % 2 + 1
                        i *= (-1)
                        if (arraySB.contains(pos)) {
                            for (k in i until 2) {
                                if (k == 1)
                                    break

                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                player.getMyTablero()[it].bg = R.drawable.submarino_1
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_1)
                                player.getMyTablero()[it + i].bg = R.drawable.submarino_2
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_2)
                                player.getMyTablero()[it + i].bg = R.drawable.submarino_3
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_3)
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
                                    .setBackgroundResource(R.drawable.submarino_1)
                                player.getMyTablero()[it].bg = R.drawable.submarino_1
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_2)
                                player.getMyTablero()[it + 1].bg = R.drawable.submarino_2
                                items[it + 2].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.submarino_3)
                                player.getMyTablero()[it + 2].bg = R.drawable.submarino_3
                            }
                        }
                    }
                    "C" -> {
                        i = pos % 2
                        i *= (-1)
                        if (arrayC.contains(pos)) {
                            for (k in i until 1) {
                                if (k == 1)
                                    break
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                player.getMyTablero()[it].bg = R.drawable.crucero_1
                                items[it + i++].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_1)
                                items[it + i].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_2)
                                player.getMyTablero()[it+i].bg = R.drawable.crucero_2
                            }
                        } else {
                            for (k in 0 until 1) {
                                if (player.getMyTablero()[it + k].isnave) {
                                    colocar.visibility = View.INVISIBLE
                                    break
                                }
                            }

                            if (colocar.visibility != View.INVISIBLE) {
                                items[it].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_1)
                                player.getMyTablero()[it].bg = R.drawable.crucero_1
                                items[it + 1].findViewById<ImageView>(R.id.itemmarnave)
                                    .setBackgroundResource(R.drawable.crucero_2)
                                player.getMyTablero()[it + 1].bg = R.drawable.crucero_2
                            }
                        }
                    }
                    "L" -> {

                        if (player.getMyTablero()[it].isnave) {
                            colocar.visibility = View.INVISIBLE
                        }

                        if (colocar.visibility != View.INVISIBLE) {
                            items[it].findViewById<ImageView>(R.id.itemmarnave)
                                .setBackgroundResource(R.drawable.lancha_1)
                            player.getMyTablero()[it].bg = R.drawable.lancha_1
                        }
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
                    if (!player.getMyTablero()[i].isnave) {
                        items[i].findViewById<ImageView>(R.id.itemmarnave).background = null
                        player.getMyTablero()[i].bg = 0
                    }
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

    private fun move(it: Int, binding: View, move: String) {

        val items: RecyclerView = binding.findViewById(R.id.recView_mytab)
        val colocar: ImageButton = binding.findViewById(R.id.setnave)

        var i = 0


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
                "2" -> {//arriba
                    when (selectName) {
                        "PA" -> {
                            if (direccion == "Horizontal") {
                                if (pos > 10) {
                                    pos -= 10
                                } else if (pos < 10) {
                                    pos += 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos >= 10) {
                                        pos -= 10
                                    } else {
                                        pos += 50
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos >= 50) {
                                        pos -= 10
                                    } else {
                                        pos += 50
                                    }
                                }
                            }
                        }
                        "B" -> {
                            if (direccion == "Horizontal") {
                                if (pos > 10) {
                                    pos -= 10
                                } else if (pos < 10) {
                                    pos += 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos >= 10) {
                                        pos -= 10
                                    } else {
                                        pos += 60
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos >= 40) {
                                        pos -= 10
                                    } else {
                                        pos += 60
                                    }
                                }
                            }
                        }
                        "SB" -> {
                            if (direccion == "Horizontal") {
                                if (pos > 10) {
                                    pos -= 10
                                } else if (pos < 10) {
                                    pos += 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos >= 10) {
                                        pos -= 10
                                    } else {
                                        pos += 70
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos >= 30) {
                                        pos -= 10
                                    } else {
                                        pos += 70
                                    }
                                }
                            }
                        }
                        "C" -> {
                            if (direccion == "Horizontal") {
                                if (pos > 10) {
                                    pos -= 10
                                } else if (pos < 10) {
                                    pos += 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos >= 10) {
                                        pos -= 10
                                    } else {
                                        pos += 80
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos >= 20) {
                                        pos -= 10
                                    } else {
                                        pos += 80
                                    }
                                }
                            }
                        }
                        "L" -> {
                            if (pos >= 10) {
                                pos -= 10
                            } else if (pos <= 10) {
                                pos += 90
                            }
                        }
                    }
                }
                "4" -> {//izquierda
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

                            if (direccion == "Horizontal") {
                                if (pos == 0) {
                                    pos += 7
                                } else if (pos % 10 == 0) {
                                    pos += 7
                                } else if (pos % 10 > 6) {
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
                        "SB" -> {
                            if (direccion == "Horizontal") {
                                if (pos == 0) {
                                    pos += 8
                                } else if (pos % 10 == 0) {
                                    pos += 8
                                } else if (pos % 10 > 7) {
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
                        "C" -> {
                            if (direccion == "Horizontal") {
                                if (pos == 0) {
                                    pos += 9
                                } else if (pos % 10 == 0) {
                                    pos += 9
                                } else if (pos % 10 > 8) {
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
                        "L" -> {
                            if (pos == 0) {
                                pos += 10
                            } else if (pos % 10 == 0)
                                pos += 10

                            pos--
                        }
                    }
                }
                "6" -> { //derecha
                    pos++
                    when (selectName) {
                        "PA" -> {
                            if (direccion == "Horizontal") {
                                if (pos % 10 > 5)
                                    pos -= 6
                            } else {
                                if (pos % 10 == 0) {
                                    pos -= 10
                                }
                            }
                        }
                        "B" -> {
                            if (direccion == "Horizontal") {
                                if (pos % 10 > 6)
                                    pos -= 7
                            } else {
                                if (pos % 10 == 0) {
                                    pos -= 10
                                }
                            }
                        }
                        "SB" -> {
                            if (direccion == "Horizontal") {
                                if (pos % 10 > 7)
                                    pos -= 8
                            } else {
                                if (pos % 10 == 0) {
                                    pos -= 10
                                }
                            }
                        }
                        "C" -> {
                            if (direccion == "Horizontal") {
                                if (pos % 10 > 8)
                                    pos -= 9
                            } else {
                                if (pos % 10 == 0) {
                                    pos -= 10
                                }
                            }
                        }
                        "L" -> {
                            if (pos % 10 == 0)
                                pos -= 10
                        }

                    }//end position nave

                }
                "8" -> {
                    when (selectName) {
                        "PA" -> {//bajamos nave
                            if (direccion == "Horizontal") {
                                if (pos < 90) {
                                    pos += 10
                                } else if (pos > 90) {
                                    pos -= 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos < 49) {
                                        pos += 10
                                    } else {
                                        pos -= 50
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos < 90) {
                                        pos += 10
                                    } else {
                                        pos -= 50
                                    }
                                }
                            }
                        }
                        "B" -> {
                            if (direccion == "Horizontal") {
                                if (pos < 90) {
                                    pos += 10
                                } else if (pos > 90) {
                                    pos -= 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos < 59) {
                                        pos += 10
                                    } else {
                                        pos -= 60
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos < 90) {
                                        pos += 10
                                    } else {
                                        pos -= 60
                                    }
                                }
                            }
                        }
                        "SB" -> {
                            if (direccion == "Horizontal") {
                                if (pos < 90) {
                                    pos += 10
                                } else if (pos > 90) {
                                    pos -= 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos < 69) {
                                        pos += 10
                                    } else {
                                        pos -= 70
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos < 90) {
                                        pos += 10
                                    } else {
                                        pos -= 70
                                    }
                                }
                            }
                        }
                        "C" -> {
                            if (direccion == "Horizontal") {
                                if (pos < 90) {
                                    pos += 10
                                } else if (pos > 90) {
                                    pos -= 90
                                }
                            } else {
                                if (this.angle == 90 || this.angle == -270) {//hacia abajo
                                    if (pos < 79) {
                                        pos += 10
                                    } else {
                                        pos -= 80
                                    }
                                } else if (this.angle == 270 || this.angle == -90) {//hacia arriba
                                    if (pos < 90) {
                                        pos += 10
                                    } else {
                                        pos -= 80
                                    }
                                }
                            }
                        }
                        "L" -> {
                            if (pos < 90) {
                                pos += 10
                            } else if (pos > 90) {
                                pos -= 90
                            }
                        }
                    }
                }
                "izq" -> {
                    if (direccion == "Horizontal") {
                        direccion = "Vertical"
                    } else {
                        direccion = "Horizontal"
                    }

                    this.angle -= 90
                }
                "der" -> {
                    if (direccion == "Vertical") {
                        direccion = "Horizontal"
                    } else {
                        direccion = "Vertical"
                    }

                    this.angle += 90
                }

            }//end tipo de movimiento

            if (move == "der" || move == "izq") {
                when (this.angle) {
                    90, -270 -> {
                        // abajo  90, -270
                        a = 10
                        b = 20
                        c = 30
                        d = 40
                    }
                    -90, 270 -> {
                        // arriba -90, 270
                        a = -10
                        b = -20
                        c = -30
                        d = -40
                    }
                    180, -180 -> {
                        //izquierda 180, -180
                        a = -1
                        b = -2
                        c = -3
                        d = -4
                    }
                    360, -360, 0 -> {
                        //derecha 360, -360
                        this.angle = 0
                        a = 1
                        b = 2
                        c = 3
                        d = 4
                    }
                }// fin when angulos
            }//condicionales de giro
            else if (this.angle == 0) {
                a = 1
                b = 2
                c = 3
                d = 4
            }


            


            when (selectName) {
                "PA" -> {
                    when (this.angle) {
                        90, -270 -> {
                            if (pos > 59) {
                                pos = pos % 10 + 50
                            }
                        }
                        -90, 270 -> {
                            if (pos < 39) {
                                pos = pos % 10 + 40
                            }
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
                        }
                    }// fin when separador de lados

                    // abajo 90, -270  // arriba -90, 270
                    // izquierda 180, -180 // derecha 360, -360
                    for (k in 0 until 4) {

                        if (this.angle == 270 || this.angle == -90) { //arriba
                            if (player.getMyTablero()[pos + (k * (-10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == -270 || this.angle == 90) { //abajo
                            if (player.getMyTablero()[pos + (k * (10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == 180 || this.angle == -180) { //izquierda
                            if (player.getMyTablero()[pos + (k * (-1))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else {//derecha
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }
                    }

                    // 10 abajo -10 arriba 1 derecha -1 izquierda
                    if (colocar.visibility != View.INVISIBLE) {
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.portaaviones_1)
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos].bg = R.drawable.portaaviones_1
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.portaaviones_2)
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + a].bg = R.drawable.portaaviones_2
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.portaaviones_3)
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + b].bg = R.drawable.portaaviones_3
                        items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.portaaviones_4)
                        items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + c].bg = R.drawable.portaaviones_4
                        items[pos + d].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.portaaviones_5)
                        items[pos + d].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + d].bg = R.drawable.portaaviones_5
                    }// end draw permite


                }//end PA
                "B" -> {
                    when (this.angle) {
                        90, -270 -> {//abajo
                            if (pos > 69) {
                                pos = pos % 10 + 60
                            }
                        }
                        -90, 270 -> {//arriba
                            if (pos < 29) {
                                pos = pos % 10 + 30
                            }
                        }
                        180, -180 -> { //izquierda
                            if (pos % 10 < 4) {
                                when (pos % 10) {
                                    0 -> pos += 3
                                    1 -> pos += 2
                                    2 -> pos += 1
                                }
                            }
                        }
                        0 -> { //derecha
                            if (pos % 10 > 6) {
                                when (pos % 10) {
                                    7 -> pos -= 1
                                    8 -> pos -= 2
                                    9 -> pos -= 3
                                }
                            }
                        }
                    }// fin when separador de lados

                    // abajo 90, -270  // arriba -90, 270
                    // izquierda 180, -180 // derecha 360, -360
                    for (k in 0 until 3) {


                        if (this.angle == 270 || this.angle == -90) { //arriba
                            if (player.getMyTablero()[pos + (k * (-10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == -270 || this.angle == 90) { //abajo
                            if (player.getMyTablero()[pos + (k * (10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == 180 || this.angle == -180) { //izquierda
                            if (player.getMyTablero()[pos + (k * (-1))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else {//derecha
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }
                    }

                    // 10 abajo -10 arriba 1 derecha -1 izquierda
                    if (colocar.visibility != View.INVISIBLE) {
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.buqe_1)
                        player.getMyTablero()[pos].bg = R.drawable.buqe_1
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.buqe_2)
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + a].bg = R.drawable.buqe_2
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.buqe_3)
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + b].bg = R.drawable.buqe_3
                        items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.buqe_4)
                        items[pos + c].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + c].bg = R.drawable.buqe_4
                    }// end draw permite


                }//end  B
                "SB" -> {
                    when (this.angle) {
                        90, -270 -> {//abajo
                            if (pos > 79) {
                                pos = pos % 10 + 70
                            }
                        }
                        -90, 270 -> {//arriba
                            if (pos < 19) {
                                pos = pos % 10 + 20
                            }
                        }
                        180, -180 -> { //izquierda
                            if (pos % 10 < 3) {
                                when (pos % 10) {
                                    0 -> pos += 2
                                    1 -> pos += 1
                                }
                            }
                        }
                        0 -> { //derecha
                            if (pos % 10 > 7) {
                                when (pos % 10) {
                                    8 -> pos -= 1
                                    9 -> pos -= 2
                                }
                            }
                        }
                    }// fin when separador de lados

                    // abajo 90, -270  // arriba -90, 270
                    // izquierda 180, -180 // derecha 360, -360
                    for (k in 0 until 2) {


                        if (this.angle == 270 || this.angle == -90) { //arriba
                            if (player.getMyTablero()[pos + (k * (-10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == -270 || this.angle == 90) { //abajo
                            if (player.getMyTablero()[pos + (k * (10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == 180 || this.angle == -180) { //izquierda
                            if (player.getMyTablero()[pos + (k * (-1))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else {//derecha
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }
                    }

                    // 10 abajo -10 arriba 1 derecha -1 izquierda
                    if (colocar.visibility != View.INVISIBLE) {
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.submarino_1)
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos].bg = R.drawable.submarino_1
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.submarino_2)
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + a].bg = R.drawable.submarino_2
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.submarino_3)
                        items[pos + b].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + b].bg = R.drawable.submarino_3
                    }// end draw permite
                }//end SB
                "C" -> {
                    when (this.angle) {
                        90, -270 -> {//abajo
                            if (pos > 89) {
                                pos = pos % 10 + 80
                            }
                        }
                        -90, 270 -> {//arriba
                            if (pos < 9) {
                                pos = pos % 10 + 10
                            }
                        }
                        180, -180 -> { //izquierda
                            if (pos % 10 < 2) {
                                when (pos % 10) {
                                    0 -> pos += 1
                                }
                            }
                        }
                        0 -> { //derecha
                            if (pos % 10 > 8) {
                                when (pos % 10) {
                                    9 -> pos -= 1
                                }
                            }
                        }
                    }// fin when separador de lados

                    // abajo 90, -270  // arriba -90, 270
                    // izquierda 180, -180 // derecha 360, -360
                    for (k in 0 until 1) {

                        if (this.angle == 270 || this.angle == -90) { //arriba
                            if (player.getMyTablero()[pos + (k * (-10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == -270 || this.angle == 90) { //abajo
                            if (player.getMyTablero()[pos + (k * (10))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else if (this.angle == 180 || this.angle == -180) { //izquierda
                            if (player.getMyTablero()[pos + (k * (-1))].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        } else {//derecha
                            if (player.getMyTablero()[pos + k].isnave) {
                                colocar.visibility = View.INVISIBLE
                                break
                            }
                        }
                    }

                    // 10 abajo -10 arriba 1 derecha -1 izquierda
                    if (colocar.visibility != View.INVISIBLE) {
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.crucero_1)
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos].bg = R.drawable.crucero_1
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.crucero_2)
                        items[pos + a].findViewById<ImageView>(R.id.itemmarnave)
                            .rotation = this.angle.toFloat()
                        player.getMyTablero()[pos + b].bg = R.drawable.crucero_2
                    }// end draw permite
                }//end C
                "L" -> {
                    if (player.getMyTablero()[pos].isnave) {
                        colocar.visibility = View.INVISIBLE
                    }

                    if (colocar.visibility != View.INVISIBLE) {
                        items[pos].findViewById<ImageView>(R.id.itemmarnave)
                            .setBackgroundResource(R.drawable.lancha_1)
                        player.getMyTablero()[pos].bg = R.drawable.lancha_1
                    }
                }
            }//end tipo de nave
        }//if select nave

    }//fin fun move


}
