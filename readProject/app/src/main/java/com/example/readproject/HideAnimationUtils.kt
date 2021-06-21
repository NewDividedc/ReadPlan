package com.example.readproject

import android.view.View
import android.view.animation.TranslateAnimation

class HideAnimationUtils(private val Show: Boolean,  private val view_bottom: View) {
    private fun ShowOrHideTitle() { //标题栏
        val fromY: Int //0表示控件Y轴起点
        val toY: Int //正值表示下移，负值上移
//        if (Show) { //显示
//            fromY = -view_title.height
//            toY = 0
//        } else { //隐藏
//            fromY = 0
//            toY = -view_title.height
//        }
//        val animation: TranslateAnimation //平移动画
//        animation = TranslateAnimation(0F, 0F, fromY.toFloat(), toY.toFloat())
//        animation.duration = 400 //设置动画持续毫秒
//        animation.fillAfter = true //动画执行完后是否停留在执行完的状态
        //view_title.startAnimation(animation)
    }

    private fun ShowOrHideBottom() { //底部栏
        val fromY: Int //0表示控件Y轴起点
        val toY: Int //正值表示下移，负值上移
        if (Show) { //显示
            fromY = view_bottom.height
            toY = 0
        } else { //隐藏
            fromY = 0
            toY = view_bottom.height
        }
        val animation: TranslateAnimation //平移动画
        animation = TranslateAnimation(0F, 0F, fromY.toFloat(), toY.toFloat())
        animation.duration = 400 //设置动画持续毫秒
        animation.fillAfter = true //动画执行完后是否停留在执行完的状态
        view_bottom.startAnimation(animation)
    }

    init {
        //ShowOrHideTitle()
        ShowOrHideBottom()
    }
}
