package com.example.readproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.example.readproject.shelfchildFragment.list_fragment
import com.example.readproject.shelfchildFragment.shelf_fragment
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_shelf.*
import java.lang.reflect.Array.newInstance

class FragmentShelf : Fragment() {
    val menus = listOf<String>("书架","书单")
    var fragment_list = listOf<Fragment>(list_fragment(),shelf_fragment())

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shelf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewPager : ViewPager = view.findViewById(R.id.viewpager)
        val tabLayout : TabLayout = view.findViewById(R.id.tablayout)
        val adapter = ShelfFragmentAdapter(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

    inner class ShelfFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            var fragment: Fragment = shelf_fragment()
            when (position){
                0 ->{
                    Toast.makeText(context,"书架",Toast.LENGTH_LONG).show()
                    fragment = shelf_fragment()
                }
                1 ->{
                    Toast.makeText(context,"书单",Toast.LENGTH_LONG).show()
                    fragment = list_fragment()
                }
            }
            return fragment
        }
        override fun getPageTitle(position: Int): CharSequence? {
            return menus[position]
        }
        override fun getCount(): Int {
            return 2
        }
    }
}