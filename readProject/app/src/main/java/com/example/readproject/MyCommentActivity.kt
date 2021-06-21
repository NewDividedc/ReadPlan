package com.example.readproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyCommentActivity : AppCompatActivity() {

    private lateinit var commentRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_comment)

        val uid = intent.getIntExtra("uid",5)
        commentRecyclerView = findViewById(R.id.books_recycler_my_review)
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        var commentList=ReviewDao(this).queryReviewByUid(uid) as ArrayList<bookReview>

        if(!commentList.isEmpty()){
            val adapter = MyCommentListAdapter(commentList,this)
            commentRecyclerView.adapter = adapter
            adapter.setOnItemClickListener(object :OnBookItemClickListener{
                override fun onclick(view: View, position: Int) {
                    when (view.getId()){
                        R.id.delete_more->{
                            val menu = PopupMenu(this@MyCommentActivity,view);
                            menu.inflate(R.menu.deleteitem_menu)
                            menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener() {
                                item: MenuItem? ->
                                when (item!!.itemId) {
                                    R.id.removeItem -> {
                                        ReviewDao(this@MyCommentActivity).deleteReview(commentList[position].id)
                                        commentList=ReviewDao(this@MyCommentActivity).queryReviewByUid(uid) as ArrayList<bookReview>
                                        adapter.setCommentList(commentList)
                                        adapter.notifyDataSetChanged()
                                    }
                                    R.id.cancelItem -> {
                                        menu.dismiss()
                                    }
                                }
                                true
                            })
                            menu.show();
                        }
                        else->{
                            val intent2 = Intent(this@MyCommentActivity,BookDetailActivity::class.java)
                            intent2.putExtra("bid",commentList[position].bid)
                            startActivity(intent2)
                        }
                    }
                }
            })
        }
    }

    fun back(view: View){
        finish()
    }

}