package com.example.readproject

import android.content.Context

class ReviewDao (context: Context){

    val helper=DatabaseHelper(context,"ReadProject.db",1)

    fun addReview(review:bookReview){
        val db = helper.writableDatabase
        val sql="insert into reviews(bid,uid,date,comment,rating,likeNum) values("+
                review.bid+","+
                review.uid+",'"+
                review.updated+"','"+
                review.comments+"','"+
                review.rating+"',"+
                review.like+")"
        db.execSQL(sql)
    }

    fun queryReview(bid:Int):ArrayList<bookReview>{
        val reviewList = ArrayList<bookReview>()
        val db = helper.writableDatabase
        val cursor = db.query("reviews",null,"bid=?", arrayOf(bid.toString()),null,null,null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val uid = cursor.getInt(cursor.getColumnIndex("uid"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val comment = cursor.getString(cursor.getColumnIndex("comment"))
                val rating = cursor.getString(cursor.getColumnIndex("rating"))
                val likeNum =  cursor.getInt(cursor.getColumnIndex("likeNum"))

                val review = bookReview(id,bid,uid,date,comment,rating,likeNum)
                reviewList.add(review)
            }while (cursor.moveToNext())
        }
        return reviewList
    }

    fun deleteReview(id:Int){
        val db = helper.writableDatabase
        val sql = "delete from reviews where id="+id
        db.execSQL(sql)
    }

    fun queryReviewByUid(uid:Int):ArrayList<bookReview>{
        val reviewList = ArrayList<bookReview>()
        val db = helper.writableDatabase
        val cursor = db.query("reviews",null,"uid=?", arrayOf(uid.toString()),null,null,null)
        if (cursor.moveToFirst()){
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val bid = cursor.getInt(cursor.getColumnIndex("bid"))
                val date = cursor.getString(cursor.getColumnIndex("date"))
                val comment = cursor.getString(cursor.getColumnIndex("comment"))
                val rating = cursor.getString(cursor.getColumnIndex("rating"))
                val likeNum =  cursor.getInt(cursor.getColumnIndex("likeNum"))
                val review = bookReview(id,bid,uid,date,comment,rating,likeNum)
                reviewList.add(review)
            }while (cursor.moveToNext())
        }
        return reviewList
    }
}