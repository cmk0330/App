//package com.cmk.app.ui.adapter
//
//import android.annotation.SuppressLint
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.paging.PagedListAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.cmk.app.R
//import com.cmk.app.base.BaseViewHolder
//import com.cmk.app.databinding.AdapterSearchBinding
//import com.cmk.app.vo.SearchHistoryVo
//
///**
// * @Author: romens
// * @Date: 2019-12-17 15:28
// * @Desc:
// */
//class SearchHistoryAdapter :
//    PagedListAdapter<SearchHistoryVo, SearchHistoryAdapter.SearcchHistyroVH>(CALLBACK()) {
//
//    private lateinit var onItemClickListener: OnItemClickListener<SearchHistoryVo>
//    private lateinit var onClickListener: OnItemViewClickListener<SearchHistoryVo>
//    private lateinit var onClickListenerH: OnItemViewClickListener<SearchHistoryVo>
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): SearcchHistyroVH {
//        val binding = DataBindingUtil.inflate<AdapterSearchBinding>(
//            LayoutInflater.from(parent.context),
//            R.layout.adapter_search,
//            parent,
//            false
//        )
//        return SearcchHistyroVH(binding, onItemClickListener, onClickListener, onClickListenerH)
//    }
//
//    override fun onBindViewHolder(holder: SearcchHistyroVH, position: Int) {
//        getItem(position)?.let { holder.bind(it) }
//
//    }
//
//    fun setOnClickListener(listener: OnItemClickListener<SearchHistoryVo>) {
//        onItemClickListener = listener
//    }
//
//    fun setOnClick(listener: OnItemViewClickListener<SearchHistoryVo>) {
//        onClickListener = listener
//    }
//
//    fun setOnClickH(listener: OnItemViewClickListener<SearchHistoryVo>) {
//        onClickListenerH = listener
//    }
//
//    class SearcchHistyroVH(
//        val binding: AdapterSearchBinding,
//        val listener: OnItemClickListener<SearchHistoryVo>,
//        val viewListener: OnItemViewClickListener<SearchHistoryVo>,
//        val viewListenerH:OnItemViewClickListener<SearchHistoryVo>
//    ) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun bind(data: SearchHistoryVo) {
//            binding.data = data
//            binding.ivClean.setOnClickListener {
//                viewListener.onItemViewClick(it, data)
//            }
//            binding.ivHistory.setOnClickListener {
//                viewListenerH.onItemViewClick(it, data)
//            }
//            binding.root.setOnClickListener {
//                listener.onItemClick(binding.root, data)
//            }
//            binding.executePendingBindings()
//        }
//    }
//
//    private class CALLBACK : DiffUtil.ItemCallback<SearchHistoryVo>() {
//        override fun areItemsTheSame(
//            oldItem: SearchHistoryVo,
//            newItem: SearchHistoryVo
//        ): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        @SuppressLint("DiffUtilEquals")
//        override fun areContentsTheSame(
//            oldItem: SearchHistoryVo,
//            newItem: SearchHistoryVo
//        ): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//}