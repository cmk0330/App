package com.cmk.app.ui.adapter.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterArticleBinding
import com.cmk.app.vo.ArticleVo

class Paging3Adapter : PagingDataAdapter<ArticleVo.DataX, BaseViewHolder<ArticleVo.DataX>>(CALLBACK()) {

    private var onViewClickListener: ((ArticleVo.DataX)->Unit)? = null
    private var onItemClickListener: ((ArticleVo.DataX) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ArticleVo.DataX> {
        val binding = DataBindingUtil.inflate<AdapterArticleBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_article,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }


    override fun onBindViewHolder(holder: BaseViewHolder<ArticleVo.DataX>, position: Int) {
        getItem(position)?.let {
            holder.bind(it, onItemClickListener)
            holder.bindItemViewClickListener(onViewClickListener)
        }
    }

    fun setOnViewClickListener(listener: ((ArticleVo.DataX) -> Unit)) {
        onViewClickListener = listener
    }

    fun setListener(clickListener: (data: ArticleVo.DataX) -> Unit) {
        this.onItemClickListener = clickListener
    }

    private class CALLBACK : DiffUtil.ItemCallback<ArticleVo.DataX>() {
        override fun areItemsTheSame(oldItem: ArticleVo.DataX, newItem: ArticleVo.DataX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ArticleVo.DataX,
            newItem: ArticleVo.DataX
        ): Boolean {
            return oldItem == newItem
        }
    }
}