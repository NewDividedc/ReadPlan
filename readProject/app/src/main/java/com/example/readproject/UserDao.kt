package com.example.readproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDao(val context: Context) {

    private val helper= DatabaseHelper(context,"ReadProject.db",1)

    fun insertUser(user:User){
        val db = helper.writableDatabase
        val sql="insert into user (name,img) values('"+user.name+"','"+user.img+"')"
        db.execSQL(sql)
    }

    fun queryUser(uid:Int):User?{

        var user : User ?= null

        val db=helper.writableDatabase
        val cursor = db.query("user",null,"uid=?", arrayOf(uid.toString()),null,null,null)
        if(cursor.moveToFirst()){
            do{
                val name=cursor.getString(cursor.getColumnIndex("name"))
                val img = cursor.getString(cursor.getColumnIndex("img"))
                user=User(uid,name,img)
            }while (cursor.moveToNext())
        }
        return user
    }
}