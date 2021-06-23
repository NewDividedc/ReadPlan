package com.example.readproject.ui

import PlanDatabase.MyPlanHelper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.readproject.Plan.DayPlanAdapter
import com.example.readproject.Plan.NewPlanActivity
import com.example.readproject.R
import kotlinx.android.synthetic.main.every_plan.*
import kotlinx.android.synthetic.main.fragment_day_plan.*
import kotlinx.android.synthetic.main.new_plan_layout.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentDayPlan: Fragment() {

    private var topic_list: ArrayList<String>? = null
    private var content_list: ArrayList<String>? = null
    private var is_finish_list: ArrayList<Int>? = null
    private var id_list: ArrayList<Int>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_day_plan, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        topic_list = ArrayList()
        content_list = ArrayList()
        is_finish_list = ArrayList()
        id_list = ArrayList()

        val dbHelper = this.context?.let { MyPlanHelper(it, "plan.db", 1) }
        val db = dbHelper?.writableDatabase

        val sf = SimpleDateFormat("yyyyMMdd")
        val mCurDate = sf.format(Date())//将date变成了string

        val cursor = db?.query("plan",null,"time=?", arrayOf(mCurDate),null,null,null)
        if (cursor != null) {
            if(cursor.moveToFirst()){
                do {
                    val topic_one = cursor.getString(cursor.getColumnIndex("topic"))
                    topic_list!!.add(topic_one)
                    val content_one = cursor.getString(cursor.getColumnIndex("content"))
                    content_list!!.add(content_one)
                    val is_finish_one = cursor.getInt(cursor.getColumnIndex("is_finish"))
                    is_finish_list!!.add(is_finish_one)
                    val id_one = cursor.getInt(cursor.getColumnIndex("id"))
                    id_list!!.add(id_one)

                }while (cursor.moveToNext())

            }
        }

        var is_finish = 0
        var not_finish = 0

        for(i in 0 .. topic_list!!.size-1){
            if(is_finish_list!![i]==0){
                not_finish = not_finish+1
            }else{
                is_finish = is_finish+1
            }
        }

        plan_is_finish_num.text = is_finish.toString() + " " + "项"
        plan_not_finish_num.text = not_finish.toString() + " " + "项"




        val mRecyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view)



        mRecyclerView?.adapter =DayPlanAdapter(context, topic_list!!,content_list!!,is_finish_list!!,id_list!!)

        var adapter : DayPlanAdapter = mRecyclerView?.adapter as DayPlanAdapter

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        // layoutManager
        mRecyclerView?.layoutManager = layoutManager

//        // itemDecoration
//        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        itemDecoration.setDrawable(resources.getDrawable(R.drawable.disapart))
//        mRecyclerView?.addItemDecoration(itemDecoration)



        // animation动画
        mRecyclerView?.itemAnimator = DefaultItemAnimator()

        // itemClick
        adapter!!.setOnKotlinItemClickListener(object : DayPlanAdapter.IKotlinItemClickListener {


            override fun onItemClickListener(position: Int ) {

                var is_finish = 0
                var not_finish = 0

                for(i in 0 .. topic_list!!.size-1){
                    if(is_finish_list!![i]==0){
                        not_finish = not_finish+1
                    }else{
                        is_finish = is_finish+1
                    }
                }

                is_finish = is_finish + 1
                not_finish = not_finish - 1
                plan_is_finish_num.text = is_finish.toString() + " " + "项"
                plan_not_finish_num.text = not_finish.toString() + " " + "项"

                //Toast.makeText(context,"会执行",Toast.LENGTH_SHORT).show()


            }
        })



        //add_button的点击函数：
        Day_add_Button.setOnClickListener{
            val intent = Intent()
            this.context?.let { it1 -> intent.setClass(it1, NewPlanActivity::class.java) }
            startActivity(intent)
        }




    }





}