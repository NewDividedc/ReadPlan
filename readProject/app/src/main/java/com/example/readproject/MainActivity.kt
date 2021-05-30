package com.example.readproject

import FragmentNote
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

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
         * 侧边栏点击事件
         */
        nav_view.setNavigationItemSelectedListener {
            // Handle navigation view item clicks here.
            when (it.itemId) {
                R.id.nav_comment -> {
                    Toast.makeText(getApplicationContext(), "我的评论", Toast.LENGTH_SHORT).show()
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
}