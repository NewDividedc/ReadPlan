package com.example.readproject.note

import androidx.lifecycle.ViewModel

class NoteViewModel : ViewModel(){
    var noteList: MutableList<ReadNote> = ArrayList()
    fun insertNote(date:String,title:String,content:String){
        var note=ReadNote()
        note.notedate=date
        note.notetitle=title
        note.notecontent=content
        noteList.add(note)
    }
    fun clearAll(){
        noteList=mutableListOf<ReadNote>()
    }

}