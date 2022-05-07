package com.cmk.app.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityPhotoViewBinding
import com.cmk.app.net.Http
import com.cmk.app.net.splashApi
import com.cmk.app.ui.adapter.PhotoViewAdapter

class PhotoViewActivity : BaseActivity() {
    private lateinit var binding: ActivityPhotoViewBinding
    private val mAdapter by lazy { PhotoViewAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(LayoutInflater.from(this))
        binding.viewPager.adapter = mAdapter

        lifecycleScope.launchWhenCreated {
            val page: Int? = 1
            val count: Int? = 10
            splashApi(Http.gankService.myDynamic("Girl/page/$page/count/$count")) {
                success { list ->
                    val dataList = list.map { it.url }
                    mAdapter.submitList(dataList)
                    Log.e("dataList-->", dataList.toString())
                }
                failure { Log.e("动态图片获取失败-->", "${it.message}") }
                error { errorCode, errorMsg -> Log.e("动态图片获取错误-->", "[$errorCode:$errorMsg]") }
            }
        }
    }
}