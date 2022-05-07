package com.cmk.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentPlazaBinding
import com.cmk.app.ui.activity.WebActivity
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.PlazaListAdapter
import com.cmk.app.viewmodel.PlazaViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * @Author: romens
 * @Date: 2019-11-25 16:28
 * @Desc:
 */
class PlazaFragment : BaseFragment() {

    private var _binding: FragmentPlazaBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlazaViewModel by viewModels()
    private val mAdapter by lazy { PlazaListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlazaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loadMoreAdapter = LoadMoreAdapter()
        binding.recyclerview.adapter = mAdapter.withLoadStateFooter(loadMoreAdapter)
        loadMoreAdapter.retryListener { mAdapter.retry() }
        binding.refresh.setOnRefreshListener {
            mAdapter.refresh()
        }
        mAdapter.setOnClickListener {
            val intent = Intent(context, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("data", it)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        mAdapter.addLoadStateListener {
            binding.refresh.setRefreshing(it.refresh is LoadState.Loading)
        }
        lifecycleScope.launchWhenCreated {
            viewModel.getPlazaList().collectLatest {
                mAdapter.submitData(it)
            }

//            mAdapter.loadStateFlow.collectLatest {
//                binding.refresh.setRefreshing(it.refresh is LoadState.Loading)
//            }
        }
    }

    override suspend fun subscribe() {
        super.subscribe()
    }

//    override fun providerViewModelClass(): Class<PlazaViewModel>? {
//        return PlazaViewModel::class.java
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}