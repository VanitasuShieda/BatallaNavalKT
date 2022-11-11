package com.proyectofinal.batallanavalkt.models

data class User(
    var online: Boolean ,
    var id: String = "",
    var nick: String = "",
    var email: String = "",
    var wins: Int = 0,
    var loses: Int = 0,
    var winspvp: Int = 0,
    var losespvp: Int = 0,
)
