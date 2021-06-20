package com.example.readproject.note

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.readproject.DragFloatActionButton
import com.example.readproject.R
import com.example.readproject.SPUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.where
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FragmentNote :Fragment() {
    private var rootView: CoordinatorLayout? = null
    private var noteRecyclerView: RecyclerView? = null
    private var addFAB: DragFloatActionButton? = null
    private var adapter: NoteAdapter? = null
    private var titleEt: EditText? = null
    private var contentEt: EditText? = null
    var mess : String? = null;
    //private var noteList: MutableList<ReadNote> = ArrayList()
    private val noteResult=0;
    val myViewModel by viewModels<NoteViewModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_note, container, false)
        myViewModel.clearAll()
        mess= SPUtils.instance.getString("ACCOUNT")
        val allNotes: MutableList<ReadNote>? = where("userAccount = ?",mess).find(ReadNote::class.java)
        //findAll<ReadNote>(ReadNote::class.java)where("userAccount = ?",mess ).
        Toast.makeText(context,mess,Toast.LENGTH_SHORT).show()
        if(allNotes!=null) {
            for (noteone in allNotes) {
                if (noteone != null) {
                    noteone.getNoteDate()?.let { it1 ->
                        noteone.getNoteTitle()?.let { it2 ->
                            noteone.getNoteContent()?.let { it3 ->
                                myViewModel.insertNote(it1,
                                    it2, it3
                                )
                            }
                        }

                    }
                }


            }
        }

        initView(view)
        initEvent()
        //fragment.refresh()
        return view
    }
//    override fun onAttach(context: android.content.Context) {
//        var sql:NoteSql= NoteSql(context)
//        val list:List<ReadNote> =sql.GetNoteList(" ");
//        for (onenote in list ) {
//            myViewModel.insertNote(ReadNote(onenote.notedate, onenote.notetitle, onenote.notecontent))
//        }
//        super.onAttach(context)
//    }

    override fun onResume() {
        super.onResume()
        myViewModel.clearAll()
        //deleteAll("readnote")
        val allNotes: MutableList<ReadNote>? =where("userAccount = ?",mess).find(ReadNote::class.java)
        //Toast.makeText(context,"${allNotes?.get(0)?.getNoteTitle()}",Toast.LENGTH_SHORT).show()where("userAccount = ?",mess )
        if(allNotes!=null) {
            for (noteone in allNotes) {
                if (noteone != null) {
                    noteone.getNoteDate()?.let { it1 ->
                        noteone.getNoteTitle()?.let { it2 ->
                            noteone.getNoteContent()?.let { it3 ->
                                myViewModel.insertNote(it1,
                                    it2, it3
                                )
                            }
                        }

                    }
                }


            }
        }

        //view?.let { initView(it) }
        //initEvent()
        //adapter = NoteAdapter(requireContext(), myViewModel.noteList)
        adapter?.update(myViewModel.noteList)
        //super.onResume()
    }
    override fun onViewCreated(view: View,
                               savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //textView.setText(mess);
        adapter = NoteAdapter(requireContext(), myViewModel.noteList)
        adapter!!.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val notes:MutableList<ReadNote> =
                    where("notedate='${myViewModel.noteList[position].getNoteDate()}' and userAccount='${mess}'").find(
                        ReadNote::class.java
                    )
//                if( notes[0].getNoteContent()!=null&&notes[0].getNoteTitle()!=null) {
//                    icAct_desc_et.hint = notes[0].getNoteContent()
//                    icAct_title_et.hint = notes[0].getNoteTitle()
//                }
                val intent = Intent(context, NoteCreateActivity::class.java)
                var bundle=Bundle()
                bundle.putString("title",notes[0].getNoteTitle())
                bundle.putString("content",notes[0].getNoteContent())
                bundle.putString("date",notes[0].getNoteDate())
                bundle.putString("account",mess)
                intent.putExtras(bundle)
                startActivityForResult(intent, noteResult)
                //Toast.makeText(context,"${notes[0].getNoteContent()}",Toast.LENGTH_SHORT).show()
            }

        })
        noteRecyclerView!!.adapter = adapter
        noteRecyclerView!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
    private fun initView(view: View) {
        rootView = view.findViewById<View>(R.id.stFrm_coordinatorLayout) as CoordinatorLayout
        addFAB = view.findViewById<View>(R.id.noteFrm_addFad) as DragFloatActionButton
        noteRecyclerView = view.findViewById<View>(R.id.noteFrm_recyclerView) as RecyclerView
        adapter = NoteAdapter(requireContext(), myViewModel.noteList)
//        adapter!!.setOnItemClickListener(object : NoteAdapter.OnItemClickListener {
//            override fun onItemClick(view: View, position: Int) {
//                //adapter!!.notifyItemChanged(position)
//                val notes: kotlin.collections.MutableList<ReadNote> =
//                    where("notedate='${myViewModel.noteList[position].getNoteDate()}'").find(
//                        ReadNote::class.java
//                    )
//                val intent = Intent(context, NoteCreateActivity::class.java)
//                OneNote.onecontent= notes[0].getNoteContent().toString()
//                OneNote.onetitle= notes[0].getNoteTitle().toString()
//                startActivityForResult(intent, noteResult)
//                Toast.makeText(context,"${notes[0].getNoteContent()}",Toast.LENGTH_SHORT).show()
//            }
//
//        })
//        noteRecyclerView!!.adapter = adapter
//        noteRecyclerView!!.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

}

    private fun initEvent() {
        addFAB!!.setOnClickListener{
            val intent = Intent(this.context, NoteCreateActivity::class.java)
            startActivityForResult(intent, noteResult)
        }
    }
    fun onClick(v: View) {
        when (v.id) {
            R.id.noteFrm_addFad -> {
//                val intent = Intent(activity, IdeaCreateActivity::class.java)
//                startActivity(intent)
//                requireActivity().overridePendingTransition(R.anim.push_in_right, R.anim.push_out_left)
            }
        }
    }

    fun loadIdeaData(ideaList: Collection<ReadNote>) {
        this.myViewModel.noteList.clear()
        this.myViewModel.noteList.addAll(ideaList)
        adapter!!.notifyDataSetChanged()
    }

    fun showMessage(message: String?) {
        Snackbar.make(rootView!!, message!!, Snackbar.LENGTH_LONG).show()
    }
}

