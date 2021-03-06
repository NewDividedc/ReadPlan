package com.example.readproject.database
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(val context: Context, name: String, version: Int) :SQLiteOpenHelper(context, name, null, version) {

    private final val dbName="ReadProject.db"

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
        db.execSQL("create table if not exists shelfBooks (" +
                "bookID integer primary key," +
                "bookName string," +
                "bookType integer," +
                "bookImage integer," +
                "writer text," +
                "allPages integer," +
                "readPages integer," +
                "contents text," +
                "readTime bigint)")
        Log.d("DatabaseHelper","initintitintint")
        db.execSQL("create table if not exists booklists (" +
                "listID integer primary key," +
                "listName varchar," +
                "listImage integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql = "DROP TABLE IF EXISTS " + dbName;
        if (db != null) {
            Log.d("DatabaseHelper", "onUpgrade")
            db.execSQL(sql)
            onCreate(db)
        }
    }
}