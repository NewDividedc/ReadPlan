package com.example.readproject.ui

import PlanDatabase.MyPlanHelper
import android.content.Context
import android.database.Cursor
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.example.readproject.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
class MyCalendarView : View {

    companion object {
        private val TAG = "MyCalendarView"
        private val months = arrayOf(
                "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"
        )
        private val weeks = arrayOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        private val selectedDates = java.util.ArrayList<Int>()



        public enum class TYPE { BEFORE, AFTER }
    }

    //实现dp和px之间的转换
    open fun dp2px(dp: Float): Float {
        val density = context.resources.displayMetrics.density
        return (density * dp) + 0.5F
    }

    //正点击到的地方的数据
    var placeArray:Array<Float> = arrayOf(0.0f, 0.0f)

    var curmonth_string :String = String()
    var curyear :String = String()
    var ontouchtime:String = "0"

    var xArray = arrayOf(0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f)
    var yArray = arrayOf(0.0f,0.0f,0.0f,0.0f,0.0f)


    // 定义画笔 这里其实可以优化省略一点
    private var mBgPaint: Paint
    private var mTextPaint: Paint
    private var mPaint: Paint
    private var mBorderPaint: Paint

    // 当前月份、日期
    private var curMonth: String? = null
    private var curDate: Int? = null

    // 字体大小
    private var mTitleSize: Int = 0
    private var mWeekSize: Int = 0
    private var mDateSize: Int = 0

    // 字体颜色
    private var mColorText: Int = 0
    private var mSelectedColorText: Int = 0
    private var mSelectedColor: Int = 0

    // 阴影 在关闭硬件加速后会显示
    private val mSignedShader: Shader? = null
    private val mShader: Shader? = null

    // 标题高度
    private var mTitleHeight: Float = 0.toFloat()
    // 星期几的高度
    private var mWeekHeight: Float = 0.toFloat()
    // 日期行的高度
    private var mDayHeight: Float = 0.toFloat()

    private var mTitlePaddingTop: Float = 0.toFloat()
    private var mWeekPaddingTop: Float = 0.toFloat()

    // 日期计算的相关参数
    private var rowCount: Int = 0
    private var isCurMonth: Boolean = false
    private var firstIndex: Int = 0
    private var dayOfMonth: Int = 0



    //不懂这个是啥
    private var remains: Int = 0

    private var lastMonthDays: Int = 0

    private var perWidth: Int = 0

    private var mListener: OnMonthChangeListener? = null

    //是否打卡
    private var makecard_num:Int = 0


