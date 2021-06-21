package com.example.readproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
    }

    fun addData(view: View?){
        val helper = DatabaseHelper(this,"ReadProject.db",1)
        val db=helper.writableDatabase
        val sql= "alter table user add passWord varchar"
        db.execSQL(sql)
        val sql1 ="update user set passWord='123456' where uid=1"
        val sql2 ="update user set passWord='123456' where uid=2"
        val sql3 ="update user set passWord='123456' where uid=3"
        val sql4 ="update user set passWord='123456' where uid=4"
        val sql5 ="update user set passWord='123456' where uid=5"
        db.execSQL(sql1)
        db.execSQL(sql2)
        db.execSQL(sql3)
        db.execSQL(sql4)
        db.execSQL(sql5)
    }
}