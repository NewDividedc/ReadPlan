package com.example.readproject.note

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.readproject.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_note_create.*
import java.text.SimpleDateFormat
import java.util.*


class NoteCreateActivity: AppCompatActivity(){
    //var db: SQLiteDatabase = Connector.getDatabase()
    var notes= ReadNote();
    private var rootView: LinearLayout? = null
    private var titleTi: TextInputLayout? = null
    private var titleEt: EditText? = null
    private var contentTi: TextInputLayout? = null
    private var contentEt: EditText? = null
    private var addFAB: FloatingActionButton? = null
    private var isSaved:Int=1
    private var time:String?=null

    private var idea: ReadNote? = null
    //private var calendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_create)
        var bundle=this.intent.extras

        if (bundle != null) {
            icAct_title_et.text= Editable.Factory.getInstance().newEditable(bundle.get("title") as CharSequence?)
            icAct_desc_et.text= Editable.Factory.getInstance().newEditable(bundle.get("content") as CharSequence?)
            time= bundle.get("date") as String?
        }
        else{
            isSaved=-1
        }
        idea = intent.getSerializableExtra("Idea") as ReadNote?
        initView()
        initData()
        initEvent()
    }
    private fun initView() {
        rootView = findViewById<LinearLayout>(R.id.icAct_layout) as LinearLayout
        titleEt = findViewById<EditText>(R.id.icAct_title_et) as EditText
        titleTi = findViewById<TextInputLayout>(R.id.icAct_title_textInput) as TextInputLayout
        contentEt = findViewById<EditText>(R.id.icAct_desc_et) as EditText
        contentTi = findViewById<TextInputLayout>(R.id.icAct_desc_ti) as TextInputLayout
        addFAB = findViewById<View>(R.id.note_create_Fad) as FloatingActionButton

    }
    private fun initData() {
        //presenter.setActionView(this)
        if (idea != null) {
            titleEt?.setText(idea!!.getNoteTitle())
            contentEt?.setText(idea!!.getNoteContent())
        }
    }
    private fun initEvent() {
        addFAB!!.setOnClickListener{
            val title = titleEt!!.text.toString()
            val content: String = contentEt?.text.toString()
            var flag = true
            if (isEmpty(title)) {
                showMessage("title不能为空")
                flag = false
            }
            if (flag && isEmpty(content)) {
                showMessage("content不能为空")
                flag = false
            }
            if (flag) {
                if (idea == null) {
                    idea = ReadNote()
                }
                idea!!.setNoteTitle(title)
                idea!!.setNoteContent(content)
                val currentTime = Date()
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val dateString = formatter.format(currentTime)
                if(isSaved==-1) {
                    notes.setNoteDate(dateString)
                    notes.setNoteTitle(title)
                    notes.setNoteContent(content)
                    notes.save()
                }
                else{
                    val note = ReadNote()
                    note.setNoteDate(dateString)
                    note.setNoteTitle(title)
                    note.setNoteContent(content)
                    note.updateAll("notedate=?", time)
                }
                saveNoteMessage("笔记保存")
                finish()

            }
        }

    }
    private fun showMessage(message: String?) {
        Snackbar.make(rootView!!, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveNoteMessage(message: String?) {
        showMessage(message)
//        Handler().postDelayed(Runnable {
//            this@IdeaCreateActivity.finish()
//            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right)
//        }, 1000)
    }



}
