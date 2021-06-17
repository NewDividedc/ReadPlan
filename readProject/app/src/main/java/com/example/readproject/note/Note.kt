package com.example.readproject
import java.io.Serializable;
class ReadNote : Serializable {
    var notedate: String? = null
    var notetitle: String? = null
    var notecontent: String? = null

    constructor() {}
    constructor(date: String?, title: String?, content: String?) {
        this.notedate = date
        this.notetitle = title
        this.notecontent = content
    }
    fun setNoteDate(date: String?) {
        this.notedate = date
    }

    fun getNoteDate(): String? {
        return this.notedate
    }
    fun setNoteTitle(title: String?) {
        this.notedate = title
    }

    fun getNoteTitle(): String? {
        return this.notetitle
    }
    fun setNoteContent(content: String?) {
        this.notetitle = content
    }

    fun getNoteContent(): String? {
        return this.notecontent
    }

}
