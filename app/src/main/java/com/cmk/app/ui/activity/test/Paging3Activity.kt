package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.paging.map
import com.cmk.app.R
import com.cmk.app.base.BaseDBActivity
import com.cmk.app.databinding.ActivityPaging3Binding
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.test.Paging3Adapter
import com.cmk.app.ui.fragment.test.GroupInterface
import com.cmk.app.ui.fragment.test.StickItemDecoration
import com.cmk.app.viewmodel.HomeViewModel
import com.cmk.app.widget.StatusLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

class Paging3Activity : BaseDBActivity<ActivityPaging3Binding>() {
    override val layoutId: Int
        get() = R.layout.activity_paging3
    private val viewModel by viewModels<HomeViewModel>()

    val mAdapter by lazy { Paging3Adapter() }

    @ExperimentalPagingApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initStatus()

        val loadMoreAdapter = LoadMoreAdapter()
//        mAdapter.withLoadStateFooter(loadMoreAdapter)

        binding.recyclerView.adapter = mAdapter.withLoadStateFooter(loadMoreAdapter)

        loadMoreAdapter.retryListener { mAdapter.retry() }
        //因为刷新前也会调用LoadState.NotLoading，所以用一个外部变量判断是否是刷新后
        var hasRefreshing = false
        binding.refreshLayout.setOnRefreshListener { mAdapter.refresh() }
        // 加载状态方式1---监听实现
        mAdapter.addLoadStateListener {
            binding.refreshLayout.setRefreshing(it.refresh is LoadState.Loading)
            when (it.refresh) {
                is LoadState.Error -> {//加载失败 （加载数据失败回调）
                    binding.statusLayout.switchLayout(StatusLayout.STATUS_ERROR)
                }
                is LoadState.Loading -> {//加载中 (加载数据时候回调)
                    hasRefreshing = true
                    //如果是手动下拉刷新，则不展示loading页
                    if (!binding.refreshLayout.isRefreshing())
                        binding.statusLayout.showDefaultContent()
                }
                is LoadState.NotLoading -> {//没有加载中 (加载数据前和加载数据完成后回调)
                    if (hasRefreshing) {
                        hasRefreshing = false
                        binding.statusLayout.showDefaultContent()
                        binding.refreshLayout.setRefreshing(false)
                        //如果第一页数据就没有更多了，第一页不会触发append
                        if (it.source.append.endOfPaginationReached) {
                            binding.refreshLayout.setRefreshing(false)
                        }
                    }

                }
            }
            var hasLoadMore = false
            when (it.append) {
                is LoadState.Loading -> {
                    hasLoadMore = true
                    binding.refreshLayout.setRefreshing(false)
                }
                is LoadState.NotLoading -> {
                    if (hasLoadMore) {
                        hasLoadMore = false
                        if (it.source.append.endOfPaginationReached) {
                            binding.refreshLayout.setRefreshing(false)
                        } else {

                        }
                    }
                }
                is LoadState.Error -> {
                    binding.statusLayout.switchLayout(StatusLayout.STATUS_ERROR)
                }
            }
        }
        //加载状态方式2---基于流实现
//        lifecycleScope.launchWhenCreated {
//            mAdapter.loadStateFlow.collectLatest {
//                binding.refreshLayout.isRefreshing = it.refresh is LoadState.Loading
//                when( it.refresh) {
//                    is LoadState.Error ->binding.statusLayout.switchLayout(StatusLayout.STATUS_ERROR)
//                }
//            }
//        }


        lifecycleScope.launchWhenCreated {
            //collectLatest: 如果在下游没有处理完情况下上游继续下个发射会导致上次的下游被取消
            viewModel.pager.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun subscribe() {
        super.subscribe()

    }

    /**
     * 初始化statuslayout
     *
     */
    fun initStatus() {
        binding.statusLayout.add(
            StatusLayout.StatusConfig(
                StatusLayout.STATUS_LOADING,
                R.layout.layout_loading
            )
        )
        binding.statusLayout.add(
            StatusLayout.StatusConfig(
                StatusLayout.STATUS_ERROR,
                R.layout.layout_error,
                clickRes = R.id.btn_retry
            )
        )
        binding.statusLayout.setLayoutClickListener(object : StatusLayout.OnLayoutClickListener {
            override fun OnLayoutClick(view: View, status: String?) {
                if (status === StatusLayout.STATUS_ERROR) {
                    mAdapter.retry()
                }
            }
        })
    }
}