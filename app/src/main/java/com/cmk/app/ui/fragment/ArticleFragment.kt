package com.cmk.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.base.BaseFragment
import com.cmk.app.base.statusLayout
import com.cmk.app.databinding.FragmentArticleBinding
import com.cmk.app.net.ERROR
import com.cmk.app.ui.activity.WebActivity
import com.cmk.app.ui.adapter.ArticleAdapter
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.viewmodel.HomeViewModel
import com.cmk.app.widget.StatusLayout
import com.cmk.app.widget.configList
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_web.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart

/**
 * @Author: romens
 * @Date: 2019-11-8 10:49
 * @Desc:
 */
@AndroidEntryPoint
class ArticleFragment : BaseFragment() {

    private var _binding: FragmentArticleBinding? = null
    val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeViewModel>()
    private var layoutManager: LinearLayoutManager? = null
    val mAdapter by lazy { ArticleAdapter() }

    private val loadMoreAdapter: LoadMoreAdapter
        get() = LoadMoreAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStatus()
        layoutManager = LinearLayoutManager(context)
        binding.recyclerview.layoutManager = layoutManager
        binding.recyclerview.adapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        loadMoreAdapter.retryListener { mAdapter.retry() }
        mAdapter.setOnItemClickListener { data ->
            val intent = Intent(context, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data", data)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        mAdapter.setOnViewClickListener {
            Toast.makeText(context, "aaaaa", Toast.LENGTH_SHORT).show()
        }

        // 加载状态方式1---监听实现
        mAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> binding.statusLayout.switchLayout(StatusLayout.STATUS_ERROR)
                is LoadState.Loading -> {
                    binding.statusLayout.switchLayout(StatusLayout.STATUS_LOADING)
                }
                is LoadState.NotLoading -> {
                    binding.statusLayout.showDefaultContent()
                }
            }
        }

        // 加载状态方式2---基于流实现
        lifecycleScope.launchWhenCreated {
            viewModel.getArticle()
                .flow
                .collect {
                    mAdapter.submitData(it)
                }
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    viewModel.setArticleScrollState(layoutManager!!.findFirstVisibleItemPosition() > 0)
                }
            }
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                viewModel.articleScrollLiveData.value = layoutManager!!.findFirstVisibleItemPosition() > 0
//            }
        })
    }

    /**
     * 初始化statuslayout
     */
    private fun initStatus() {
//        StatusLayout.setGlobalData(configList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
