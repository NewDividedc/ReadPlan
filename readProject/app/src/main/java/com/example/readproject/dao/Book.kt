package com.example.readproject.dao

class Book(
    val bookID: Int,
    val bookName: String,
    val type: Int,
    val bookImage: Int, //不确定
    val allPages: Int,
    val writer: String,
    val readPages: Int,
    val contents: String?,
    val readTime: Long)
