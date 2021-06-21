package com.example.readproject

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(val context: Context, name: String, version: Int) :SQLiteOpenHelper(context, name, null, version) {

    private final val dbName="Read.db"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table if not exists user(uid integer primary key autoincrement,name varchar not null unique,img text)")
        db.execSQL("create table if not exists books(" +
                "bid integer primary key autoincrement," +
                "img text," +
                "name varchar," +
                "author varchar," +
                "publisher varchar," +
                "intro text," +
                "rating varchar," +
                "date varchar)")
        db.execSQL("create table if not exists reviews(id integer primary key autoincrement,bid integer,uid integer,date varchar,comment text,rating varchar,likeNum integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}