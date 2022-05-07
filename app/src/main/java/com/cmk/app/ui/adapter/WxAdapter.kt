package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterWxBinding
import com.cmk.app.vo.WxListVo

/**
 * @Author: romens
 * @Date: 2019-12-2 9:15
 * @Desc:
 */
class WxAdapter : PagingDataAdapter<WxListVo.DataX, BaseViewHolder<WxListVo.DataX>>(diffCallback) {

    //    private lateinit var onItemClickListener: OnItemClickListener<WxListVo.DataX>
    private var onItemClickListener: ((WxListVo.DataX) -> Unit)? = null

    override fun onBindViewHolder(holder: BaseViewHolder<WxListVo.DataX>, position: Int) {
        getItem(position)?.let { holder.bind(it, onItemClickListener) }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<WxListVo.DataX> {
        val binding = DataBindingUtil.inflate<AdapterWxBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_wx,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    fun setOnClickListener(listener: (data: WxListVo.DataX) -> Unit) {
        this.onItemClickListener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<WxListVo.DataX>() {
            override fun areItemsTheSame(
                oldItem: WxListVo.DataX,
                newItem: WxListVo.DataX
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: WxListVo.DataX,
                newItem: WxListVo.DataX
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}