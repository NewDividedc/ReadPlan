package com.example.readproject

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


class FragmentHome : Fragment() {

    private val booksTypeList = listOf("小说", "散文", "人文","经管","生活")

    private val fragmentList = ArrayList<BookSListFragment>()

    private val booksItemOneListRec = ArrayList<BooksItemOne>()
    private val booksItemOneListNov = ArrayList<BooksItemOne>()
    private val booksItemOneListPro = ArrayList<BooksItemOne>()
    private val booksItemOneListHum = ArrayList<BooksItemOne>()
    private val booksItemOneListMan = ArrayList<BooksItemOne>()
    private val booksItemOneListLif = ArrayList<BooksItemOne>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val tableLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = view.findViewById<ViewPager>(R.id.view_pager)

        initList()

        val booksRecRecyclerView=view.findViewById<RecyclerView>(R.id.books_recycler_view_rec)
        booksRecRecyclerView.layoutManager=LinearLayoutManager(activity,RecyclerView.HORIZONTAL,false)
        val adapterRec=BooksItemAdapter(booksItemOneListRec,requireContext())
        booksRecRecyclerView.adapter= adapterRec

        adapterRec.setOnItemClickListener(object :OnBookItemClickListener{
            override fun onclick(view: View, position: Int) {
                val intent=Intent(activity,BookDetailActivity::class.java)
                intent.putExtra("bid",booksItemOneListRec[position].bid)
                intent.putExtra("uid",5)
                startActivity(intent)
            }
        })

        for (booksType in booksTypeList) {
            when(booksType){
                "小说"->fragmentList.add(BookSListFragment(booksType,booksItemOneListNov))
                "散文"->fragmentList.add(BookSListFragment(booksType,booksItemOneListPro))
                "人文"->fragmentList.add(BookSListFragment(booksType,booksItemOneListHum))
                "经管"->fragmentList.add(BookSListFragment(booksType,booksItemOneListMan))
                "生活"->fragmentList.add(BookSListFragment(booksType,booksItemOneListLif))
            }
        }

        viewPager.adapter = ViewPagerAdapter(childFragmentManager)
        tableLayout.setupWithViewPager(viewPager)

        tableLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                tableLayout.getTabAt(tab.position)?.select()
                //当tab被选中，改变tab
                viewPager.setCurrentItem(tab.position,true)}
            //当tab被选中，改变viewpage实现同步
            override fun onTabReselected(tab: TabLayout.Tab) {}
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })

        viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageSelected(position: Int) {
                //当viewpage改变时候，改变对应tab，实现同步
                tableLayout.getTabAt(position)?.select()
            }
        })

        return view
    }


    // BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ----> Indicates that only the current fragment will be in the {@link Lifecycle.State#RESUMED}
        inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

            override fun getItem(position: Int): Fragment {
                return fragmentList[position]
            }

            override fun getCount(): Int {
                return fragmentList.size
            }

        //        展示顶部导航栏标题
            override fun getPageTitle(position: Int): CharSequence? {
                return booksTypeList[position]
            }
        }

    fun initList(){

        booksItemOneListRec.add(BookDao(requireActivity()).queryBookbyId(1)!!)
        booksItemOneListRec.add(BookDao(requireActivity()).queryBookbyId(2)!!)
        booksItemOneListRec.add(BookDao(requireActivity()).queryBookbyId(3)!!)
        booksItemOneListRec.add(BookDao(requireActivity()).queryBookbyId(4)!!)
        booksItemOneListRec.add(BookDao(requireActivity()).queryBookbyId(5)!!)

        booksItemOneListNov.add(BookDao(requireActivity()).queryBookbyId(6)!!)
        booksItemOneListNov.add(BookDao(requireActivity()).queryBookbyId(7)!!)
        booksItemOneListNov.add(BookDao(requireActivity()).queryBookbyId(8)!!)
        booksItemOneListNov.add(BookDao(requireActivity()).queryBookbyId(9)!!)

        booksItemOneListPro.add(BookDao(requireActivity()).queryBookbyId(10)!!)
        booksItemOneListPro.add(BookDao(requireActivity()).queryBookbyId(11)!!)
        booksItemOneListPro.add(BookDao(requireActivity()).queryBookbyId(12)!!)
        booksItemOneListPro.add(BookDao(requireActivity()).queryBookbyId(13)!!)

        booksItemOneListHum.add(BookDao(requireActivity()).queryBookbyId(14)!!)
        booksItemOneListHum.add(BookDao(requireActivity()).queryBookbyId(15)!!)
        booksItemOneListHum.add(BookDao(requireActivity()).queryBookbyId(16)!!)
        booksItemOneListHum.add(BookDao(requireActivity()).queryBookbyId(17)!!)

        booksItemOneListMan.add(BookDao(requireActivity()).queryBookbyId(18)!!)
        booksItemOneListMan.add(BookDao(requireActivity()).queryBookbyId(19)!!)
        booksItemOneListMan.add(BookDao(requireActivity()).queryBookbyId(20)!!)
        booksItemOneListMan.add(BookDao(requireActivity()).queryBookbyId(21)!!)

        booksItemOneListLif.add(BookDao(requireActivity()).queryBookbyId(22)!!)
        booksItemOneListLif.add(BookDao(requireActivity()).queryBookbyId(23)!!)
        booksItemOneListLif.add(BookDao(requireActivity()).queryBookbyId(24)!!)
        booksItemOneListLif.add(BookDao(requireActivity()).queryBookbyId(25)!!)
    }

}
