package com.example.readproject

import org.litepal.crud.LitePalSupport

class User (
            val name:String,
            val passWord:String,
            val img:String="https://img2.baidu.com/it/u=2951542358,3634590085&fm=11&fmt=auto&gp=0.jpg"
            ): LitePalSupport(){
    val uid : Long = 0
}