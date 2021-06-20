package com.example.readproject


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.readproject.note.FragmentNote
import com.example.readproject.note.NoteViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.view.*
import kotlinx.android.synthetic.main.bar_header.view.*

class MainActivity : AppCompatActivity() {
    private val myViewModel by viewModels<NoteViewModel>()
    var account:String?=null
    var CAMERA_CODE = 0
    var picture_scene_img: ImageView?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        //setContentView(R.layout.bar_header)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.user)
        setSupportActionBar(toolbar)
        replaceFragment(FragmentPlan())
        initActionBarDrawer()
        initListener()
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frag,fragment)
        transaction.commit()
        transaction.addToBackStack(null)
    }

    private fun initActionBarDrawer() {
        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        //Toast.makeText(this, "extraData", Toast.LENGTH_SHORT).show()
    }

    private fun initListener() {
        /**
         * 侧边栏点击事件
         */
        nav_view.getHeaderView(0).useraccount.text = SPUtils.instance.getString("ACCOUNT")
        nav_view.setNavigationItemSelectedListener {

            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_comment -> {
                    Toast.makeText(getApplicationContext(), "我的评论", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_scan -> {
                    Toast.makeText(getApplicationContext(), "扫一扫", Toast.LENGTH_SHORT).show()
                    this.takePhoto()
                    true
                }
                R.id.nav_set -> {
                    Toast.makeText(getApplicationContext(), "通知设置", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_safety -> {
                    Toast.makeText(getApplicationContext(), "账号安全", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_help -> {
                    Toast.makeText(getApplicationContext(), "帮助", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(getApplicationContext(), "退出登录", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }
            }//关闭侧边栏
            drawer_layout.closeDrawer(GravityCompat.START)

            true

        }
        bnv.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.plan->{
                    replaceFragment(FragmentPlan())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bookshelf->{
                    replaceFragment(FragmentShelf())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bookhome->{
                    replaceFragment(FragmentHome())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.note->{
//
                    replaceFragment(FragmentNote())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }

    }
    fun clickMessage(view:View){
        val intent = Intent(this, MessageActivity::class.java)
        startActivity(intent)
    }
    private fun takePhoto() {

        val state: String = Environment.getExternalStorageState() //拿到sdcard是否可用的状态码

        if (state == Environment.MEDIA_MOUNTED) {   //如果可用
            val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_CODE)
        } else {
            Toast.makeText(this, "sdcard不可用", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_CODE && resultCode== RESULT_OK){
            var bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            //picture_scene_img?.setImageBitmap(bitmap)
            nav_view.nav_view.getHeaderView(0).headimage.setImageBitmap(bitmap)
        }
    }

}