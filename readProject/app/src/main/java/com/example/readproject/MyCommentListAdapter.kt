package com.example.readproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_my_review.view.*

class MyCommentListAdapter (private val commentList: ArrayList<bookReview>,val context: Context) : RecyclerView.Adapter<MyCommentListAdapter.CommentViewHolder>(),View.OnClickListener {

    private lateinit var mCommentList:ArrayList<bookReview>

    init {
        mCommentList=commentList
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myRatingBar: AppCompatRatingBar = itemView.findViewById(R.id.my_ratingBar_review)
        val date: TextView = itemView.findViewById(R.id.tv_review_date)
        val comment: TextView=itemView.findViewById(R.id.tv_my_comment_content)
        val pic: ImageView = itemView.findViewById(R.id.image_book_review)
        val name: TextView = itemView.findViewById(R.id.name_review)
        val bookRatingBar: AppCompatRatingBar = itemView.findViewById(R.id.ratingBar_book_review)
        val info: TextView = itemView.findViewById(R.id.info_review)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_my_review, parent,false)
        return CommentViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mCommentList.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        val comment = mCommentList[position]
        val book=BookDao(context).queryBookbyId(comment.bid)
        val user=UserDao(context).queryUser(comment.uid)
        holder.myRatingBar.setRating(java.lang.Float.valueOf(comment.rating) / 2)
        holder.date.setText(comment.updated)
        holder.comment.setText(comment.comments)
        Glide.with(context).load(book!!.pic).into(holder.pic)
        holder.name.setText(book.name)
        holder.bookRatingBar.setRating(java.lang.Float.valueOf(book.rating) / 2)
        holder.info.setText(book.author+"/"+book.publisher)

        if(onItemClickListener!=null){
            holder.itemView.delete_more.setOnClickListener { onItemClickListener?.onclick(holder.itemView.delete_more,position) }
            holder.itemView.setOnClickListener { onItemClickListener?.onclick(holder.itemView,position) }
        }
    }

    private var onItemClickListener: OnBookItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnBookItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onClick(v: View?) {
        val position = v?.getTag() as Int; //getTag()获取数据
        if (onItemClickListener != null) {
            if (v != null) {
                when (v.getId()){
                    R.id.delete_more-> {
                        onItemClickListener!!.onclick(v,position)
                    }
                    else->{
                        onItemClickListener!!.onclick(v,position)
                    }
                }
            }
        }
    }

    fun setCommentList(list:ArrayList<bookReview>){
        mCommentList=list
    }

}