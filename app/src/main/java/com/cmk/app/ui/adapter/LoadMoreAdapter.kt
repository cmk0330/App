package com.cmk.app.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterLoadmoreBinding

class LoadMoreAdapter : LoadStateAdapter<LoadMoreVH>() {

    private var retryBlock: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadMoreVH {
        val binding = DataBindingUtil.inflate<AdapterLoadmoreBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_loadmore,
            parent,
            false
        )

        return LoadMoreVH(binding, loadState,retryBlock)
    }

    override fun onBindViewHolder(holder: LoadMoreVH, loadState: LoadState) {
       holder.bindState(loadState)
    }

    fun retryListener(retryBlock: () -> Unit) {
        this.retryBlock = retryBlock
    }
}

class LoadMoreVH(
    val binding: AdapterLoadmoreBinding,
    loadState: LoadState,
    val retryBlock: (() -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {

    val loading = ObservableBoolean()
    val error = ObservableBoolean()

    init {
        bindState(loadState)
    }

    fun bindState(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.tvRetry.isVisible = loadState is LoadState.Error
        binding.tvNoMore.isVisible = loadState is LoadState.NotLoading
        Log.e("loadmore-->",LoadState.NotLoading(true).toString())
        Log.e("loadmoreAdapter-->", "${loadState.endOfPaginationReached}")
//        binding.itemView = this
//        loading.set(loadState is LoadState.Loading)
//        error.set(loadState is LoadState.Error)
        binding.tvRetry.setOnClickListener { retryBlock?.invoke() }
        binding.executePendingBindings()
    }
}