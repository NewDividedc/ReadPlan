package com.example.readproject

import FragmentNote
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(FragmentPlan())

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
        userImage.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

            }
        })
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.frag,fragment)
        transaction.commit()
        transaction.addToBackStack(null)
    }


    fun user_click(view: View){
        val intent = Intent(MainActivity@this, UserActivity::class.java)
        startActivity(intent)
    }

    fun onClick(view: View){

    }
}