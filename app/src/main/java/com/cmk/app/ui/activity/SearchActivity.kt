package com.cmk.app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.filter
import androidx.paging.flatMap
import androidx.paging.map
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.cmk.app.R
import com.cmk.app.base.BaseActivity
import com.cmk.app.base.dataBinding
import com.cmk.app.databinding.ActivitySearchBinding
import com.cmk.app.listener.textWatcher
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.SearchResultAdapter
import com.cmk.app.ui.adapter.SearchTypeAdapter
import com.cmk.app.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest


/**
 * @Author: romens
 * @Date: 2019-12-17 14:52
 * @Desc:  https://www.jianshu.com/p/6dd6c89220d9
 */
class SearchActivity : BaseActivity() {

    private val binding by dataBinding<ActivitySearchBinding>(R.layout.activity_search)
    private val viewModel by viewModels<SearchViewModel>()

    //    private val mAdapter: SearchHistoryAdapter by lazy { SearchHistoryAdapter() }
    private val typeAdapter: SearchTypeAdapter by lazy { SearchTypeAdapter() }
    private val resultAdapter: SearchResultAdapter by lazy { SearchResultAdapter() }
    private val applySet: ConstraintSet by lazy { ConstraintSet() }
    private val resetSet: ConstraintSet by lazy { ConstraintSet() }
    private var isResult: Boolean = false

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.recyclerviewType.adapter = typeAdapter
        val loadMoreAdapter = LoadMoreAdapter()
        binding.recyclerviewResult.adapter = resultAdapter.withLoadStateFooter(loadMoreAdapter)
        loadMoreAdapter.retryListener { resultAdapter.retry() }

        initAnim()
        resultReset()
        initEvent()

        viewModel.loadType()
        viewModel.loadHotKey()
        viewModel.getSearchHistoryAll()

        lifecycleScope.launchWhenCreated {

            resultAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
//                    binding.recyclerviewResult.setBackgroundResource(R.drawable.shape_recycler_bg)
                }
            }
        }
    }

    private fun initAnim() {
        applySet.clone(binding.clSearch)
        resetSet.clone(binding.clSearch)
    }

    fun initEvent() {
        binding.tvCancel.setOnClickListener {
            finish()
        }

        binding.etContent.textWatcher {
            afterTextChanged {
                binding.ivInputClean.isVisible = it.toString().isNotBlank()
                viewModel.getSearchAuthor(it.toString())
                if (it.toString().isBlank()) {
                    binding.recyclerviewResult.background = null
                }
            }
        }

        binding.ivInputClean.setOnClickListener {
            binding.etContent.setText("")
//            mViewModel.loadHotKey()
        }

        typeAdapter.setItemClickListener {
            resultApply()
            binding.etContent.hint = it.typeName
        }

        resultAdapter.setItemClickListener {
            val intent = Intent(this@SearchActivity, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data", it)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        binding.setOnClick {
            resultReset()
        }
    }

    private fun resultApply() {
        val transition = AutoTransition()
        transition.duration = 200
        TransitionManager.beginDelayedTransition(binding.clSearch, transition)
        applySet.setVisibility(R.id.iv_back, View.VISIBLE)
        applySet.setVisibility(R.id.tv_type_head, View.GONE)
        applySet.setVisibility(R.id.recyclerview_type, View.GONE)
        applySet.setVisibility(R.id.recyclerview_result, View.VISIBLE)
        applySet.applyTo(binding.clSearch)
    }

    private fun resultReset() {
        val transition = AutoTransition()
        transition.duration = 200
        TransitionManager.beginDelayedTransition(binding.clSearch, transition)
        resetSet.setVisibility(R.id.iv_back, View.GONE)
        applySet.setVisibility(R.id.tv_type_head, View.VISIBLE)
        applySet.setVisibility(R.id.recyclerview_type, View.VISIBLE)
        applySet.setVisibility(R.id.recyclerview_result, View.GONE)
        resetSet.applyTo(binding.clSearch)
        binding.etContent.setText("")
//        binding.recyclerviewResult.background = null
        resultAdapter.refresh()
//        resultAdapter.submitList(ArrayList<SearchResultVo.Author.DataX>())
    }

    private fun showItemPop(view: View, msg: String) {
        val v = LayoutInflater.from(this).inflate(R.layout.popup_item, null)
        v.findViewById<TextView>(R.id.tv_message).text = msg

        val window = PopupWindow(
            v,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        window.isOutsideTouchable = true
//        window.elevation = 1000f
//        window.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        window.showAsDropDown(view)
    }

    override suspend fun subscribe() {
        super.subscribe()
        viewModel.typeState.collect {
            typeAdapter.submitList(it)
        }

        viewModel.searchState.collect {
            val hotKeyVo = it.random()
            binding.etContent.hint = hotKeyVo.name
        }

        viewModel.searchAuthorLiveData.observe(this@SearchActivity, {
            resultAdapter.submitData(lifecycle, it)
            it.map { dataX ->
                binding.recyclerviewResult.setBackgroundResource(R.drawable.shape_recycler_bg)
            }
        })
//        mViewModel.historyPagedLiveData.observe(this, Observer {
//            mAdapter.submitList(it)
//        })
    }

}