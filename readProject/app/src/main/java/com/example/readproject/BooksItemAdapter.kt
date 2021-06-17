package com.example.readproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BooksItemAdapter(private val booksList: List<BooksItemOne>,val context: Context) : RecyclerView.Adapter<BooksItemAdapter.BooksViewHolder>() {
    inner class BooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pic: ImageView = itemView.findViewById(R.id.image_book)
        val name: TextView = itemView.findViewById(R.id.name)
        val author: TextView = itemView.findViewById(R.id.author)
        val publisher: TextView = itemView.findViewById(R.id.publisher)
        val intro: TextView = itemView.findViewById(R.id.intro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.books_item_one, parent,false)
        return BooksViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        val book = booksList[position]
        Glide.with(context).load(book.pic).into(holder.pic)
        holder.name.setText(book.name)
        holder.author.setText(book.author)
        holder.publisher.setText(book.publisher)
        holder.intro.setText(book.intro)
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener { onItemClickListener?.onclick(holder.itemView,position) }
        }
    }

    private var onItemClickListener: OnBookItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnBookItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}