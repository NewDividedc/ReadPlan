package com.example.readproject

import com.example.readproject.note.ReadNote
import org.litepal.crud.LitePalSupport

data class UserBean(
        var userAccount: String?,
        var passWord: String?,
        var userName: String?
        //var noteList: List<ReadNote>?
) : LitePalSupport(){
    val id : Long = 0
}
