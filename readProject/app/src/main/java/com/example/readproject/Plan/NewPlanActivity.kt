package com.example.readproject.Plan

import PlanDatabase.MyPlanHelper
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.readproject.MainActivity
import com.example.readproject.R
import kotlinx.android.synthetic.main.fragment_day_plan.*
import kotlinx.android.synthetic.main.new_plan_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class NewPlanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_plan_layout)

        backtomonth.setOnClickListener{
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
        }

        new_plan_makesure.setOnClickListener{
            val topictext = topic_text.text
            val contenttext = concent_text.text

            //获取当前的时间
            val sf = SimpleDateFormat("yyyyMMdd")
            val mCurDate = sf.format(Date())//将date变成了string
            val time_now: String = mCurDate

            val dbHelper = MyPlanHelper(this ,"plan.db",1)
            val db = dbHelper.writableDatabase

            var is_warn :Int =0
            if(is_notice_switch.isChecked){
                is_warn=1
            }else{
                is_warn=0
            }


            val values = ContentValues().apply {
                put("Time",time_now)
                put("topic",topictext.toString())
                put("content",contenttext.toString())
                put("is_warn" ,is_warn)
                put("is_finish",0)
            }

            db.insert("plan",null,values)



            Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show()

            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)

        }


    }




}