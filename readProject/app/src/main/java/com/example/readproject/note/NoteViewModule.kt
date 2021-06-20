package com.example.readproject.note

import androidx.lifecycle.ViewModel

class NoteViewModel : ViewModel(){
    var noteList: MutableList<ReadNote> = ArrayList()
    fun insertNote(date:String,title:String,content:String){
        var note=ReadNote()
        note.setNoteDate(date)
        note.setNoteTitle(title)
        note.setNoteContent(content)
        //note.setNoteDate(date)
        noteList.add(note)
    }
    fun clearAll(){
        noteList=mutableListOf<ReadNote>()
    }

}