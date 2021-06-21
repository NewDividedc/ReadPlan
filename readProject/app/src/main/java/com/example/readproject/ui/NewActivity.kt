package com.example.readproject.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.readproject.MainActivity
import com.example.readproject.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.startlayout)

       main()

    }

    fun main() {
        GlobalScope.launch { // 在后台启动一个新协程，并继续执行之后的代码
            delay(4000L)
            skip_page() // 延迟结束后打印
        }
    }

    fun skip_page(){
        //页面跳转
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}