package com.example.readproject.dao

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.core.database.getStringOrNull
import com.example.readproject.database.DatabaseHelper

class BookShelfDao(val context: Context) {
    private val helper = DatabaseHelper(context,"ReadProject.db",1)

    fun insertBook(book: Book){
        val db = helper.writableDatabase
        val sql = "insert into shelfBooks values(" + book.bookID + "," +
                book.bookName + "," +
                book.type + "," +
                book.bookImage + "," +
                book.writer + "," +
                book.allPages + "," +
                book.readPages + "," +
                book.contents + "," +
                book.readTime + ")"
        Log.d("sql",sql)
        db.execSQL(sql)
        helper.close()
    }

    fun queryBookbyName(bookName: String):Book?{
        val db = helper.writableDatabase
        val cursor = db.query("shelfBooks",null,"bookName=?", arrayOf(bookName),null,null,null)
        var book : Book ?= null
        if(cursor.moveToFirst()){
            do {
                val bookID = cursor.getInt(cursor.getColumnIndex("bookID"))
                val bookName = cursor.getString(cursor.getColumnIndex("bookName"))
                val bookType = cursor.getInt(cursor.getColumnIndex("bookType"))
                val bookImage = cursor.getInt(cursor.getColumnIndex("bookImage"))
                val writer = cursor.getString(cursor.getColumnIndex("writer"))
                val allPages = cursor.getInt(cursor.getColumnIndex("allPages"))
                val readPages = cursor.getInt(cursor.getColumnIndex("readPages"))
                val contents = cursor.getStringOrNull(cursor.getColumnIndex("contents"))
                val readTime = cursor.getLong(cursor.getColumnIndex("readTime"))
                book = Book(bookID,bookName,bookType,bookImage,allPages,writer,readPages,contents,readTime)
            } while (cursor.moveToNext())
        }
        return book
    }

    fun queryBookbyId(bookID: Int): Book? {
        val db = helper.writableDatabase
        val cursor = db.query("shelfBooks",null,"bookID=?", arrayOf(bookID.toString()),null,null,null)
        var book : Book ?= null
        if(cursor.moveToFirst()){
            do {
                val bookID = cursor.getInt(cursor.getColumnIndex("bookID"))
                val bookName = cursor.getString(cursor.getColumnIndex("bookName"))
                val bookType = cursor.getInt(cursor.getColumnIndex("bookType"))
                val bookImage = cursor.getInt(cursor.getColumnIndex("bookImage"))
                val writer = cursor.getString(cursor.getColumnIndex("writer"))
                val allPages = cursor.getInt(cursor.getColumnIndex("allPages"))
                val readPages = cursor.getInt(cursor.getColumnIndex("readPages"))
                val contents = cursor.getStringOrNull(cursor.getColumnIndex("contents"))
                val readTime = cursor.getLong(cursor.getColumnIndex("readTime"))
                book = Book(bookID,bookName,bookType,bookImage,allPages,writer,readPages,contents,readTime)
            } while (cursor.moveToNext())
        }
        //helper.close()
        return book
    }

    fun deleteBook(bookID: Int){
        val db = helper.writableDatabase
        val sql = "delete from shelfBooks where bookID="+ bookID.toString()
        db.execSQL(sql)
    }
    fun updateReadtime(bookid: Int,readtime: Long){
        val db = helper.writableDatabase
        val values = ContentValues()
        values.put("readTime",readtime)
        val selection = "bookID = ?"
        val selectionArgs = arrayOf<String>(bookid.toString())
        db.update(
                "shelfBooks",
                values,
                selection,
                selectionArgs
        )
        helper.close()
        Log.d("BookShelfDao","updatetime"+readtime)
    }
    fun updateReadPages(bookid: Int,readpages: Int){
        val db = helper.writableDatabase
        val values = ContentValues()
        values.put("readPages",readpages)
        val selection = "bookID = ?"
        val selectionArgs = arrayOf<String>(bookid.toString())
        db.update(
            "shelfBooks",
            values,
            selection,
            selectionArgs
        )
        helper.close()
        Log.d("BookShelfDao","readPages"+readpages)
    }

}



/*

    fun deleteBook(bid:Int){
        val db = helper.writableDatabase
        val sql = "delete from books where bid="+bid.toString()
        db.execSQL(sql)
    }
}
*/