package com.cmk.app.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import com.cmk.app.R
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentHomeBinding
import com.cmk.app.databinding.LayoutHomeBannerBinding
import com.cmk.app.ui.activity.SearchActivity
import com.cmk.app.ui.adapter.ViewPagerFragmentStateAdapter
import com.cmk.app.viewmodel.HomeViewModel
import com.cmk.app.vo.BannerVo
import com.cmk.app.widget.recyclerview.SecondFloorView
import com.google.android.material.tabs.TabLayoutMediator
import com.zhouwei.mzbanner.holder.MZViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

/**
 * @Author: romens
 * @Date: 2019-11-8 9:19
 * @Desc:
 */
@AndroidEntryPoint
class HomeFragment : BaseFragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeViewModel>()
    private val articleFragment by lazy { ArticleFragment() }
    private val projectFragment by lazy { ProjectFragment() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPager.isUserInputEnabled = false
        val tabTitles = arrayOf("最新博文", "最新项目")
        val fragments = ArrayList<Fragment>().apply {
            add(articleFragment)
            add(projectFragment)
        }

        val adapter = ViewPagerFragmentStateAdapter(
            fragmentManage = childFragmentManager,
            lifecycle = lifecycle, fragments = fragments
        )
        binding.viewPager.adapter = adapter
        val layoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }
        layoutMediator.attach()
        binding.refresh.setIsCanSecondFloor(true)
        binding.refresh.isRefreshing()

        val floorView = SecondFloorView(context)
        floorView.setOnClickCallback {
            binding.refresh.setBackToFirstFloor()
        }

        binding.refresh.setSecondFloorView(floorView)

        viewModel.localhostLogin()
//        viewModel.localhost1()
        viewModel.loadBanners()
//        viewModel.testFlow()

        initEvent()
    }

    private fun initEvent() {
        binding.refresh.setOnRefreshListener {
            if (binding.viewPager.currentItem == 0) {
                articleFragment.mAdapter.refresh()
            }

            if (binding.viewPager.currentItem == 1) {
                projectFragment.mAdapter.refresh()
            }
        }

        binding.ivSearch.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        binding.ivRefresh.setOnClickListener {
            if (binding.viewPager.currentItem == 0) {
                articleFragment.mAdapter.refresh()
            }

            if (binding.viewPager.currentItem == 1) {
                projectFragment.mAdapter.refresh()
            }
//            viewModel.loadBanners()
        }

        binding.ivGoTop.setOnClickListener {
            if (binding.viewPager.currentItem == 0) {
                articleFragment.binding.recyclerview.smoothScrollToPosition(0)
            }

            if (binding.viewPager.currentItem == 1) {
                projectFragment.binding.recyclerview.scrollToPosition(0)
            }
        }

        articleFragment.mAdapter.addLoadStateListener {
            binding.refresh.setRefreshing(it.refresh is LoadState.Loading)
            if (it.refresh is LoadState.Loading)
                articleFragment.binding.recyclerview.smoothScrollToPosition(0)
        }

        projectFragment.mAdapter.addLoadStateListener {
            binding.refresh.setRefreshing(it.refresh is LoadState.Loading)
            if (it.refresh is LoadState.Loading)
                projectFragment.binding.recyclerview.smoothScrollToPosition(0)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.articleScrollState.collect {
                Log.e("滚动-->", "$it")
                if (it) {
                    if (!binding.ivGoTop.isVisible) {
                        binding.ivGoTop.isVisible = true
                        binding.ivGoTop.animation = AnimationUtils.makeInAnimation(context, false)
                    }

                } else {
                    if (binding.ivGoTop.isVisible) {
                        binding.ivGoTop.isVisible = false
                        binding.ivGoTop.animation = AnimationUtils.makeOutAnimation(context, true)
                    }
                }
                Log.e("homeObserve", "article-->$it")
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.projectScrollState.collect {
                if (it) {
                    if (!binding.ivGoTop.isVisible) {
                        binding.ivGoTop.isVisible = true
                        binding.ivGoTop.animation = AnimationUtils.makeInAnimation(context, false)
                    }

                } else {
                    if (binding.ivGoTop.isVisible) {
                        binding.ivGoTop.isVisible = false
                        binding.ivGoTop.animation = AnimationUtils.makeOutAnimation(context, true)
                    }
                }
                Log.e("homeObserve", "project-->$it")
            }
        }
    }

    override suspend fun subscribe() {
        super.subscribe()
        viewModel.apply {

            bannerState.collectLatest {
                binding.banner.setPages(it) { BannerViewHolder() }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.banner.start()
    }

    override fun onPause() {
        super.onPause()
        binding.banner.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class BannerViewHolder : MZViewHolder<BannerVo> {
        lateinit var binding: LayoutHomeBannerBinding
        override fun createView(context: Context): View {
            binding = DataBindingUtil.inflate<LayoutHomeBannerBinding>(
                LayoutInflater.from(context),
                R.layout.layout_home_banner,
                null,
                false
            )
            return binding.root
        }

        override fun onBind(p0: Context?, p1: Int, p2: BannerVo?) {
            binding.data = p2
        }
    }
}