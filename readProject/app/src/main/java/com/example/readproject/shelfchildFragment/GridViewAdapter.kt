package com.example.readproject.shelfchildFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView



//class GridViewAdapter(
//    private val mContext: Context,
//    private val layoutResourceId: Int,
//    itemList: ArrayList<GridItem>
//) :
//    ArrayAdapter<GridItem?>(mContext, layoutResourceId, itemList as List<GridItem?>) {
//    private var mGridData = itemList
//    fun setGridData(mGridData: ArrayList<GridItem>) {
//        this.mGridData = mGridData
//        notifyDataSetChanged()
//    }
//
//    override fun getView(
//        position: Int,
//        view: View?,
//        parent: ViewGroup
//    ): View {
//        var convertView = view
//        val holder: ViewHolder
//        if (convertView == null) {
//            Log.d("TTTTTTT","converView=Null")
//            val inflater = (mContext as Activity).layoutInflater
//            convertView = inflater.inflate(layoutResourceId, parent, false)
//            holder = ViewHolder()
//            holder.textView =
//                convertView.findViewById<TextView>(R.id.book_name)
//            holder.imageView =
//                convertView.findViewById<ImageView>(R.id.book_image)
//            convertView.tag = holder
//        } else {
//            holder = convertView.tag as ViewHolder
//        }
//        val item = mGridData[position]
//        holder.textView?.text = item.name
//        Log.d("NNNNName",item.name.toString())
//        Log.d("IIIIImage",item.image.toString())
//        Log.d("mContext",mContext.toString())
//        val url : String = "https://img1.doubanio.com/view/subject/s/public/s33849778.jpg"
//        Glide.with(mContext).load(url).into(holder.imageView)
//        //Picasso.with(mContext).load(item.getImage()).into(holder.imageView)
//        return convertView!!
//    }
//
//    private inner class ViewHolder {
//        var textView: TextView? = null
//        var imageView: ImageView? = null
//    }
//    init {
//        setGridData(mGridData)
//    }
//}
