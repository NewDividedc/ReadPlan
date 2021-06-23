package com.example.readproject.dao

import android.content.Context
import com.example.readproject.database.DatabaseHelper
import com.example.readproject.shelfchildFragment.GridItem

class BooklistDao(val context: Context) {
    private val helper = DatabaseHelper(context,"ReadProject.db",1)

    fun insertLists(booklist: Booklist){
        val db = helper.writableDatabase
        val sql = "insert into booklists values(" + booklist.listID + "," + booklist.listName +"," + booklist.listImage + ")"
        db.execSQL(sql)
        helper.close()
    }
    fun createListTable(booklist: Booklist){
        val db = helper.writableDatabase
        val sql = "create table if not exists booklist_" + booklist.listID +
                "(bookID integer primary key," +
                "bookName varchar," +
                "bookImage integer)"
        db.execSQL(sql)
        helper.close()
        updateListTable(booklist)
    }
    fun updateListTable(booklist: Booklist){
        val db = helper.writableDatabase
        for (book in booklist.bookList){
            val sql = "insert into booklist_" + booklist.listID +" values(" +
                    book.bookID + "," +
                    book.bookName + "," +
                    book.bookImage + ")"
            db.execSQL(sql)
        }
        helper.close()
    }
    fun getListfromDB(listID: Int) : Booklist?{
        val db = helper.writableDatabase
        var listid : Int = 1
        var listname : String? = null
        var listimage : Int = 0
        var book_list = ArrayList<Book>()
        val bookShelfDao = BookShelfDao(context)

        var cursor = db.query("booklists",null,"listID=?",arrayOf(listID.toString()),null,null,null)
        if(cursor.moveToFirst()){
            do {
                listid = cursor.getInt(cursor.getColumnIndex("listID"))
                listname = cursor.getString(cursor.getColumnIndex("listName"))
                listimage = cursor.getInt(cursor.getColumnIndex("listImage"))
            } while (cursor.moveToNext())
        }
        cursor = db.query("booklist_"+listid,null,null,null,null,null,null,null)
        if(cursor.moveToFirst()){
            do {
                val bookid = cursor.getInt(cursor.getColumnIndex("bookID"))
                val book = bookShelfDao.queryBookbyId(bookid)
                if (book != null) {
                    book_list.add(book)
                }
            } while (cursor.moveToNext())
        }
        return if (listname != null) {
            Booklist(listid,listname,listimage,book_list)
        } else {
            null
        }
    }
}