package com.proyectofinal.batallanavalkt.models

import com.proyectofinal.batallanavalkt.R

//Clase para los jugadores
class Player() {
    private var nickname:String=""
    private var usermail :String=""
    private var ppt = -1 //piedra(0), papel(1) o tijera(2)
    private var isFirst = false
    private var autowin = false

    fun getNickname():String{return nickname}
    fun setNickname(nickname: String){this.nickname=nickname}
    fun getUsermail():String{return usermail}
    fun setUsermail(usermail: String){this.usermail=usermail}
    fun getPpt():Int{return ppt}
    fun setPpt(ppt:Int){this.ppt=ppt}
    fun getIsFirst():Boolean{return isFirst}
    fun setIsFirst(isFirst:Boolean){this.isFirst=isFirst}
    fun getautowin():Boolean{return autowin}
    fun setautowin(winer:Boolean){this.autowin=winer}


}
