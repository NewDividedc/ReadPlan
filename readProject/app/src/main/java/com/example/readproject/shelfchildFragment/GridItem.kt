package com.example.readproject.shelfchildFragment


class GridItem(bookName: String, bookImage: Int) {
    var name: String? = bookName
    var image: Int? = bookImage

    companion object{
        const val TYPE_EBOOK = 0
        const val TYPE_PAPER = 1
    }
}
