package com.example.readproject

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_book_detail.*
import java.util.*
import kotlin.collections.ArrayList

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        bid = intent.getIntExtra("bid",1)
        uid = intent.getIntExtra("uid",1)
        SendMessage(bid!!,uid!!)
    }

    fun back(view: View){
        finish()
    }

    var bid:Int?=null
    var uid:Int?=null
    var book : BooksItemOne?=null

    var commentList = ArrayList<bookReview>()

    var mDetailAdapter:bookDetailAdapter?=null

    fun SendMessage(bid:Int,uid:Int){

        book=BookDao(this).queryBookbyId(bid)

        Glide.with(this)
                .load(book!!.pic.toString())
                .asBitmap()
                .into(object :SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap?, glideAnimation: GlideAnimation<in Bitmap?>?) {
                        iv_book_img.setImageBitmap(resource)
                        iv_book_bg.setImageBitmap(Blur.apply(resource!!))
                        iv_book_bg.alpha = 0.9f
                    }
                })
        book_title.setText(book!!.name)

        val mRecyclerView = this.findViewById<RecyclerView>(R.id.recyclerView)
        val mLayoutManager = LinearLayoutManager(this@BookDetailActivity)
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        mRecyclerView.setLayoutManager(mLayoutManager)

        commentList= ReviewDao(this).queryReview(book!!.bid)

        mDetailAdapter=bookDetailAdapter(book!!,commentList,this)

        mRecyclerView.setAdapter(mDetailAdapter)
        mRecyclerView.setItemAnimator(DefaultItemAnimator())
        val mfab=this.findViewById<FloatingActionButton>(R.id.fab)
        mfab.setOnClickListener(object:View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@BookDetailActivity, BookAddReviewActivity::class.java)
                startActivityForResult(intent,1)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val cal=Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH) + 1
        val day_of_month: Int = cal.get(Calendar.DAY_OF_MONTH)
        //Toast.makeText(this,"nowisok",Toast.LENGTH_LONG).show()
        when(requestCode){
            1 -> if(resultCode == RESULT_OK){
                val returnedComment = data ?. getStringExtra("comment_return")
                val returenRating = data?.getStringExtra("rating_return")
                commentList.add(bookReview(0,bid!!,uid!!,year.toString()+"-"+month+"-"+day_of_month,returnedComment.toString(),returenRating.toString(),0))
                ReviewDao(this).addReview(bookReview(0,bid!!,uid!!,year.toString()+"-"+month+"-"+day_of_month,returnedComment.toString(),returenRating.toString(),0))
            }
        }
        mDetailAdapter!!.setReviews(commentList)
        mDetailAdapter!!.notifyDataSetChanged()
    }

}