package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterSearchResultBinding
import com.cmk.app.vo.SearchResultVo

/**
 * @Author: romens
 * @Date: 2019-12-25 11:39
 * @Desc:
 */

class SearchResultAdapter :
    PagingDataAdapter<SearchResultVo.Author.DataX, BaseViewHolder<SearchResultVo.Author.DataX>>
        (diffCallback) {
    private var onItemClickListener: ((SearchResultVo.Author.DataX) -> Unit)? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<SearchResultVo.Author.DataX> {
        val binding = DataBindingUtil.inflate<AdapterSearchResultBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_search_result,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<SearchResultVo.Author.DataX>,
        position: Int
    ) {
        holder.bind(getItem(position), onItemClickListener)
    }

    fun setItemClickListener(listener: ((data: SearchResultVo.Author.DataX) -> Unit)) {
        onItemClickListener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<SearchResultVo.Author.DataX>() {
            override fun areItemsTheSame(
                oldItem: SearchResultVo.Author.DataX,
                newItem: SearchResultVo.Author.DataX
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: SearchResultVo.Author.DataX,
                newItem: SearchResultVo.Author.DataX
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