    private var card_list: ArrayList<String>? = null
    private var theday_num = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet? = null) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet? = null, defStyle: Int? = 0) : super(context, attr, defStyle ?: 0)

    var number2 = 0

    init {
        mTitleSize = dp2px(30f).toInt()
        mWeekSize = dp2px(10f).toInt()
        mDateSize = dp2px(12f).toInt()

        mColorText = Color.BLACK
        mSelectedColorText = Color.WHITE
        mSelectedColor = resources.getColor(R.color.colorAccent)

        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        mTextPaint.textSize = mTitleSize.toFloat()
        mTitleHeight = mTextPaint.fontMetrics.descent - mTextPaint.fontMetrics.ascent

        mTextPaint.textSize = mWeekSize.toFloat()
        mWeekHeight = mTextPaint.fontMetrics.descent - mTextPaint.fontMetrics.ascent

        mTextPaint.textSize = mDateSize.toFloat()
        mDayHeight = mTextPaint.fontMetrics.descent - mTextPaint.fontMetrics.ascent

        mBgPaint.color = Color.parseColor("#DDDDDD")
        mBgPaint.strokeWidth = 1f

        mTitlePaddingTop = dp2px(10f)
        mWeekPaddingTop = dp2px(15f)

        setMonth(Date())
        // 放置在初始化可以避免经常重绘设置 提高性能
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setMonthChangeListener(listener: OnMonthChangeListener) {
        this.mListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setMonth(date: Date) {

        val sf = SimpleDateFormat("yyyyMMdd")
        val mCurDate = sf.format(date)//将date变成了string
        Log.i(TAG, "mCurDate = $mCurDate")
        var month = mCurDate.substring(4, 6)

        curmonth_string = month.toString()
        curyear = mCurDate.substring(0,4)

        Log.i(TAG, "year = $curyear")

        Log.i(TAG, "month = $month")

        var the_fir_day = curyear + month + "01"
        val formatter =  SimpleDateFormat("yyyyMMdd")
        val thefirstdate = formatter.parse(the_fir_day)


        val curDate = sf.format(Date())
        val curMonth = curDate.substring(4, 6)
        isCurMonth = curMonth == month

        if (month.startsWith("0")) {
            month = month.substring(1)
        }
        Log.i(TAG, "month = $month")
        val monthInt = Integer.parseInt(month)
        this.curMonth = months[monthInt - 1]
        Log.i(TAG, "monthInt = $monthInt")

        var mDate = mCurDate.substring(6, 8)
        if (mDate.startsWith("0")) {
            mDate = mDate.substring(1)
        }
        // 获取到当前日期
        this.curDate = Integer.parseInt(mDate)

        val c = Calendar.getInstance()
        c.time = date


        val form = DateTimeFormatter.ofPattern("yyyyMMdd",Locale.ENGLISH)
        val the_date = LocalDate.parse(the_fir_day, form)
        val the_date_num=the_date.dayOfWeek.value

        // 当前月份第一天开始是星期几 周一开始算的话要 - 2
        Log.d("day_of_week",the_date_num.toString())
        firstIndex = the_date_num - 7
        if (firstIndex < 0) {
            firstIndex += 7
        }

        Log.i(TAG, "firstIndex = $firstIndex")

        // 当前月份的天数
        dayOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        var days = dayOfMonth
        Log.i(TAG, "dayOfMonth = $dayOfMonth")

        days -= 7 - firstIndex
        // 最后一行剩余的天数
        remains = days
        rowCount = 1
        while (remains >= 7) {
            remains -= 7
            rowCount++
        }
        if (remains > 0) rowCount++

        Log.i(TAG, "rowCount = $rowCount")
        Log.i(TAG, "remains = $remains")

//        // 模拟添加数据
//        selectedDates.add(4)
//        selectedDates.add(9)
//        selectedDates.add(16)
//        selectedDates.add(25)

        // 上一个月
        c.add(Calendar.MONTH, -1)
        // 上一个月总共有多少天
        lastMonthDays = c.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    //当然也可以在布局文件里面写死宽高，而重写该方法可以根据自己的需求设置自定义view大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        perWidth = width / 7
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec), heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // 设置关闭硬件加速 否则不显示阴影 但是在此开启后会很慢 因为会不断重绘
//        setLayerType(LAYER_TYPE_SOFTWARE, null);


        drawMonth(canvas)
        drawWeek(canvas)
        drawDate(canvas)
        drawTouch(canvas)
        getontouchtime()

    }


    private fun drawMonth(canvas: Canvas?) {
        mTextPaint.textSize = mTitleSize.toFloat()
        mTextPaint.color = Color.parseColor("#393F4E")
        Log.d("curmonth",curMonth.toString())

        val the_topic_time = curyear + "年" + curmonth_string + "月"

//        val x = (width - mTextPaint.measureText(curMonth).toInt()) / 2
//        curMonth?.let { canvas!!.drawText(it, x.toFloat(), mTitleHeight, mTextPaint) }

        val x = (width - mTextPaint.measureText(the_topic_time).toInt()) / 2
        the_topic_time?.let { canvas!!.drawText(it, x.toFloat(), mTitleHeight, mTextPaint) }

        Log.d("theontouch",ontouchtime)
    }

    private fun drawWeek(canvas: Canvas?) {
        mTextPaint.textSize = mWeekSize.toFloat()

        mTextPaint.color = Color.parseColor("#BBBBBB")

        for (i in 0..6) {
            val len = mTextPaint.measureText(weeks[i]).toInt()
            val currentX = i * perWidth + (perWidth - len) / 2
            canvas!!.drawText(
                    weeks[i], currentX.toFloat(), mTitleHeight
                    + mWeekPaddingTop + mWeekHeight, mTextPaint
            )
            //            currentX += perWidth;
        }

        mBgPaint.color = Color.parseColor("#BBBBBB")
        canvas!!.drawLine(
                0f, mTitleHeight
                + mWeekPaddingTop + mWeekHeight + dp2px(10f), measuredWidth.toFloat(),
                mTitleHeight + mWeekPaddingTop + mWeekHeight + dp2px(10f), mBgPaint
        )
    }

    private fun drawDate(canvas: Canvas?) {
        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = mDateSize.toFloat()

        mPaint.color = Color.BLUE
        mPaint.strokeWidth = 1f

        val rowHeight = mDayHeight + dp2px(30f)
        var passDays = 0
        for (row in 0 until rowCount) {
            if (row == 0) {
                // 第一行
                drawDay(canvas!!, 7, firstIndex, row, rowHeight, passDays)
                passDays += 7 - firstIndex
                if (firstIndex > 0) {
                    for (i in 0 until firstIndex) {
                        mTextPaint.color = Color.argb(
                                50,
                                95, 95, 95
                        )
                        val day = (lastMonthDays - firstIndex + i + 1).toString()
                        val len = mTextPaint.measureText(day)
                        val curX = i * perWidth + (perWidth - len) / 2
                        val curY = mTitleHeight + mWeekPaddingTop + mWeekHeight + (row + 1) * rowHeight
                        canvas.drawText(day, curX, curY, mTextPaint)
                    }
                    mTextPaint.color = Color.BLACK
                }
            } else if (row == rowCount - 1) {
                // 最后一行
                if (remains > 0) {
                    drawDay(canvas!!, remains, 0, row, rowHeight, dayOfMonth - remains)
                    var i = remains
                    var j = 1
                    while (i < 7) {
                        mTextPaint.color = Color.argb(
                                50,
                                95, 95, 95
                        )
                        val day = j.toString()
                        val len = mTextPaint.measureText(day)
                        val curX = i * perWidth + (perWidth - len) / 2
                        val curY = mTitleHeight + mWeekPaddingTop + mWeekHeight + (row + 1) * rowHeight
                        canvas.drawText(day, curX, curY, mTextPaint)
                        i++
                        j++
                    }
                    mTextPaint.color = Color.BLACK
                } else {
                    // remains = 0的情况就是刚好一周
                    drawDay(canvas!!, 7, 0, row, rowHeight, dayOfMonth - 7)
                }
            } else {
                drawDay(canvas!!, 7, 0, row, rowHeight, passDays)
                passDays += 7
            }
        }
    }

    private fun drawDay(canvas: Canvas, endIndex: Int, startIndex: Int, rowCount: Int, rowHeight: Float, passDay: Int) {
        var date = passDay + 1
        var number1 = 0

        val originTop = mTitleHeight + mWeekPaddingTop + mWeekHeight
        for (column in startIndex until endIndex) {
            theday_num = theday_num+1
            val len = mTextPaint.measureText(date.toString())
            val currentX = column * perWidth + (perWidth - len) / 2
            //获取所有的点

            if(isCurMonth)
            {
                val they = originTop + rowHeight * (rowCount + 1) - 12

                if(number1==0&&number2<5){
                    yArray[number2]=they
                    number2++
                }

            if(isCurMonth)
            {
                val thex = column * perWidth + perWidth.toFloat() / 2
                if(number1<7){

                    xArray[number1]=thex
                    number1= number1+1
                }
            }

            }



            //打卡
            if (isCurMonth&&makecard_num==1&&date == this.curDate) {
                    mPaint.color = Color.argb(100, 228, 0, 0)
                    val cx = column * perWidth + perWidth.toFloat() / 2
                    val cy = originTop + rowHeight * (rowCount + 1) - 12
                    mPaint.style = Paint.Style.FILL
                    mBorderPaint.style = Paint.Style.STROKE
                    mBorderPaint.color = Color.GRAY
                    mPaint.setShadowLayer(10f, 0f, 5f, Color.GRAY)
                    canvas.drawCircle(cx, cy, 40f, mPaint)
                    canvas.drawCircle(cx, cy, 40f, mBorderPaint)
                    mTextPaint.color = mSelectedColorText
                    mPaint.setShadowLayer(0f, 0f, 0f, Color.GRAY)
                }





            //给当前日子画圈
            if (isCurMonth) {
                if (date == this.curDate) {
                    mBgPaint.color = resources.getColor(R.color.colorAccent)
                    val cx = column * perWidth + perWidth.toFloat() / 2
                    val cy = originTop + rowHeight * (rowCount + 1) - 12
                    mBgPaint.style = Paint.Style.STROKE
                    canvas.drawCircle(cx, cy, 40f, mBgPaint)
                    mTextPaint.color = mSelectedColor
                }
            }



            if (selectedDates.contains(date)) {
                mPaint.color = resources.getColor(R.color.colorAccent)
                val cx = column * perWidth + perWidth.toFloat() / 2
                val cy = originTop + rowHeight * (rowCount + 1) - 12
                mPaint.style = Paint.Style.FILL
                mPaint.setShadowLayer(10f, 0f, 5f, mSelectedColor)
                canvas.drawCircle(cx, cy, 40f, mPaint)
                mTextPaint.color = mSelectedColorText
                mPaint.setShadowLayer(0f, 0f, 0f, Color.GRAY)

            }
 //           else {
//                // 不包含
//                if (date < curDate!! || !isCurMonth) {
//                    mPaint.color = Color.argb(100, 228, 0, 0)
//                    val cx = column * perWidth + perWidth.toFloat() / 2
//                    val cy = originTop + rowHeight * (rowCount + 1) - 12
//                    mPaint.style = Paint.Style.FILL
//                    mBorderPaint.style = Paint.Style.STROKE
//                    mBorderPaint.color = Color.GRAY
//                    mPaint.setShadowLayer(10f, 0f, 5f, Color.GRAY)
//                    canvas.drawCircle(cx, cy, 40f, mPaint)
//                    canvas.drawCircle(cx, cy, 40f, mBorderPaint)
//                    mTextPaint.color = mSelectedColorText
//                    mPaint.setShadowLayer(0f, 0f, 0f, Color.GRAY)
//                }
//            }

            canvas.drawText(date.toString(), currentX, originTop + (rowCount + 1) * rowHeight, mTextPaint)


             //画出所有打卡的天数：
            card_list = ArrayList()
            val makecard_dbHelper = this.context?.let { it1 -> MyPlanHelper(it1, "card.db", 1) }
            val make_cardb = makecard_dbHelper?.writableDatabase

            val sf = SimpleDateFormat("yyyyMMdd")
            val mCurDate = sf.format(Date())//将date变成了string

            val sql = "select time from card"

            val cursor: Cursor? = make_cardb?.rawQuery(sql,null)
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val card_one = cursor.getString(cursor.getColumnIndex("time"))
                    card_list!!.add(card_one)
                }
            }

            Log.d("the_day_num",theday_num.toString())

            var card_list_day :Array<Int>

            var the_day :String
            var the_day_int:Int

            for(i in 0 .. card_list!!.size-1){

                the_day= card_list!![i].substring(6, 8)

                if(card_list!![i].startsWith("0")){
                    the_day = the_day.substring(1)
                }
                //获取到打卡的每天的数字
                the_day_int = Integer.parseInt(the_day)

                if(the_day_int == theday_num){
                    //画打卡
                    mPaint.color = Color.argb(100, 228, 0, 0)
                    val cx = column * perWidth + perWidth.toFloat() / 2
                    val cy = originTop + rowHeight * (rowCount + 1) - 12
                    mPaint.style = Paint.Style.FILL
                    mBorderPaint.style = Paint.Style.STROKE
                    mBorderPaint.color = Color.GRAY
                    mPaint.setShadowLayer(10f, 0f, 5f, Color.GRAY)
                    canvas.drawCircle(cx, cy, 40f, mPaint)
                    canvas.drawCircle(cx, cy, 40f, mBorderPaint)
                    mTextPaint.color = mSelectedColorText
                    mPaint.setShadowLayer(0f, 0f, 0f, Color.GRAY)
                }

            }




            date++
            mBgPaint.style = Paint.Style.FILL
            mBgPaint.color = Color.parseColor("#DDDDDD")
            mTextPaint.color = Color.BLACK
            mPaint.shader = null



        }
    }

    private fun drawTouch(canvas: Canvas?){
        theday_num = 0
        if (placeArray[0]!=0.0f){
            mBgPaint.color = resources.getColor(R.color.black)
            mBgPaint.style = Paint.Style.STROKE
            var x = placeArray[0]
            var y = placeArray[1]
            var curx : Float =x
            var cury : Float =y
            var disx = abs(x- xArray[0]!!)
            var disy = abs(y-yArray[0]!!)
            Log.d("dis",disx.toString())





            for(i in 0..6){

                if (abs(x - xArray[i]) <= disx) {
                    disx = abs(x - xArray[i])
                    curx = xArray[i]
                }

            }


            for (n in 0..4 ){
                Log.d("yarray",yArray[n].toString())

                if(abs(y-yArray[n]) <= disy){
                    disy = abs(y-yArray[n])
                    cury = yArray[n]
                }
            }

            var x_num =0
            var y_num =0
            for(i in 0..6){
                if(curx==xArray[i]){
                    x_num = i+1
                }
                Log.d("xarray",xArray[i].toString())
            }

            for(n in 0..4){
                if(cury==yArray[n]){
                    y_num = n+1
                }
                Log.d("yarray",yArray[n].toString())
            }

            ontouchtime =curyear+ curmonth_string + ((7 * (y_num-1)+x_num)-firstIndex).toString()
            Log.d("ontouch",ontouchtime)

            curx?.let { canvas!!.drawCircle(it, cury, 40f, mBgPaint)
            }
        }
    }

    public fun makecard(){
        makecard_num=1
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float
        val y: Float
        when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.x
                    y = event.y
                    Log.e(TAG, "x = $x")
                    Log.e(TAG, "y = $y")

//                    val judgeYTop = mTitleHeight + mWeekPaddingTop + mWeekHeight + dp2px(30f) + 20f
//                    val judgeYBottom = mTitleHeight + mWeekPaddingTop + mWeekHeight + mDayHeight + dp2px(30f) + 40f
//                    val judgeXEnd = (firstIndex * perWidth).toFloat()
//                    val judgeXStart = 0f
                        //                        Log.e(TAG, "judgeYTop = " + judgeYTop);
                        //                        Log.e(TAG, "judgeYBottom = " + judgeYBottom);
                        //                        Log.e(TAG, "judgeXStart = " + judgeXStart);
                        //                        Log.e(TAG, "judgeXEnd = " + judgeXEnd);


                    placeArray[0]=x
                    placeArray[1]=y

                    invalidate()

                    //mTextPaint.color = mSelectedColor

//                    if (x < judgeXEnd && x > judgeXStart && y > judgeYTop && y < judgeYBottom) {
//                            mListener!!.changeMonth(TYPE.BEFORE)
//                    }
                }
        }



