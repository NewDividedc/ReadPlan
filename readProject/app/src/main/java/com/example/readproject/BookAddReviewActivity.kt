package com.example.readproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity

class BookAddReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)

        val mEditText = findViewById(R.id.review_content) as EditText
        val mRatingBar = findViewById(R.id.review_ratingBar) as RatingBar
        val mButtonSubmit = findViewById(R.id.review_submit) as Button
        val mButtonCancel = findViewById<Button>(R.id.review_cancel)

        mButtonSubmit.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                val intent2= Intent()
                intent2.putExtra("comment_return",mEditText.text.toString())
                intent2.putExtra("rating_return",mRatingBar.rating.toString())
                setResult(RESULT_OK,intent2)
                finish()
            }
        })

        mButtonCancel.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                finish()
            }
        })
    }

}