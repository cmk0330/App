package com.cmk.app.widget.gallery

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cmk.app.databinding.ActivityAlbumPreviewBinding
import com.cmk.app.ext.dp2px
import com.cmk.app.widget.gallery.entity.Item
import com.cmk.app.widget.recyclerview.itemPadding3

class AlbumPreviewActivity:AppCompatActivity() {

    private lateinit var binding:ActivityAlbumPreviewBinding
    private val mAdapter by lazy{AlbumAdapter()}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumPreviewBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        val gridLayoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.layoutManager = gridLayoutManager
        binding.recyclerView.itemPadding3(3, dp2px(4), false)
        binding.recyclerView.adapter = mAdapter

        val list:MutableList<Item> = ArrayList()
        for (i in 1..20) {
            list.add(Item(0,"",0,0))
        }
        mAdapter.submitList(list)
    }
}