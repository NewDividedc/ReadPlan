package com.example.readproject

import android.content.Context
import com.example.readproject.note.ReadNote
import java.io.File
import java.io.RandomAccessFile

object NoteFileUtils{
    fun writeTextToFile(context:Context,content:String,title:String,userAccount:String) {
        var file = File(context?.getFilesDir()?.getPath().toString()+userAccount+".txt")
        if(!file.exists()){
            file.createNewFile()
        }
        var strContent = "$title  $content"
        strContent += "\r\n"
        var randomAccessFile = RandomAccessFile(file, "rwd")
        randomAccessFile.seek(file.length())
        randomAccessFile.write(strContent.toByteArray())
        randomAccessFile.close()
        println(file.absolutePath)
    }
    fun readTextFromFile(context: Context,userAccount:String): List<String> {
        val file = File(context?.getFilesDir()?.getPath().toString()+userAccount+".txt")
        if(!file.exists()){
            return ArrayList()
        }
        return file.readLines()
    }
    fun rewriteFile(context: Context,userAccount: String,notes:List<ReadNote>){
        val file = File(context?.getFilesDir()?.getPath().toString()+userAccount+".txt")
        if(file.exists()){
            file.delete()
        }
        for(each in notes) {
            each.getNoteContent()?.let {
                writeTextToFile(context,
                    it, each.getNoteTitle()!!,userAccount)
            }
        }
    }
    fun findnote(context: Context,keyword:String,userAccount: String): List<Int> {
        val file = File(context?.getFilesDir()?.getPath().toString()+userAccount+".txt")
        if(!file.exists()){
            return ArrayList()
        }
        val notespos:MutableList<Int> =ArrayList()
        val filenotes=file.readLines()
        for(i in filenotes.indices){
            if(keyword in filenotes[i]){
                notespos.add(i)
            }
        }
        return notespos
    }
}