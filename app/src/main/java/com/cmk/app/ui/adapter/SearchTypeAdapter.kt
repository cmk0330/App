package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterSearchTypeBinding
import com.cmk.app.vo.SearchVo

/**
 * @Author: romens
 * @Date: 2019-12-24 13:52
 * @Desc:
 */
class SearchTypeAdapter :
    ListAdapter<SearchVo, BaseViewHolder<SearchVo>>(CALLBACK()) {

    //    private lateinit var onItemClickListener: OnItemClickListener<SearchVo>
    private var onItemClickListener: ((SearchVo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<SearchVo> {
        val binding = DataBindingUtil.inflate<AdapterSearchTypeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_search_type,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<SearchVo>, position: Int) {
        holder.bind(getItem(position), onItemClickListener)
    }

    fun setItemClickListener(listener: ((data: SearchVo) -> Unit)) {
        this.onItemClickListener = listener
    }

    private class CALLBACK : DiffUtil.ItemCallback<SearchVo>() {
        override fun areItemsTheSame(
            oldItem: SearchVo,
            newItem: SearchVo
        ): Boolean {
            return oldItem.typeName == newItem.typeName
        }

        override fun areContentsTheSame(
            oldItem: SearchVo,
            newItem: SearchVo
        ): Boolean {
            return oldItem == newItem
        }
    }
}
