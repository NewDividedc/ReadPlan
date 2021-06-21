package com.example.readproject.ui

import PlanDatabase.MyPlanHelper
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.readproject.Plan.MonthNewActivity
import com.example.readproject.R
import kotlinx.android.synthetic.main.fragment_month_plan.*
import java.text.SimpleDateFormat
import java.util.*


class FragmentMonthPlan : Fragment()  {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_month_plan, container, false)

    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        card.setOnClickListener{
            calendar_view.makecard()

            val makecard_dbHelper = this.context?.let { it1 -> MyPlanHelper(it1, "card.db", 1) }
            val make_cardb = makecard_dbHelper?.writableDatabase

            val sf = SimpleDateFormat("yyyyMMdd")
            val mCurDate = sf.format(Date())//将date变成了string
            val time_now: String = mCurDate

            val cursor = make_cardb?.query("card",null,"time=?", arrayOf(time_now),null,null,null)

            if (cursor != null) {
                if(cursor.moveToFirst()) {
                    Toast.makeText(context, "已经打卡", Toast.LENGTH_SHORT).show()
                } else{
                    val values = ContentValues().apply {
                        put("time", time_now)
                    }

                    if (make_cardb != null) {

                        make_cardb.insert("card", null, values)
                    }

                    Toast.makeText(context, "打卡成功", Toast.LENGTH_SHORT).show()

                }
            }


        }

        addButton2.setOnClickListener{
            Log.d("ontouch_in_frag",calendar_view.ontouchtime)

            val intent = Intent()
            this.context?.let { it1 -> intent.setClass(it1,MonthNewActivity::class.java) }
            intent.putExtra("ontouchtime",calendar_view.ontouchtime)
            startActivity(intent)

        }




        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun notify1(){
        val mNm = context?.let { it1 -> NotificationManagerCompat.from(it1) }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    "lockTodo1",
                    "chan",
                    NotificationManager.IMPORTANCE_DEFAULT

            )

            if (mNm != null) {
                mNm.createNotificationChannel(channel)
            }

        }

        val notifyBuild = this.context?.let { it1 ->
            NotificationCompat.Builder(it1, "lockTodo1")
                    //设置通知标题
                    .setChannelId("lockTodo1")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("todoit")
                    //设置通知内容
                    .setContentText("请继续保持哦！")
                    .setAutoCancel(false)
                    .setGroup("todos")
                    .setGroupSummary(false)
                    .setWhen(System.currentTimeMillis()).build().apply {
                        visibility = Notification.VISIBILITY_PUBLIC
                    }
        }

        if (mNm != null) {
            if (notifyBuild != null) {
                mNm.notify(1, notifyBuild)
            }
        }





    }





    }






