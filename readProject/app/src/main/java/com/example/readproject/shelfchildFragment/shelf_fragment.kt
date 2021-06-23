package com.example.readproject.shelfchildFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.readproject.R
import com.example.readproject.dao.Book
import com.example.readproject.dao.BookShelfDao

class shelf_fragment: Fragment(), AdapterView.OnItemClickListener {
    var GridItemList = ArrayList<GridItem>()
    var bookList = ArrayList<Book>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shelf_child_fragmentshelf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridView: GridView = view.findViewById(R.id.allBooks)
        initBookList()
        //val adapter = GridViewAdapter(requireContext(),R.layout.shelf_book_item,GridItemList)
        val adapter = MyBaseAdapter()
        gridView.adapter = adapter
        gridView.onItemClickListener = this

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(context,bookList[position].bookName,Toast.LENGTH_LONG).show()
        val intent = Intent(context,ClockActivity::class.java).apply {
            val bookid = bookList[position].bookID
            val bookTmp = BookShelfDao(requireActivity()).queryBookbyId(bookid)
            if (bookTmp != null){
                putExtra("bookID",bookid)
                putExtra("bookName",bookTmp.bookName)
                putExtra("readTime",bookTmp.readTime)
            }
        }
        startActivityForResult(intent,1)
    }

    private fun initBookList(){
        val book1 = BookShelfDao(requireActivity()).queryBookbyId(1)
        val book2 = BookShelfDao(requireActivity()).queryBookbyId(2)
        val book3 = BookShelfDao(requireActivity()).queryBookbyId(3)
        val book4 = BookShelfDao(requireActivity()).queryBookbyId(4)
        val book5 = BookShelfDao(requireActivity()).queryBookbyId(5)
        val book6 = BookShelfDao(requireActivity()).queryBookbyId(6)

        bookList.add(book1!!)
        bookList.add(book2!!)
        bookList.add(book3!!)
        bookList.add(book4!!)
        bookList.add(book5!!)
        bookList.add(book6!!)

        GridItemList.add(GridItem(book1.bookName,book1.bookImage))
        GridItemList.add(GridItem(book2.bookName,book2.bookImage))
        GridItemList.add(GridItem(book3.bookName,book3.bookImage))
        GridItemList.add(GridItem(book4.bookName,book4.bookImage))
        GridItemList.add(GridItem(book5.bookName,book5.bookImage))
        GridItemList.add(GridItem(book6.bookName,book6.bookImage))

    }

    inner class MyBaseAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return GridItemList.size
        }

        override fun getItem(position: Int): Any {
            return GridItemList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(
            position: Int,
            view: View?,
            parent: ViewGroup
        ): View {
            var convertView = view
            val holder: ViewHolder
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.shelf_book_item,parent,false)
                holder = ViewHolder()
                holder.mTexTView = convertView.findViewById(R.id.book_name)
                holder.imageView = convertView.findViewById(R.id.book_image)
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder.mTexTView?.text = GridItemList[position].name
            GridItemList[position].image?.let { holder.imageView!!.setBackgroundResource(it) }
            //Glide.with(context).load(GridItemList[position].image).into(holder.imageView)
            return convertView!!
        }

        internal inner class ViewHolder {
            var mTexTView: TextView? = null
            var imageView: ImageView? = null
        }
    }
}

