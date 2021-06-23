package com.example.readproject.shelfchildFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.readproject.R
import com.example.readproject.dao.Booklist

class list_fragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.shelf_child_fragmentlist, container, false)
    }

    fun refresh(new_booklist: Booklist){
        val fragmentManager = childFragmentManager
        val fragment_content = fragmentManager.findFragmentById(R.id.listContent_frag) as listContentFragment
        fragment_content.refresh(new_booklist)
    }
}