//        if (remains > 0 || firstIndex > 0) {
//            // 只有满足这两个条件才进行点击事件传递
//
//            Toast.makeText(context, "点击", Toast.LENGTH_SHORT).show()
//
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    x = event.x
//                    y = event.y
//                    Log.e(TAG, "x = $x")
//                    Log.e(TAG, "y = $y")
//                    if (firstIndex > 0) {
//                        // 如果第一行有空隙
//                        val judgeYTop = mTitleHeight + mWeekPaddingTop + mWeekHeight + dp2px(30f) + 20f
//                        val judgeYBottom = mTitleHeight + mWeekPaddingTop + mWeekHeight + mDayHeight + dp2px(30f) + 40f
//                        val judgeXEnd = (firstIndex * perWidth).toFloat()
//                        val judgeXStart = 0f
//                        //                        Log.e(TAG, "judgeYTop = " + judgeYTop);
//                        //                        Log.e(TAG, "judgeYBottom = " + judgeYBottom);
//                        //                        Log.e(TAG, "judgeXStart = " + judgeXStart);
//                        //                        Log.e(TAG, "judgeXEnd = " + judgeXEnd);
//                        if (x < judgeXEnd && x > judgeXStart && y > judgeYTop && y < judgeYBottom) {
//                            mListener!!.changeMonth(TYPE.BEFORE)
//                        }
//                    }
//                    if (remains > 0) {
//                        // 最后一行有空隙
//                        val judgeYTop = (mTitleHeight + mWeekPaddingTop + mWeekHeight + mDayHeight * (rowCount + 1)
//                                + dp2px(30f) + (40 * (rowCount + 1)).toFloat() + 20f)
//                        val judgeYBottom = (mTitleHeight + mWeekPaddingTop + mWeekHeight + mDayHeight * (rowCount + 2)
//                                + dp2px(30f) + ((rowCount + 2) * 40).toFloat() + 40f)
//                        val judgeXEnd = width.toFloat()
//                        val judgeXStart = (remains * perWidth).toFloat()
//                        //                        Log.e(TAG, "judgeYTop = " + judgeYTop);
//                        //                        Log.e(TAG, "judgeYBottom = " + judgeYBottom);
//                        //                        Log.e(TAG, "judgeXStart = " + judgeXStart);
//                        //                        Log.e(TAG, "judgeXEnd = " + judgeXEnd);
//                        if (x < judgeXEnd && x > judgeXStart && y > judgeYTop && y < judgeYBottom) {
//                            mListener!!.changeMonth(TYPE.AFTER)
//                        }
//                    }
//                }
//                MotionEvent.ACTION_UP -> {
//                }
//            }
//        }


        return true
    }

    /**
     * 改变月份
     * 以日期为参数 mDate
     */
    fun changeMonth(mDate: Date) {
        setMonth(mDate)
        invalidate()
    }

    /**
     * 改变月份
     * 以数字为参数 +1 -1 change
     */
    fun changeMonth(change: Int) {
        Log.e(TAG, "change = $change")
        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.MONTH, change)
        setMonth(c.time)
        //        invalidate();
        postInvalidate()
    }

    fun getontouchtime(): String {
        if(placeArray[0]!=0.0f){
            var x = placeArray[0]
            var y = placeArray[1]
            var curx : Float =x
            var cury : Float =y
            var disx = abs(x- xArray[0]!!)
            var disy = abs(y-yArray[0]!!)


            for(i in 0..6){

                if (abs(x - xArray[i]) <= disx) {
                    disx = abs(x - xArray[i])
                    curx = xArray[i]
                }

            }


            for (n in 0..4 ){
                Log.d("yarray",yArray[n].toString())

                if(abs(y-yArray[n]) <= disy){
                    disy = abs(y-yArray[n])
                    cury = yArray[n]
                }
            }

            var x_num =0
            var y_num =0
            for(i in 0..6){
                if(curx==xArray[i]){
                    x_num = i+1
                }

            }

            for(n in 0..4){
                if(cury==yArray[n]){
                    y_num = n+1
                }

            }

            ontouchtime =curyear+ curmonth_string + ((7 * (y_num-1)+x_num)-firstIndex).toString()

        }

        return ontouchtime
    }

    override fun invalidate() {
        requestLayout()
        super.invalidate()
    }

    public interface OnMonthChangeListener {
        fun changeMonth(type: TYPE): Unit
    }
}