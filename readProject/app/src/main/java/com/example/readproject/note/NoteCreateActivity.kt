package com.example.readproject.note

import android.content.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.PermissionChecker
import androidx.core.view.isVisible
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.readproject.DragFloatActionButton
import com.example.readproject.HideAnimationUtils
import com.example.readproject.R
import com.example.readproject.SPUtils
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_note_create.*
import kotlinx.android.synthetic.main.note_bottom.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

open class NoteCreateActivity: AppCompatActivity(),SeekBar.OnSeekBarChangeListener{
    //var db: SQLiteDatabase = Connector.getDatabase()
    var notes= ReadNote();
    private var rootView: RelativeLayout? = null
    private var titleTi: TextInputLayout? = null
    private var titleEt: EditText? = null
    private var contentTi: TextInputLayout? = null
    private var contentEt: EditText? = null
    private var addFAB: CardView? = null
    private var isSaved:Int=1
    private var time:String?=null
    private var idea: ReadNote? = null
    var account1:String?=null
   @JvmField
   @BindView(R.id.lv_bottom)
    var lvBottom: LinearLayout? = null
    private var isShowing = false

    override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_create)
        ButterKnife.bind(this)
        var bundle=this.intent.extras
        if (bundle != null) {
            icAct_title_et.text= Editable.Factory.getInstance().newEditable(bundle.get("title") as CharSequence?)
            icAct_desc_et.text= Editable.Factory.getInstance().newEditable(bundle.get("content") as CharSequence?)
            time= bundle.get("date") as String?
            account1= bundle.get("account") as String?
        }
        else{
            isSaved=-1
        }
        initView()
        initData()
        initEvent()
        val fonts = listOf("瘦金体", "楷体", "华文行楷", "方正粗圆", "方正流行体简体", "方正卡通简体")
        //fontchange.text = "选择字体"
        fontchange.setOnClickListener {
            selector("请选择字体", fonts) {_: DialogInterface, i: Int ->
                //fontchange.text = fonts[i]
                val typeface = Typeface.createFromAsset(assets, "${fonts[i]}.ttf") // 换成需要的字体，字体文件放于 工程名\app\src\main\assets\fonts 下
                icAct_title_et.typeface = typeface
                icAct_desc_et.typeface=typeface
                toast("你选择的字体是${fonts[i]}")
            }
        }
        val sb= findViewById<SeekBar>(R.id.seekbar)
        sb.setOnSeekBarChangeListener(this)
        //tv=(TextView) findViewById(R.id.tv);
    }

    fun searchFile(){
        val fileNames: MutableList<String> = mutableListOf()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File("").walk()
        fileTree.maxDepth(1) //需遍历的目录层次为1，即无须检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
//        .filter { it.extension == "txt"  } //选择扩展名为txt的文本文件
            .filter { it.extension in listOf("ttf") }//选择扩展名为txt或者mp4的文件
            .forEach { fileNames.add(it.name) }//循环 处理符合条件的文件
        fileNames.forEach(::println)
    }

    @OnClick(R.id.icAct_title_et)
        fun OnClick1(v:View){
        if (isShowing) { //如果标题栏是显示状态，则隐藏
            isShowing = false
            lvBottom?.let { HideAnimationUtils(false, it) }
            /*Animation(false);*/
        } else {
            isShowing = true
            lvBottom?.let { HideAnimationUtils(true, it) }
        }

    }
    @OnClick(R.id.icAct_desc_et)
    fun OnClick2(v:View){
        if (isShowing) { //如果标题栏是显示状态，则隐藏
            isShowing = false
            lvBottom?.let { HideAnimationUtils(false, it)}
            /*Animation(false);*/
        } else {
            isShowing = true
            lvBottom?.let { HideAnimationUtils(true, it) }
        }

    }

    @OnClick(R.id.noteexit)
    fun OnClick3(v: View){
        alert("不保留修改吗？", "尊敬的用户") {
            positiveButton("残忍丢弃") { finish()  }
            negativeButton("我再想想") {  }
        }.show()

    }
    @OnClick(R.id.noteclear)
    fun OnClick4(v: View){
        titleEt?.setText("")
        contentEt?.setText("")
    }
    @OnClick(R.id.noteshare)
    fun OnClick5(v: View){
       val bit:Bitmap=shotScrollView(scroll_content)
        saveImageToGallery(this,bit)
        searchFile()
    }
      fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val appDir =File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "笔记截屏");
        // val appDir1 =File(Environment.getext(), "笔记截屏");
        if (!appDir.exists()) {
            // 目录不存在 则创建
            appDir.mkdirs();
        }
        //imageFile = File(filePath, imageName);
        //fos =FileOutputStream(imageFile);
        val fileName:String= System.currentTimeMillis().toString()+".jpg";
        val file = File(appDir,fileName);
        try {
            //file.createNewFile()
            val fos = FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // 保存bitmap至本地
            fos.flush();
            fos.close();
            Toast.makeText(context, "图片保存为" + file.absolutePath, Toast.LENGTH_SHORT).show();

        } catch(e: FileNotFoundException) {
            e.printStackTrace();
            Toast.makeText(context, "未找到文件", Toast.LENGTH_SHORT).show();

        }
        catch (e:Exception) {
            e.printStackTrace();
           // Toast.makeText(context, "图片保存为" + file.absolutePath, Toast.LENGTH_SHORT).show();

        } finally {
            // 其次把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.contentResolver,
                    file.absolutePath, fileName, null);
            } catch ( e:FileNotFoundException) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.absolutePath)));
            if (!bitmap.isRecycled) {
                // bitmap.recycle(); 当存储大图片时，为避免出现OOM ，及时回收Bitmap
                System.gc(); // 通知系统回收
            }
              }
    }
    @OnClick(R.id.fontsizechange)
    fun onClickseekBar(v:View){
        seekbar.isVisible = !seekbar.isVisible
        seekbarprocess.isVisible=!seekbarprocess.isVisible

    }

    /**

     * Scrollview截屏

     *

     * @param scrollView 要截图的ScrollView

     * @return Bitmap

     */

    fun  shotScrollView(scrollView: ScrollView): Bitmap {
        var h = 0;
         var bitmap: Bitmap? = null;
        //var i=0
        val scoll=scrollView.childCount

        for ( i  in 0 until scoll) {
            h += scrollView.getChildAt(i).height;
            scrollView.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));
        }
        bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.RGB_565);
        val canvas: Canvas = Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }
    /*  private void Animation(Boolean Show) {// 显隐动画效果,已统一封装到TitleAnimationUtils，减少代码冗余
        isShowing = Show;
        int fromY;//0表示控件Y轴起点
        int toY;//正值表示下移，负值上移
        if (Show) {//显示
            fromY = -toolbar.getHeight();
            toY = 0;
        } else {//隐藏
            fromY = 0;
            toY = -toolbar.getHeight();
        }
        final TranslateAnimation animation;//平移动画
        animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(400);//设置动画持续毫秒
        animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
        toolbar.startAnimation(animation);
    }*/
    override fun onDestroy(): Unit {
    //ButterKnife.(this)
    super.onDestroy()
}

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        //重载TouchEvent 监听事件
        if (event!!.action == MotionEvent.ACTION_DOWN) { //手指按下
            if (isShowing) { //如果标题栏是显示状态，则隐藏
                isShowing = false
                lvBottom?.let { HideAnimationUtils(false, it) }
                /*Animation(false);*/
            } else {
                isShowing = true
                lvBottom?.let { HideAnimationUtils(true, it) }
            }
        }
        return super.onTouchEvent(event)
    }
    private fun initView() {
        rootView = findViewById<RelativeLayout>(R.id.icAct_layout) as RelativeLayout
        titleEt = findViewById<EditText>(R.id.icAct_title_et) as EditText
        titleTi = findViewById<TextInputLayout>(R.id.icAct_title_textInput) as TextInputLayout
        contentEt = findViewById<EditText>(R.id.icAct_desc_et) as EditText
        contentTi = findViewById<TextInputLayout>(R.id.icAct_desc_ti) as TextInputLayout
        addFAB = findViewById<View>(R.id.note_create_Fad) as CardView

    }
    private fun initData() {
        //presenter.setActionView(this)
        if (idea != null) {
            titleEt?.setText(idea!!.getNoteTitle())
            contentEt?.setText(idea!!.getNoteContent())
        }
    }
    private fun initEvent() {
        addFAB!!.setOnClickListener{
            val title = titleEt!!.text.toString()
            val content: String = contentEt?.text.toString()
            var flag = true
            if (isEmpty(title)) {
                showMessage("title不能为空")
                flag = false
            }
            if (flag && isEmpty(content)) {
                showMessage("content不能为空")
                flag = false
            }
            if (flag) {
                if (idea == null) {
                    idea = ReadNote()
                }
                idea!!.setNoteTitle(title)
                idea!!.setNoteContent(content)
                val currentTime = Date()
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                val dateString = formatter.format(currentTime)
                if(isSaved==-1) {
                    notes.setNoteDate(dateString)
                    notes.setNoteTitle(title)
                    notes.setNoteContent(content)
                    account1= SPUtils.instance.getString("ACCOUNT")
                    notes.setUserAccount(account1)
                    //account1?.let { it1 -> notes.setUserAccount(it1) }
                    notes.save()
                }
                else{
                    val note = ReadNote()
                    note.setNoteDate(dateString)
                    note.setNoteTitle(title)
                    note.setNoteContent(content)
                    note.updateAll("notedate=?", time)
                }
                saveNoteMessage("笔记保存")
                //lvBottom?.let { HideAnimationUtils(false, it) }
                finish()

            }
        }

    }
    private fun showMessage(message: String?) {
        Snackbar.make(rootView!!, message!!, Snackbar.LENGTH_SHORT).show()
    }

    private fun saveNoteMessage(message: String?) {
        showMessage(message)
//        Handler().postDelayed(Runnable {
//            this@IdeaCreateActivity.finish()
//            overridePendingTransition(R.anim.push_in_left, R.anim.push_out_right)
//        }, 1000)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        contentEt?.textSize=progress.toFloat()
        seekbarprocess.text="字体大小为${progress}"
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


}

