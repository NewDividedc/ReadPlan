package com.example.readproject

import android.content.Context
import android.util.DisplayMetrics

class DisplayUtil {
    companion object{
        fun getScreenWidth(context: Context): Int {
            val dm: DisplayMetrics = context.getResources().getDisplayMetrics()
            return dm.widthPixels
        }
    }
}