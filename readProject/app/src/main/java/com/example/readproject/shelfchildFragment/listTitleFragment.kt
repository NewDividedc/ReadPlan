package com.example.readproject.shelfchildFragment

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readproject.R
import com.example.readproject.dao.Booklist
import com.example.readproject.dao.BooklistDao
import kotlinx.android.synthetic.main.shelf_child_list_title_frag.*

class listTitleFragment : Fragment() {
    var listTitleList = ArrayList<Booklist>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shelf_child_list_title_frag, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initLists()
        val layoutManager = LinearLayoutManager(activity)
        listTitleRecyclerView.layoutManager = layoutManager
        val adapter = ListTitleAdapter(listTitleList)
        listTitleRecyclerView.adapter = adapter
    }

    private fun initLists(){
        //getListfromDB
        val booklist1 = BooklistDao(requireContext()).getListfromDB(1)
        val booklist2 = BooklistDao(requireContext()).getListfromDB(2)
        val booklist3 = BooklistDao(requireContext()).getListfromDB(3)
        listTitleList.add(booklist1!!)
        listTitleList.add(booklist2!!)
        listTitleList.add(booklist3!!)
    }

    inner class ListTitleAdapter(val listTitle_list: List<Booklist>) : RecyclerView.Adapter<ListTitleAdapter.ViewHolder>(){
        private var thisPosition : Int = 0
        var fragment_content = listContentFragment()
        public fun getthisPosition() : Int{
            return thisPosition
        }
        public fun setthisPosition(thisPosition: Int){
            this.thisPosition = thisPosition
        }

        inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
            val listTitle_layout : LinearLayout = view.findViewById(R.id.list_item_layout)
            val listTitle_image : ImageView = view.findViewById(R.id.list_image)
            val listTitle_name : TextView = view.findViewById(R.id.list_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
            val holder = ViewHolder(view)
            Log.d("onCreateViewHolder","onCreateViewHolderonCreateViewHolderonCreateViewHolder")
            holder.itemView.setOnClickListener {
                val list_item = listTitle_list[holder.adapterPosition]
                Log.d("onCreateViewHolder",list_item.listName)
                setthisPosition(holder.adapterPosition)
                notifyDataSetChanged()
                val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
                editor.putInt("listID",list_item.listID)
                editor.apply()
                Log.d("listID","ccccccccccccccccccccccccccccccc")
                Log.d("listID",PreferenceManager.getDefaultSharedPreferences(context).getInt("listID",0).toString())
                fragment_content.refresh(fragment_content.adapter!!,list_item)
//                val bundle = Bundle()
//                bundle.putInt("listID", list_item.listID)
//                fragment.arguments = bundle
            }
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val list_item = listTitle_list[position]
            holder.listTitle_image.setImageResource(list_item.listImage)
            holder.listTitle_name.text = list_item.listName
            if (position == getthisPosition()){
                holder.listTitle_layout.setBackgroundColor(Color.parseColor("#B8C2DC"))
                holder.listTitle_name.setTextColor(Color.WHITE)
            } else {
                holder.listTitle_layout.setBackgroundColor(Color.WHITE)
                holder.listTitle_name.setTextColor(Color.parseColor("#B8C2DC"))
            }
        }

        override fun getItemCount() = listTitle_list.size
        
    }
}