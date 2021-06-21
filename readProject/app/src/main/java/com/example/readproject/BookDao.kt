package com.example.readproject

import android.content.Context

class BookDao(val context: Context) {

    private val helper = DatabaseHelper(context,"ReadProject.db",1)

    fun insertBook(book:BooksItemOne){
        val db = helper.writableDatabase
        val sql = "insert into books values(" + book.bid + ",'" +
                book.pic +"','" +
                book.name +"','" +
                book.author +"','" +
                book.publisher +"','" +
                book.intro +"','" +
                book.rating +"','" +
                book.pubdate+"')"
        db.execSQL(sql)
    }

    fun queryBookbyName(bookName:String):BooksItemOne?{
        val db = helper.writableDatabase
        val cursor = db.query("books",null,"name=?", arrayOf(bookName),null,null,null)
        var book : BooksItemOne ?=null
        if(cursor.moveToFirst()){
            do {
                val bid = cursor.getInt(cursor.getColumnIndex("bid"))
                val img = cursor.getString(cursor.getColumnIndex("img"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val author = cursor.getString(cursor.getColumnIndex("author"))
                val publisher = cursor.getString(cursor.getColumnIndex("publisher"))
                val intro = cursor.getString(cursor.getColumnIndex("intro"))
                val rating = cursor.getString(cursor.getColumnIndex("rating"))
                val date = cursor.getString(cursor.getColumnIndex("date"))

                book = BooksItemOne(bid,img,name,author,publisher,intro,rating,date)
            }while (cursor.moveToNext())
        }
        return book
    }

    fun queryBookbyId(bid:Int):BooksItemOne?{
        val db = helper.writableDatabase
        val cursor = db.query("books",null,"bid=?", arrayOf(bid.toString()),null,null,null)
        var book : BooksItemOne ?=null
        if(cursor.moveToFirst()){
            do {
                val bid = cursor.getInt(cursor.getColumnIndex("bid"))
                val img = cursor.getString(cursor.getColumnIndex("img"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val author = cursor.getString(cursor.getColumnIndex("author"))
                val publisher = cursor.getString(cursor.getColumnIndex("publisher"))
                val intro = cursor.getString(cursor.getColumnIndex("intro"))
                val rating = cursor.getString(cursor.getColumnIndex("rating"))
                val date = cursor.getString(cursor.getColumnIndex("date"))

                book = BooksItemOne(bid,img,name,author,publisher,intro,rating,date)
            }while (cursor.moveToNext())
        }
        return book
    }

    fun deleteBook(bid:Int){
        val db = helper.writableDatabase
        val sql = "delete from books where bid="+bid.toString()
        db.execSQL(sql)
    }
}