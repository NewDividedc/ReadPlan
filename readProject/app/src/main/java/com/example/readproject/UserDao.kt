package com.example.readproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserDao(val context: Context) {

    private val helper= DatabaseHelper(context,"Read.db",10)

    fun insertUser(user:User){
        val db = helper.writableDatabase
        val sql="insert into user (name,password,img) values('"+user.name+"','"+user.passWord+"','"+user.img+"')"
        db.execSQL(sql)
    }

    fun queryUser(uid:Int):UserNeed?{

        var user : UserNeed ?= null

        val db=helper.writableDatabase
        val cursor = db.query("user",null,"id=?", arrayOf(uid.toString()),null,null,null)
        if(cursor.moveToFirst()){
            do{
                val name=cursor.getString(cursor.getColumnIndex("name"))
                val passWord=cursor.getString(cursor.getColumnIndex("password"))
                val img = cursor.getString(cursor.getColumnIndex("img"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                user=UserNeed(id,name,passWord,img)
            }while (cursor.moveToNext())
        }
        return user
    }
    fun queryUserbyName(name:String):UserNeed?{

        var user : UserNeed ?= null

        val db=helper.writableDatabase
        val cursor = db.query("user",null,"name=?", arrayOf(name),null,null,null)
        if(cursor.moveToFirst()){
            do{
                val name=cursor.getString(cursor.getColumnIndex("name"))
                val passWord=cursor.getString(cursor.getColumnIndex("password"))
                val img = cursor.getString(cursor.getColumnIndex("img"))
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                user=UserNeed(id,name,passWord,img)
            }while (cursor.moveToNext())
        }
        return user
    }
}