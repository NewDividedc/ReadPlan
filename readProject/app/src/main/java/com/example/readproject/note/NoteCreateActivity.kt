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
import com.example.readproject.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_note_create.*
import kotlinx.android.synthetic.main.note_bottom.*
import org.jetbrains.anko.*
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
    private var isShowing = true
    @JvmField
   @BindView(R.id.lv_bottom)
    var lvBottom: LinearLayout? = null

   //

    override fun onCreate(savedInstanceState: Bundle?): Unit {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_create)
        ButterKnife.bind(this)
        HideAnimationUtils(false,lv_bottom)
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
        val fonts = listOf("?????????", "??????", "????????????", "????????????", "?????????????????????", "??????????????????")
        //fontchange.text = "????????????"
        fontchange.setOnClickListener {
            selector("???????????????", fonts) {_: DialogInterface, i: Int ->
                //fontchange.text = fonts[i]
                val typeface = Typeface.createFromAsset(assets, "${fonts[i]}.ttf") // ?????????????????????????????????????????? ?????????\app\src\main\assets\fonts ???
                icAct_title_et.typeface = typeface
                icAct_desc_et.typeface=typeface
                toast("?????????????????????${fonts[i]}")
            }
        }
        val sb= findViewById<SeekBar>(R.id.seekbar)
        sb.setOnSeekBarChangeListener(this)

        //tv=(TextView) findViewById(R.id.tv);
    }

    fun searchFile(){
        val fileNames: MutableList<String> = mutableListOf()
        //??????????????????????????????????????????????????????
        val fileTree: FileTreeWalk = File("").walk()
        fileTree.maxDepth(1) //???????????????????????????1???????????????????????????
            .filter { it.isFile } //????????????????????????????????????
//        .filter { it.extension == "txt"  } //??????????????????txt???????????????
            .filter { it.extension in listOf("ttf") }//??????????????????txt??????mp4?????????
            .forEach { fileNames.add(it.name) }//?????? ???????????????????????????
        fileNames.forEach(::println)
    }

    @OnClick(R.id.icAct_layout)
        fun OnClick1(v:View){
        toast("?????????????????????")
        if (isShowing) { //??????????????????????????????????????????
            isShowing = false
            lvBottom?.let { HideAnimationUtils(false, it) }
            /*Animation(false);*/
        } else {
            isShowing = true
            lvBottom?.let { HideAnimationUtils(true, it) }
        }

    }
    @OnClick(R.id.noteclear)
    fun OnClick4(v: View){
        titleEt?.setText("")
        contentEt?.setText("")
    }
    @OnClick(R.id.notescreenshot)
    fun Onclickscreenshot(v:View){
        //Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();

        toast("woshi")
        val bit:Bitmap=shotScrollView(scroll_content)
        saveImageToGallery(this,bit)
        searchFile()
    }

      fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val appDir =File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "????????????");
        // val appDir1 =File(Environment.getext(), "????????????");
        if (!appDir.exists()) {
            // ??????????????? ?????????
            appDir.mkdirs();
        }
        //imageFile = File(filePath, imageName);
        //fos =FileOutputStream(imageFile);
        val fileName:String= System.currentTimeMillis().toString()+".jpg";
        val file = File(appDir,fileName);
        try {
            //file.createNewFile()
            val fos = FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // ??????bitmap?????????
            fos.flush();
            fos.close();
            Toast.makeText(context, "???????????????" + file.absolutePath, Toast.LENGTH_SHORT).show();

        } catch(e: FileNotFoundException) {
            e.printStackTrace();
            Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();

        }
        catch (e:Exception) {
            e.printStackTrace();
           // Toast.makeText(context, "???????????????" + file.absolutePath, Toast.LENGTH_SHORT).show();

        } finally {
            // ????????????????????????????????????
            try {
                MediaStore.Images.Media.insertImage(context.contentResolver,
                    file.absolutePath, fileName, null);
            } catch ( e:FileNotFoundException) {
                e.printStackTrace();
            }
            // ????????????????????????
            context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(file.absolutePath)));
            if (!bitmap.isRecycled) {
                // bitmap.recycle(); ???????????????????????????????????????OOM ???????????????Bitmap
                System.gc(); // ??????????????????
            }
              }
    }
    @OnClick(R.id.edit_cancel)
    fun onClickCancel(v: View){
        alert("?????????????????????????????????","???????????????"){
            positiveButton("????????????") { finish() }
            negativeButton("????????????") {  }
        }.show()
        //finish()
    }
    @OnClick(R.id.fontsizechange)
    fun onClickseekBar(v:View){
        seekbar.isVisible = !seekbar.isVisible
        seekbarprocess.isVisible=!seekbarprocess.isVisible

    }

    /**

    ??* Scrollview??????

    ??*

    ??* @param scrollView ????????????ScrollView

    ??* @return Bitmap

    ??*/

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
    /*  private void Animation(Boolean Show) {// ??????????????????,??????????????????TitleAnimationUtils?????????????????????
        isShowing = Show;
        int fromY;//0????????????Y?????????
        int toY;//?????????????????????????????????
        if (Show) {//??????
            fromY = -toolbar.getHeight();
            toY = 0;
        } else {//??????
            fromY = 0;
            toY = -toolbar.getHeight();
        }
        final TranslateAnimation animation;//????????????
        animation = new TranslateAnimation(0, 0, fromY, toY);
        animation.setDuration(400);//????????????????????????
        animation.setFillAfter(true);//???????????????????????????????????????????????????
        toolbar.startAnimation(animation);
    }*/
    override fun onDestroy(): Unit {
    //ButterKnife.(this)
    super.onDestroy()
}

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        //??????TouchEvent ????????????
        if (event!!.action == MotionEvent.ACTION_DOWN) { //????????????
            if (isShowing) { //??????????????????????????????????????????
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
                showMessage("title????????????")
                flag = false
            }
            if (flag && isEmpty(content)) {
                showMessage("content????????????")
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
                account1?.let { it1 -> NoteFileUtils.writeTextToFile(this,content,title, it1) }
                saveNoteMessage("????????????")
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
        seekbarprocess.text="???????????? ${progress}"
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }


}

