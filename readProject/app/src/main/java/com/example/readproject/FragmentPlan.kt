package com.example.readproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.readproject.ui.FragmentDayPlan
import com.example.readproject.ui.FragmentMonthPlan


class  FragmentPlan:Fragment(){
    lateinit var tabLayout: TabLayout
    lateinit var viewPager:ViewPager
    private val liststr = ArrayList<String>(3)
    private val mFragment = ArrayList<Fragment>(3)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_plan,container,false)
        tabLayout=view.findViewById(R.id.tabLayout)
        viewPager=view.findViewById(R.id.viewPager)
        initPager()

        return view
    }


    private fun initPager() {
        var newFragment=FragmentDayPlan()
        var replyFragment=FragmentMonthPlan()
        mFragment.add(newFragment)
        mFragment.add(replyFragment)

        liststr.add("日计划")
        liststr.add("月计划")
        //关联viewpager
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter=MyAdapter(mFragment,liststr,childFragmentManager)

    }
    class  MyAdapter(mFragment:ArrayList<Fragment>,listStr:ArrayList<String>,fm: FragmentManager) : FragmentPagerAdapter(fm) {
        var mFragment=mFragment
        var listStr=listStr

        override fun getItem(position: Int): Fragment {
            return mFragment[position]
        }

        override fun getCount(): Int {
            return mFragment.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listStr[position]
        }

    }
}