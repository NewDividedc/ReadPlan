package com.example.readproject

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.hymane.expandtextview.ExpandTextView
import java.util.*

class bookDetailAdapter(val bookInfo: BooksItemOne, val reviewList: List<bookReview>?,val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_BOOK_INFO = 0
    private val TYPE_BOOK_BRIEF = 1
    private val TYPE_BOOK_COMMENT = 2
    private val TYPE_BOOK_RECOMMEND = 3

    val HEADER_COUNT = 2
    private val AVATAR_SIZE_DP = 24
    private val ANIMATION_DURATION = 600

    //模拟加载时间
    private val PROGRESS_DELAY_MIN_TIME = 500
    private val PROGRESS_DELAY_SIZE_TIME = 1000

    private val mBookInfo: BooksItemOne
    private var mReviewsListResponse: List<bookReview>?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        return if (viewType == TYPE_BOOK_INFO) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book_info, parent, false)
            BookInfoHolder(view)
        } else if (viewType == TYPE_BOOK_BRIEF) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book_brief, parent, false)
            BookBriefHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_book_comment, parent, false)
            BookCommentHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BookInfoHolder) {
            (holder as BookInfoHolder).ratingBar_hots.setRating(java.lang.Float.valueOf(mBookInfo.rating) / 2)
            (holder as BookInfoHolder).tv_hots_num.setText(mBookInfo.rating)
            (holder as BookInfoHolder).tv_comment_num.setText(
                reviewList!!.size.toString()+"人评分"
            )
            (holder as BookInfoHolder).tv_book_info.setText(mBookInfo.author+" / "+mBookInfo.pubdate+" / "+mBookInfo.publisher)
        } else if (holder is BookBriefHolder) {
            if (!mBookInfo.intro.equals("")) {
                (holder as BookBriefHolder).etv_brief.content = mBookInfo.intro
            } else {
                (holder as BookBriefHolder).etv_brief.content = R.string.no_brief.toString()
            }
        } else if (holder is BookCommentHolder) {
            val reviews: List<bookReview> = mReviewsListResponse!!
            if (reviews.isEmpty()) {
                (holder as BookCommentHolder).itemView.setVisibility(View.GONE)
            } else if (position == HEADER_COUNT) {
                (holder as BookCommentHolder).tv_comment_title.visibility = View.VISIBLE
            }
            //else if (position == reviews.size + 1) {
            //    (holder as BookCommentHolder).tv_more_comment.visibility = View.VISIBLE
            //    (holder as BookCommentHolder).tv_more_comment.setText(
            //        reviewList!!.size.toString() + "条"
            //    )
            //}
            Glide.with(context)
                .load(
                    UserDao(context).queryUser(reviews[position - HEADER_COUNT].uid)!!.img
                )
                .asBitmap()
                .centerCrop()
                .into(object :
                    BitmapImageViewTarget((holder as BookCommentHolder).iv_avatar) {
                    override fun setResource(resource: Bitmap) {
                        val circularBitmapDrawable: RoundedBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(
                                context.getResources(),
                                resource
                            )
                        circularBitmapDrawable.setCircular(true)
                        (holder as BookCommentHolder).iv_avatar.setImageDrawable(
                            circularBitmapDrawable
                        )
                    }
                })
            (holder as BookCommentHolder).tv_user_name.setText(
                UserDao(context).queryUser(reviews[position - HEADER_COUNT].uid)!!.name
            )
            if (reviews[position - HEADER_COUNT]
                    .rating != null
            ) {
                (holder as BookCommentHolder).ratingBar_hots.setRating(
                    java.lang.Float.valueOf(
                        reviews[position - HEADER_COUNT].rating.toFloat()
                    )
                )
            }
            (holder as BookCommentHolder).tv_comment_content.setText(
                reviews[position - HEADER_COUNT].comments
            )
            (holder as BookCommentHolder).tv_favorite_num.setText(
                reviews[position - HEADER_COUNT].like.toString() + ""
            )
            (holder as BookCommentHolder).tv_update_time.setText(
                reviews[position - HEADER_COUNT].updated.split(" ").get(0)
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_BOOK_INFO
        } else if (position == 1) {
            TYPE_BOOK_BRIEF
        } else {
            TYPE_BOOK_COMMENT
        }
    }

    override fun getItemCount(): Int {
        var count: Int = HEADER_COUNT
        if (mReviewsListResponse != null) {
            count += mReviewsListResponse!!.size
        }
        return count
    }

    fun setReviews(reviewList: List<bookReview>){
        mReviewsListResponse=reviewList
    }

    inner class BookInfoHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val ratingBar_hots: AppCompatRatingBar
        val tv_hots_num: TextView
        val tv_comment_num: TextView
        val tv_book_info: TextView
        val progressBar: ProgressBar
        val rl_more_info: RelativeLayout

        init {
            ratingBar_hots =
                itemView.findViewById<View>(R.id.ratingBar_hots) as AppCompatRatingBar
            tv_hots_num = itemView.findViewById<View>(R.id.tv_hots_num) as TextView
            tv_comment_num =
                itemView.findViewById<View>(R.id.tv_comment_num) as TextView
            tv_book_info = itemView.findViewById<View>(R.id.tv_book_info) as TextView
            progressBar = itemView.findViewById<View>(R.id.progressBar) as ProgressBar
            rl_more_info =
                itemView.findViewById<View>(R.id.rl_more_info) as RelativeLayout

        }
    }

    inner class BookBriefHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val etv_brief: ExpandTextView
        init {
            etv_brief = itemView.findViewById<View>(R.id.etv_brief) as ExpandTextView
        }
    }

    inner class BookCommentHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val tv_comment_title: TextView
        val iv_avatar: ImageView
        val tv_user_name: TextView
        val ratingBar_hots: AppCompatRatingBar
        val tv_comment_content: TextView
        val iv_favorite: ImageView
        val tv_favorite_num: TextView
        val tv_update_time: TextView
        val tv_more_comment: TextView

        init {
            tv_comment_title =
                itemView.findViewById<View>(R.id.tv_comment_title) as TextView
            iv_avatar =
                itemView.findViewById<View>(R.id.iv_avatar) as ImageView
            tv_user_name = itemView.findViewById<View>(R.id.tv_user_name) as TextView
            ratingBar_hots =
                itemView.findViewById<View>(R.id.ratingBar_hots) as AppCompatRatingBar
            tv_comment_content =
                itemView.findViewById<View>(R.id.tv_comment_content) as TextView
            iv_favorite =
                itemView.findViewById<View>(R.id.iv_favorite) as ImageView
            tv_favorite_num =
                itemView.findViewById<View>(R.id.tv_favorite_num) as TextView
            tv_update_time =
                itemView.findViewById<View>(R.id.tv_update_time) as TextView
            tv_more_comment =
                itemView.findViewById<View>(R.id.tv_more_comment) as TextView
        }
    }

    private val delayTime: Int
        private get() = Random()
            .nextInt(PROGRESS_DELAY_SIZE_TIME) + PROGRESS_DELAY_MIN_TIME

    companion object {
        private const val TYPE_BOOK_INFO = 0
        private const val TYPE_BOOK_BRIEF = 1
        private const val TYPE_BOOK_COMMENT = 2
        private const val TYPE_BOOK_RECOMMEND = 3
        const val HEADER_COUNT = 2
        private const val AVATAR_SIZE_DP = 24
        private const val ANIMATION_DURATION = 600

        //模拟加载时间
        private const val PROGRESS_DELAY_MIN_TIME = 500
        private const val PROGRESS_DELAY_SIZE_TIME = 1000
    }

    init {
        mBookInfo = bookInfo
        mReviewsListResponse = reviewList
    }
}