class NoteAdapter:RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private var listener: OnItemClickListener? = null
    private var dList: MutableList<ReadNote>
    private var context: Context? = null

    constructor (mContext: Context, list:MutableList<ReadNote>) {
        this.context = mContext
        this.dList = list
    }
    fun update(List1:MutableList<ReadNote>){
        this.dList = List1
        this!!.notifyDataSetChanged()
    }

    //  删除数据
    private fun removeData(position: Int,date:String) {
        dList.removeAt(position)
        deleteAll(ReadNote::class.java, "notedate = ? ", date)
        //删除动画
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

//    interface OnItemClickListener {
//        fun onItemClick(itemView: View?, position: Int)
//    }

    // Define the method that allows the parent activity or fragment to define the listener
//    fun setOnItemClickListener(listener: OnItemClickListener) {
//        this.listener = listener
//    }

    // Usually involves inflating a layout from XML and returning the holder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflate the custom layout
        val itemView: View = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false)
        // Return a new holder instance
        return ViewHolder(context, itemView)
    }

    // Involves populating data into the item through holder
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data model based on position
        val note: ReadNote? = dList?.get(position)
        if (note != null) {
            holder.notetitle?.text = note.getNoteTitle()
            holder.notecontent?.text = note.getNoteContent()
            holder.notedate=note.getNoteDate()
            val string =note.getNoteDate()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH)
            val date = LocalDateTime.parse(string, formatter)
            val formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val s2 = date.format(formatter1) // 2020-06-30 11:00:26.401
            holder.notedateview?.text= s2
        }
        if(listener!=null){
            var position = holder.layoutPosition
            holder.notecontent?.setOnClickListener {
                listener!!.onItemClick(holder.itemView, position)
            }
            holder.notetitle?.setOnClickListener {
                listener!!.onItemClick(holder.itemView, position)
            }
            holder.notedelete?.setOnClickListener(View.OnClickListener {
                holder.notedate?.let { it1 -> removeData(position, it1) }
            })
        }
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
        var rootView: CardView? = null
        var notetitle: TextView? = null
        var notecontent: TextView? = null
        var notedelete:ImageView?=null
        var notedate:String?=null
        var notedateview:TextView?=null

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        init {
            //rootView = itemView.findViewById<View>(R.id.ideaLv_layout) as CardView
            rootView = itemView.findViewById<View>(R.id.noteLv_layout) as CardView
            notetitle = itemView.findViewById<View>(R.id.note_title) as TextView
            notecontent = itemView.findViewById<View>(R.id.note_content) as TextView
            notedateview=itemView.findViewById<View>(R.id.notedate) as TextView
            notedelete=itemView.findViewById<View>(R.id.delete_note) as ImageView

//            itemView.setOnClickListener { // Triggers click upwards to the adapter on click
//
//                fun onClick() {
//                    listener?.onItemClick(itemView, layoutPosition)
//                    //Toast.makeText(context, layoutPosition, Toast.LENGTH_SHORT).show()
//                }
//
//            }
        }
    }

    companion object {
        private val listener: OnItemClickListener? = null
    }

}