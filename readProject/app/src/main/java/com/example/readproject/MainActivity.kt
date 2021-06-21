package com.example.readproject

import com.example.readproject.note.FragmentNote
import android.app.Activity
import android.content.Context
import org.litepal.LitePal
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.readproject.note.NoteViewModel
import com.example.readproject.note.ReadNote
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val myViewModel by viewModels<NoteViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
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
    }

    private fun initListener() {
        /**
         * 搜索框点击事件
         */
        searchBookInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!"".equals(s.toString())){
                    search_delete.visibility=View.VISIBLE
                }
                else{
                    search_delete.visibility=View.GONE
                }
            }
        })

        search_delete.setOnClickListener {
            searchBookInput.setText("")
            search_delete.visibility=View.GONE
        }


        /**
         * 搜索框搜索事件
         */
        searchBookInput.setOnEditorActionListener(object:TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId==EditorInfo.IME_ACTION_SEARCH){
                    if(searchBookInput.text.isEmpty()){
                        Toast.makeText(applicationContext,"书名不能为空！",Toast.LENGTH_SHORT).show()
                    }
                    else if (BookDao(this@MainActivity).queryBookbyName(searchBookInput.text.toString())==null){
                        Toast.makeText(applicationContext,"搜索不到该书！",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val intent=Intent(this@MainActivity,BookDetailActivity::class.java)
                        intent.putExtra("bid",BookDao(this@MainActivity).queryBookbyName(searchBookInput.text.toString())!!.bid)
                        intent.putExtra("uid",5)
                        startActivity(intent)
                    }
                }
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm!!.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
                return true
            }
        })

        /**
         * 侧边栏点击事件
         */
        nav_view.setNavigationItemSelectedListener {
            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_comment -> {
                    val intent=Intent(this,MyCommentActivity::class.java)
                    intent.putExtra("uid",5)
                    startActivity(intent)
                    true
                }
                R.id.nav_scan -> {
                    Toast.makeText(getApplicationContext(), "扫一扫", Toast.LENGTH_SHORT).show()
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev!!.getAction() === MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (isShouldHideInput(v, ev!!)) {//点击的是其他区域，则调用系统方法隐藏软键盘
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm != null) {
                    imm!!.hideSoftInputFromWindow(v!!.windowToken, 0)
                }
            }
            return super.dispatchTouchEvent(ev)
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        return if (window.superDispatchTouchEvent(ev)) {
            true
        } else onTouchEvent(ev)
    }


    fun isShouldHideInput(v: View?, event: MotionEvent): Boolean {
        if (v != null) {
            when (v.id) {
                R.id.searchBookInput -> {
                    val leftTop = intArrayOf(0, 0)
                //获取输入框当前的location位置
                    v!!.getLocationInWindow(leftTop)
                    val left = leftTop[0]
                    val top = leftTop[1]
                    val bottom = top + v!!.getHeight()
                    val right = DisplayUtil.getScreenWidth(this)
                    return if (event.x > left && event.x < right
                            && event.y > top && event.y < bottom) {
                 // 点击的是输入框区域，保留点击EditText的事件
                        false
                    } else {
                        true
                    }
                }
                else -> {
                    return false
                }
            }
        }
        return false
    }

}