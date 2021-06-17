package com.example.readproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookSListFragment(val title: String,val bookList:ArrayList<BooksItemOne>):Fragment() {

    private lateinit var booksRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_books, container, false)

        booksRecyclerView = view.findViewById(R.id.books_recycler_view)
        booksRecyclerView.layoutManager = LinearLayoutManager(activity)

        val adapter = BooksItemAdapter(bookList,requireContext())
        booksRecyclerView.adapter = adapter
        adapter.setOnItemClickListener(object :OnBookItemClickListener{
            override fun onclick(view: View, position: Int) {
                val intent= Intent(activity,BookDetailActivity::class.java)
                intent.putExtra("bid",bookList[position].bid)
                intent.putExtra("uid",5)
                startActivity(intent)
            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}