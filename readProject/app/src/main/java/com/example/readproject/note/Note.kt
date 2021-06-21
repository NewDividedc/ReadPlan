package com.example.readproject.note

import org.litepal.crud.LitePalSupport

class ReadNote: LitePalSupport(){
    private var notedate:String?=null
    private var notetitle:String?=null
    private var notecontent: String?=null
    private var userAccount: String?=null
    //var user: UserBean? = null

   fun setUserAccount(account:String?){
       this.userAccount=account

   }
    fun getUserAccount(): String? {
        return this.userAccount
    }

    fun setNoteDate(date: String?) {
        this.notedate = date
    }

    fun getNoteDate(): String? {
        return this.notedate
    }
    fun setNoteTitle(title: String?) {
        this.notetitle = title
    }

    fun getNoteTitle(): String? {
        return this.notetitle
    }
    fun setNoteContent(content: String?) {
        this.notecontent = content
    }

    fun getNoteContent(): String? {
        return this.notecontent
    }

}
class OneNote(){
    companion object{
        var onetitle:String="标题"
        var onecontent:String="内容"
        fun setTitle(title:String){
            this.onetitle=title
        }
        fun setContent(content:String){
            this.onecontent=content
        }

    }
}