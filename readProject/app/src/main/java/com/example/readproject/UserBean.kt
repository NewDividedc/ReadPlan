package com.example.readproject

import org.litepal.crud.LitePalSupport

data class UserBean(
    var userAccount : String,
    var passWord : String?,
    var userName : String?
) : LitePalSupport(){
    val id : Long = 0
}
