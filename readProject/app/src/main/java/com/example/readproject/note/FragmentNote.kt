package com.example.readproject.note

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.readproject.*
import com.example.readproject.Algorithm
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.angmarch.views.NiceSpinner
import org.jetbrains.anko.alert
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.*
import org.litepal.LitePal.deleteAll
import org.litepal.LitePal.where
import java.io.*
import java.nio.channels.AsynchronousFileChannel.open
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FragmentNote :Fragment() {
    private var rootView: CoordinatorLayout? = null
    private var noteRecyclerView: RecyclerView? = null
    private var addFAB: DragFloatActionButton? = null
    private var adapter: NoteAdapter? = null
    private var spinner:NiceSpinner?=null
    private var titleEt: EditText? = null
    private var contentEt: EditText? = null
    var notelines:MutableList<String>?=null
    var searchKeys=0
    var selectedkey:String?=null
    var keys:MutableList<String>?=ArrayList()
    var mess : String? = null;
    //private var noteList: MutableList<ReadNote> = ArrayList()
    private val noteResult=0;
    var reverflag=false
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


//        //fragment.refresh()
//        val input = context?.assets?.open("notes.txt")?.bufferedReader().use{
//            it?.readLines()
//        }
//        if (input != null) {
//            Algorithm.main(input)
//        }
//        val keywords=Algorithm.resultlist
//        for(each in keywords){
//            println("最后大小为${each.key}+${each.value}")
//        }
//        writeTextToFile("12","13",)
//        writeTextToFile("12","13",)
        return view
    }
//    private fun writeTextToFile(content:String,title:String,userAccount:String) {
//        var file = File(context?.getFilesDir()?.getPath().toString()+userAccount+".txt")
//        if(!file.exists()){
//            file.createNewFile()
//        }
//        var strContent = "$title  $content"
//        strContent += "\r\n"
//        var randomAccessFile = RandomAccessFile(file, "rwd")
//        randomAccessFile.seek(file.length())
//        randomAccessFile.write(strContent.toByteArray())
//        randomAccessFile.close()
//        println(file.absolutePath)
//    }
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

        val dataList:MutableList<String> =ArrayList();
        dataList.add("时间倒序");
        dataList.add("时间正序");
        dataList.add("关键词");
        spinner?.attachDataSource(dataList);
        spinner?.addOnItemClickListener { parent, view, position, id ->
            if(dataList[position]=="关键词"){
                if(searchKeys==1){
                    alert("是否要重新提取关键词","尊敬的用户"){
                        positiveButton("好的") {
                            context?.let { it1 -> mess?.let { it2 ->
                                NoteFileUtils.rewriteFile(it1,
                                    it2,myViewModel.noteList)
                            } }
                            val dialog = indeterminateProgressDialog("正在努力加载关键词", "请稍候")
                            dialog.show()
                            Thread {
                                findkeywords()
                                runOnUiThread {
                                    //dialog.dismiss()
                                    //println("zuihou keys size${keys?.size}")
                                    if(keys?.size==0){
                                        toast("没找到关键词")
                                    }else {
                                        var selectedkey: String
                                        keys?.let {
                                            selector("请选择关键词", it) { _: DialogInterface, i: Int ->
                                                selectedkey = keys!![i]
                                                toast("你选择的关键词是${selectedkey}")
                                                findNotes(selectedkey)
                                            }
                                        }
                                    }
                                    dialog.dismiss()
                                }
                            }.start()  }
                        negativeButton("不用") {  keys?.let {
                            selector("请选择关键词", it) { _: DialogInterface, i: Int ->
                                selectedkey = keys!![i]
                                toast("你选择的关键词是${selectedkey}")
                                findNotes(selectedkey!!)
                            }
                        } }
                    }.show()

                }
                else {
                    context?.let { it1 -> mess?.let { it2 ->
                        NoteFileUtils.rewriteFile(it1,
                            it2,myViewModel.noteList)
                    } }
                    val dialog = indeterminateProgressDialog("正在努力分析关键词", "请稍候")
                    dialog.show()
                    Thread {
                        findkeywords()
                        runOnUiThread {
                            //dialog.dismiss()
                            //println("zuihou keys size${keys?.size}")
                            if(keys?.size==0){
                                toast("没找到关键词")
                            }else {
                                var selectedkey: String
                                keys?.let {
                                    selector("请选择关键词", it) { _: DialogInterface, i: Int ->
                                        selectedkey = keys!![i]
                                        toast("你选择的关键词是${selectedkey}")
                                        findNotes(selectedkey)
                                    }
                                }
                            }
                            dialog.dismiss()
                        }
                    }.start()
                    //dialog.dismiss()
                }
                //
            }
            else if(dataList[position]=="时间倒序"){
                reverflag=true
                val rev=myViewModel.noteList.reversed() as MutableList<ReadNote>
                if(rev.size!=0) {
                    myViewModel.noteList = rev
                }else{
                    myViewModel.noteList = ArrayList()
                }
                adapter?.update(myViewModel.noteList)
            }
            else{
                if(reverflag){
                    val rev=myViewModel.noteList.reversed() as MutableList<ReadNote>
                    if(rev.size!=0) {
                        myViewModel.noteList = rev
                    }else{
                        myViewModel.noteList = ArrayList()
                    }
                    myViewModel.noteList=myViewModel.noteList.reversed() as MutableList<ReadNote>
                    adapter?.update(myViewModel.noteList)
                }else {
                    adapter?.update(myViewModel.noteList)
                }
                reverflag=false
            }
        }


    }
    fun findNotes(key:String){
        val notespos=context?.let { it1 -> mess?.let { it2 ->
            NoteFileUtils.findnote(it1,key,
                it2
            )
        } }
        val filterList:MutableList<ReadNote> =ArrayList()
        if (notespos != null) {
            for(each in notespos){
                if(each<myViewModel.noteList.size) {
                    filterList.add(myViewModel.noteList[each])
                }else{
                    toast("找不到该Note，请考虑重新分析关键词")
                    break
                }
            }
        }
        adapter?.update(filterList)

    }
    fun findkeywords(){
        searchKeys=1
        notelines = context?.let {
            mess?.let { it1 ->
                NoteFileUtils.readTextFromFile(
                    it,
                    it1
                )
            }
        } as MutableList<String>
        if (notelines != null) {
            Algorithm.main(notelines!!)
            var keywords = Algorithm.resultlist
            if (keywords.size == 0) {
                //toast("未找到关键词")
            } else if (keywords.size <= 5) {
                for (each in keywords) {
                    keys?.add(each.key)
                }
            } else {
                keywords = keywords.subList(0, 4)
                for (each in keywords) {
                    keys?.add(each.key)
                }
            }
        }else{
            toast("没有笔记提取关键词")
        }

    }
    private fun initView(view: View) {
        spinner=view.findViewById<View>(R.id.note_spinner) as NiceSpinner
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

        val builder = AlertDialog.Builder(context)
        builder.setTitle("尊敬的用户")
        builder.setMessage("你真的要删除我吗？")
        builder.setPositiveButton("千真万确") { _: DialogInterface, _: Int ->
            dList.removeAt(position)
            deleteAll(ReadNote::class.java, "notedate = ? ", date)
            notifyItemRemoved(position)
            notifyDataSetChanged()}
        builder.setNegativeButton("我再想想") { _: DialogInterface, _: Int -> }
        val alert = builder.create()
        alert.show()

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