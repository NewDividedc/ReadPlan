package com.example.readproject

import android.app.Application
import android.content.Context
import org.litepal.LitePal

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        LitePal.initialize(this)
        instance(this)
    }

    //kotlin companion 相当于java的static
    companion object{
        private var context : Context? = null

        fun getInstance():Context?{
            return this.context
        }

        fun instance(context : Context){
            this.context = context
        }
    }

}


