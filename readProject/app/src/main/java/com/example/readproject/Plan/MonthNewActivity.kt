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
import kotlinx.android.synthetic.main.fragment_month_plan.*
import kotlinx.android.synthetic.main.month_new_plan_layout.*
import kotlinx.android.synthetic.main.new_plan_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log

class MonthNewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.month_new_plan_layout)

        back_to_month.setOnClickListener{
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            startActivity(intent)
        }

        month_new_plan_makesure.setOnClickListener{
            val topictext = month_topic_text.text
            val contenttext = month_concent_text.text

            var ontouchtime_get = intent.getStringExtra("ontouchtime")

            if(ontouchtime_get == "0"){
                val sf = SimpleDateFormat("yyyyMMdd")
                val mCurDate = sf.format(Date())//将date变成了string
                ontouchtime_get = mCurDate
            }
            //获取当前的时间
             //var ontouch_time=calendar_view.ontouchtime

            if (ontouchtime_get != null) {
                Log.d("new_ontouch",ontouchtime_get)
            }


            val dbHelper = MyPlanHelper(this ,"plan.db",1)
            val db = dbHelper.writableDatabase

            var is_warn :Int =0
            if(month_is_notice_switch.isChecked){
                is_warn=1
            }else{
                is_warn=0
            }


            val values = ContentValues().apply {
                put("Time",ontouchtime_get)
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