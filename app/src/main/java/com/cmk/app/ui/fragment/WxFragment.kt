package com.cmk.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentWxBinding
import com.cmk.app.ext.dp2px
import com.cmk.app.ext.toast
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.VerticalTabAdapter
import com.cmk.app.ui.adapter.WxAdapter
import com.cmk.app.viewmodel.WxViewModel
import com.cmk.app.vo.WxTabVo
import com.cmk.app.widget.SpaceItemDecoration
import kotlinx.coroutines.flow.collect
import q.rorbin.verticaltablayout.VerticalTabLayout
import q.rorbin.verticaltablayout.widget.TabView

/**
 * @Author: romens
 * @Date: 2019-11-8 9:20
 * @Desc:
 */
class WxFragment : BaseFragment() {
    private  var _binding: FragmentWxBinding ?=null
    private val binding get() = _binding!!
    private val viewModel by viewModels<WxViewModel>()
    private val mLayoutManager by lazy { LinearLayoutManager(activity) }
    private val tabList by lazy { ArrayList<WxTabVo>() }
    private val tabAdapter by lazy { VerticalTabAdapter(tabList) }
    private val mAdapter by lazy { WxAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.run {
            layoutManager = mLayoutManager
            addItemDecoration(SpaceItemDecoration(this.dp2px(10)))
            val loadMoreAdapter = LoadMoreAdapter()
            adapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
            loadMoreAdapter.retryListener { mAdapter.retry() }
            mAdapter.setOnClickListener {
                context.toast("id ${it.id}")
            }
        }


        binding.tabLayout.addOnTabSelectedListener(object :
            VerticalTabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabView?, position: Int) {
            }

            override fun onTabSelected(tab: TabView?, position: Int) {
                val firstPotion = mLayoutManager.findFirstVisibleItemPosition()
                val lastPosition = mLayoutManager.findLastVisibleItemPosition()
                viewModel.loadWxList(tabList[position].id)
                when {
                    position <= firstPotion || position >= lastPosition -> binding.recyclerview.smoothScrollToPosition(
                        position
                    )
                    else -> binding.recyclerview.run {
                        smoothScrollBy(
                            0,
                            this.getChildAt(position - firstPotion).top - this.dp2px(8)
                        )
                    }
                }
            }
        })

        viewModel.loadWxTab()

        lifecycleScope.launchWhenCreated {
            viewModel.wxTabState.collect {
                tabList.addAll(it)
                binding.tabLayout.setTabAdapter(tabAdapter)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.loadWxList(408).collect {
                mAdapter.submitData(lifecycle, it)
            }
        }
    }

    override suspend fun subscribe() {
        super.subscribe()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}