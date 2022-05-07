package com.cmk.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentProjectBinding
import com.cmk.app.ui.activity.WebActivity
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.ProjectListAdapter
import com.cmk.app.viewmodel.HomeViewModel
import com.cmk.app.widget.StatusLayout
import com.cmk.app.widget.configList
import kotlinx.coroutines.flow.collectLatest

/**
 * @Author: romens
 * @Date: 2019-11-8 9:44
 * @Desc:
 */
class ProjectFragment : BaseFragment() {

    private var _binding: FragmentProjectBinding? = null
    val binding get() = _binding!!
    private lateinit var statusLayout: StatusLayout
    private val viewModel by activityViewModels<HomeViewModel>()
    private var layoutManager: LinearLayoutManager? = null
    val mAdapter by lazy { ProjectListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        statusLayout = StatusLayout.init(binding.root)
        return statusLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initStatus()
        layoutManager = LinearLayoutManager(context)
        binding.recyclerview.layoutManager = layoutManager
        val loadMoreAdapter = LoadMoreAdapter()
        binding.recyclerview.adapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        loadMoreAdapter.retryListener { mAdapter.retry() }
        mAdapter.setOnClickListener {
            val intent = Intent(context, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data", it)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getProject().flow.collectLatest {
                mAdapter.submitData(it)
            }
        }
        mAdapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.Error -> statusLayout.switchLayout(StatusLayout.STATUS_ERROR)
                is LoadState.Loading -> statusLayout.switchLayout(StatusLayout.STATUS_LOADING)
                is LoadState.NotLoading -> statusLayout.showDefaultContent()
            }
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    viewModel.setProjectScrollState(layoutManager!!.findFirstVisibleItemPosition() > 0)
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                viewModel.setProjectScrollState(layoutManager!!.findFirstVisibleItemPosition() > 0)
                val linearLayoutManager = binding.recyclerview.layoutManager as LinearLayoutManager
                val findFirstVisibleItemPosition =
                    linearLayoutManager.findFirstVisibleItemPosition()
                val findLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                val findFirstCompletelyVisibleItemPosition =
                    linearLayoutManager.findFirstCompletelyVisibleItemPosition()
                val findLastCompletelyVisibleItemPosition =
                    linearLayoutManager.findLastCompletelyVisibleItemPosition()
                Log.e(
                    "scrollposition-->",
                    "findFirstVisibleItemPosition:$findFirstVisibleItemPosition"
                )
                Log.e(
                    "scrollposition-->",
                    "findLastVisibleItemPosition:$findLastVisibleItemPosition"
                )
                Log.e(
                    "scrollposition-->",
                    "findFirstCompletelyVisibleItemPosition:$findFirstCompletelyVisibleItemPosition"
                )
                Log.e(
                    "scrollposition-->",
                    "findLastCompletelyVisibleItemPosition:$findLastCompletelyVisibleItemPosition"
                )
            }
        })


    }

    /**
     * 初始化statuslayout
     */
    private fun initStatus() {
        statusLayout.add(
            StatusLayout.StatusConfig(
                StatusLayout.STATUS_LOADING,
                R.layout.layout_loading
            )
        )
        statusLayout.add(
            StatusLayout.StatusConfig(
                StatusLayout.STATUS_ERROR,
                R.layout.layout_error,
                clickRes = R.id.btn_retry
            )
        )
        statusLayout.setLayoutClickListener(object : StatusLayout.OnLayoutClickListener {
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