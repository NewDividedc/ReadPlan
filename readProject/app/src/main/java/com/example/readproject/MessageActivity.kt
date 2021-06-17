package com.example.readproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.readproject.note.NoteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MessageActivity : AppCompatActivity(){
    private var msgRecyclerView: RecyclerView? = null
    var adapter: MessageAdapter? = null
    var msgList:MutableList<Message>?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        //val view=findViewById<View>(R.layout.activity_message) as View
//        adapter = msgList?.let { MessageAdapter(this, it) }
//        //msgRecyclerView = view.findViewById<View>(R.id.messagelist) as RecyclerView
//        msgRecyclerView!!.adapter = adapter
//        msgRecyclerView!!.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)

    }

    fun returnMain(view: View){
        finish();
    }


}
class MessageAdapter: RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private var listener: OnItemClickListener? = null
    private var dList: MutableList<Message>
    private var context: Context? = null

    constructor (mContext: Context, list: MutableList<Message>) {
        this.context = mContext
        this.dList = list
    }

    fun update(List1: MutableList<Message>) {
        this.dList = List1
        this!!.notifyDataSetChanged()
    }

    //  删除数据
    private fun removeData(position: Int, date: String) {
        dList.removeAt(position)
        //LitePal.deleteAll(ReadNote::class.java, "notedate = ?", date)
        //删除动画
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        // Inflate the custom layout
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        // Return a new holder instance
        return ViewHolder(context, itemView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val msg: Message = dList?.get(position)
        if (msg != null) {
            holder.msgContent?.text = msg.messageContent
            holder.msgDate?.text = msg.messageDate
            holder.msgSender?.text = msg.messageSender
        }
//        if (listener != null) {
//            var position = holder.layoutPosition
//            holder.msg?.setOnClickListener {
//                listener!!.onItemClick(holder.itemView, position)
//            }
//            holder.notetitle?.setOnClickListener {
//                listener!!.onItemClick(holder.itemView, position)
//            }
//            holder.notedelete?.setOnClickListener(View.OnClickListener { v ->
//                holder.notedate?.let { removeData(position, it) }
//            })
//        }
//        if (StringUtil.isNotEmpty(idea.getBackground())) {
//            holder.rootView.setCardBackgroundColor(Color.parseColor(idea.getBackground()))
//        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.listener = onItemClickListener
    }

    // Return the total count of items
    override fun getItemCount(): Int {
        return dList?.size
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    class ViewHolder(context: Context?, itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        var msgDate: TextView? = null
        var msgContent: TextView? = null
        var msgSender: TextView? = null
        var msgImg: ImageView? = null

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        init {
            //rootView = itemView.findViewById<View>(R.id.ideaLv_layout) as CardView
            msgDate = itemView.findViewById<View>(R.id.CommentDate) as TextView
            msgContent = itemView.findViewById<View>(R.id.CommentContent) as TextView
            msgSender = itemView.findViewById<View>(R.id.SenderName) as TextView
            msgImg = itemView.findViewById<View>(R.id.SenderImg) as ImageView
        }
    }

    companion object {
        private val listener: OnItemClickListener? = null
    }
}
