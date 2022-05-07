package com.cmk.app.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.ContentFrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.cmk.app.R
import com.cmk.app.base.BaseActivity
import com.cmk.app.base.dataBinding
import com.cmk.app.base.doViewStatus
import com.cmk.app.databinding.ActivityWebBinding
import com.cmk.app.util.lifecycle.ActivityStackManager
import com.cmk.app.viewmodel.WebViewModel
import com.cmk.app.vo.ArticleVo
import com.cmk.app.vo.PlazaListVo
import com.cmk.app.vo.ProjectListVo
import com.cmk.app.vo.SearchResultVo
import com.cmk.app.widget.StatusLayout
import kotlinx.android.synthetic.main.activity_motion_layout.view.*
import kotlinx.android.synthetic.main.view_default_refresh.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

/**
 * @Author: romens
 * @Date: 2019-11-12 8:50
 * @Desc:
 */
class WebActivity : BaseActivity() {

    private lateinit var binding: ActivityWebBinding
    private val viewModel by viewModels<WebViewModel>()
    private val data: Any?
        get() {
            return intent.extras?.getParcelable("data")
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initStatus()
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        binding.statusLayout.switchLayout(StatusLayout.STATUS_EMPTY)
//        binding.statusLayout.switchLayout(StatusLayout.STATUS_LOADING)
        if (data is ArticleVo.DataX) {
            val dataX = data as ArticleVo.DataX
            binding.ivFavour.isSelected = dataX.collect
            if (dataX.link.isEmpty()) binding.statusLayout.switchLayout(StatusLayout.STATUS_EMPTY)
            else binding.webView.loadUrl(dataX.link)
        }

        if (data is ProjectListVo.DataX) {
            val dataX = data as ProjectListVo.DataX
            binding.ivFavour.isSelected = dataX.collect
            binding.webView.loadUrl(dataX.link)
        }

        if (data is PlazaListVo.DataX) {
            val dataX = data as PlazaListVo.DataX
            binding.ivFavour.isSelected = dataX.collect
            binding.webView.loadUrl(dataX.link)
        }

        if (data is SearchResultVo.Author.DataX) {
            val dataX = data as SearchResultVo.Author.DataX
            binding.ivFavour.isSelected = dataX.collect
            binding.webView.loadUrl(dataX.link)
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                binding.tvTitle.text = title
            }
        }
        binding.ivBack.setOnClickListener { finish() }
        binding.ivFavour.setOnClickListener {
            if (binding.ivFavour.isSelected)
                viewModel.unCollect(convertData(data!!))
            else
                viewModel.collect(convertData(data!!))
        }

        binding.llRoot.setAddStatesFromChildren(true)
    }

    /**
     * 初始化statuslayout
     */
    private fun initStatus() {
//        binding.statusLayout.add(
//            StatusLayout.StatusConfig(
//                StatusLayout.STATUS_LOADING,
//                R.layout.layout_loading
//            )
//        )
//        binding.statusLayout.add(
//            StatusLayout.StatusConfig(
//                StatusLayout.STATUS_ERROR,
//                R.layout.layout_error,
//                clickRes = R.id.btn_retry
//            )
//        )
//        binding.statusLayout.add(
//            StatusLayout.StatusConfig(
//                StatusLayout.STATUS_EMPTY,
//                R.layout.layout_empty
//            )
//        )
        binding.statusLayout.setLayoutClickListener(object : StatusLayout.OnLayoutClickListener {
            override fun OnLayoutClick(view: View, status: String?) {
                if (status === StatusLayout.STATUS_ERROR) {
                    Toast.makeText(this@WebActivity, "error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    

    override suspend fun subscribe() {
        super.subscribe()
        var isFirst = 0
        viewModel.collectionState.collect {
            if (isFirst != 0)
                binding.ivFavour.isSelected = it.collect
            isFirst++
        }
    }

    private fun convertData(data: Any): Int {
        if (data is ArticleVo.DataX) return data.id
        if (data is ProjectListVo.DataX) return data.id
        if (data is PlazaListVo.DataX) return data.id
        if (data is SearchResultVo.Author.DataX) return data.id
        return 0
    }
}