package com.example.readproject.Plan

import PlanDatabase.MyPlanHelper
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.readproject.R
import kotlinx.android.synthetic.main.every_plan.view.*
import kotlinx.android.synthetic.main.fragment_day_plan.*


class DayPlanAdapter(private val context: Context?, var topic_list: ArrayList<String>,var content_list: ArrayList<String>,var is_finish_list: ArrayList<Int>,var id_list: ArrayList<Int>) :
    RecyclerView.Adapter<DayPlanAdapter.MyHolder>() {
    private var itemClickListener: IKotlinItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {

        var view = LayoutInflater.from(context).inflate(R.layout.every_plan, parent, false)
        return MyHolder(view)


    }


    override fun getItemCount(): Int = topic_list?.size!!

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder?.mtext.item_plan_topic.text = topic_list!![position]
        holder?.mtext.item_text.text = content_list!![position]
        if(is_finish_list!![position]!= 0){
            holder.itemView.item_text.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
            holder.itemView.is_complet.isChecked = true
            holder.itemView.is_complet.text = "已完成"
            holder.itemView.is_complet.isEnabled = false
        }

         //点击事件
        holder.itemView.is_complet.setOnClickListener {
            if(holder.itemView.is_complet.isChecked){
                //数据库数据修改
                val dbHelper = this.context?.let { MyPlanHelper(it, "plan.db", 1) }
                val db = dbHelper?.writableDatabase
                val num = id_list!![position]

                val sql = "update plan set is_finish = 1 where id = " + id_list!![position].toString()
                db?.execSQL(sql)

                Toast.makeText(context,"完成", Toast.LENGTH_SHORT).show()
                //holder.itemView.item_text.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG)
                holder.itemView.item_text.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
                holder.itemView.is_complet.text = "已完成"
                itemClickListener!!.onItemClickListener(position)
                holder.itemView.is_complet.isEnabled = false
            }

        }
    }

    class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        // !! 断言
        //var text: TextView = itemView!!.findViewById(R.id.every_plan)
        val mtext : RelativeLayout = itemView!!.findViewById(R.id.every_plan)
    }

    // 提供set方法
    fun setOnKotlinItemClickListener(itemClickListener: IKotlinItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    //自定义接口
    interface IKotlinItemClickListener {
        fun onItemClickListener(position: Int)
    }



}