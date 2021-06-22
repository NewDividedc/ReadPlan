package com.example.readproject.shelfchildFragment

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readproject.MainActivity
import com.example.readproject.R
import com.example.readproject.dao.Book
import com.example.readproject.dao.Booklist
import com.example.readproject.dao.BooklistDao
import kotlinx.android.synthetic.main.shelf_child_list_content_frag.*

class listContentFragment() : Fragment() {

    var book_list : Booklist? = null
    var listid : Int = 0
    var adapter : ListContentAdapter? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d("listContentFragment","onCreateView")
        return inflater.inflate(R.layout.shelf_child_list_content_frag, container, false)
    }

//    init {
//        adapter = ListContentAdapter(book_list!!.bookList)
//        Log.d("listContentFragment","initintiinit")
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        val bundle = arguments
//        listid = bundle!!.getInt("listID")
        listid = PreferenceManager.getDefaultSharedPreferences(context).getInt("listID",1)
        book_list = BooklistDao(requireContext()).getListfromDB(listid)
//        initData(listid)
        val layoutManager = LinearLayoutManager(activity)
        listContentRecyclerView.layoutManager = layoutManager
        adapter = ListContentAdapter(book_list!!.bookList)
        Log.d("listContentFragment", book_list!!.bookList.size.toString())
        listContentRecyclerView.adapter = adapter
        if (adapter == null) {
            Log.d("adapter","nulnulnulnulll")
        }
    }

    fun refresh(adapter_refresh: ListContentAdapter,new_booklist: Booklist){
        Log.d("listContentFragment","refreshshshshssh")
        if (adapter_refresh == null) {
            Log.d("adapter","nulnulnulnulll")
        }
        adapter_refresh!!.setList(new_booklist.bookList)
        adapter_refresh!!.notifyDataSetChanged()
        //showUIchange(MainActivity(),new_booklist.bookList)
    }

    private fun showUIchange(activity: MainActivity,new_book_list: List<Book>){
        activity.runOnUiThread {
            Log.d("listContentFragment","showUIchange")
            adapter = ListContentAdapter(new_book_list)
            adapter!!.setList(new_book_list)
            adapter!!.notifyDataSetChanged()
            Log.d("showUIchange",new_book_list.size.toString())
        }
    }
    inner class ListContentAdapter(val booklist: List<Book>) : RecyclerView.Adapter<ListContentAdapter.ViewHolder>(){
        var booklist_inner : List<Book>?
        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val bookImage : ImageView = view.findViewById(R.id.list_book_image)
            val bookName : TextView = view.findViewById(R.id.list_book_name)
            val bookProgress : TextView = view.findViewById(R.id.list_book_progress)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_content_item,parent,false)
            val holder = ViewHolder(view)
            holder.itemView.setOnClickListener {
                val book = booklist_inner?.get(holder.adapterPosition)
                Toast.makeText(context,book!!.bookName,Toast.LENGTH_LONG).show()
            }
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = booklist_inner?.get(position)
            Log.d("onBindViewHolder",book!!.bookName)
            holder.bookImage.setImageResource(book!!.bookImage)
            holder.bookName.text = book.bookName
            val tmp : Int = (book.readPages / book.allPages) * 100
            holder.bookProgress.text = resources.getString(R.string.list_book_progress,tmp) + "%"
        }

        override fun getItemCount() = booklist.size

        fun setList(new_booklist_inner: List<Book>){
            booklist_inner = new_booklist_inner
            Log.d("setList", booklist_inner!!.size.toString())
        }

        init {
            Log.d("init","123123123123123123123")
            booklist_inner = booklist
        }
    }
}