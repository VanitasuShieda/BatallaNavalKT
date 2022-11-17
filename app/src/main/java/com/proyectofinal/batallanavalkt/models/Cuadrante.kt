package com.proyectofinal.batallanavalkt.models

data class Cuadrante(
    val id: Int,
    val name: String,
    val namenave: String,
    var isMar: Boolean,
    var isImpactMar: Boolean,
    var isImpact: Boolean,
    var isenable: Boolean,
    var bg: Int,
    var isnave: Boolean
)
