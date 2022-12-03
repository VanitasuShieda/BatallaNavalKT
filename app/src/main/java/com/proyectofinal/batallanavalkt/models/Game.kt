package com.proyectofinal.batallanavalkt.models

data class Game(
    var id: String = "",
    var name: String = "",
    var status: String = "",
    var users: List<String> = emptyList()
)
