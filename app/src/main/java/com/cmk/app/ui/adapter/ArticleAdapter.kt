package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterArticleBinding
import com.cmk.app.vo.ArticleVo

/**
 * @Author: romens
 * @Date: 2019-11-8 17:31
 * @Desc:
 */

class ArticleAdapter :
    PagingDataAdapter<ArticleVo.DataX, BaseViewHolder<ArticleVo.DataX>>(diffCallback) {

    private var onViewClickListener: ((ArticleVo.DataX) -> Unit)? = null
    private var onViewClickListener1: OnViewClickListener<ArticleVo.DataX>? = null
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
        getItem(position)?.let { data ->
            holder.bind(data, onItemClickListener)
            val binding = holder.binding as AdapterArticleBinding
            binding.checkbox.setOnCheckedChangeListener(null)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked -> data.check = isChecked }
            binding.checkbox.isChecked = data.check
            binding.ivFavour.setOnClickListener {
                onViewClickListener?.invoke(data)
            }
        }
    }

    fun setOnViewClickListener(listener: (ArticleVo.DataX) -> Unit) {
        onViewClickListener = listener
    }

    fun setOnItemClickListener(listener: (ArticleVo.DataX) -> Unit) {
        this.onItemClickListener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ArticleVo.DataX>() {
            override fun areItemsTheSame(
                oldItem: ArticleVo.DataX,
                newItem: ArticleVo.DataX
            ): Boolean {
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
}
