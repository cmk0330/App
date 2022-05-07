package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.cmk.app.R
import com.cmk.app.base.BaseViewHolder
import com.cmk.app.databinding.AdapterProjectListBinding
import com.cmk.app.vo.ProjectListVo

/**
 * @Author: romens
 * @Date: 2019-11-26 11:03
 * @Desc:
 */

class ProjectListAdapter :
    PagingDataAdapter<ProjectListVo.DataX, BaseViewHolder<ProjectListVo.DataX>>(diffCallback) {
    private var onItemClickListener: ((ProjectListVo.DataX) -> Unit)? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ProjectListVo.DataX> {
        val binding = DataBindingUtil.inflate<AdapterProjectListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_project_list,
            parent,
            false
        )
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ProjectListVo.DataX>, position: Int) {
        getItem(position)?.let { holder.bind(it, onItemClickListener) }
    }

    fun setOnClickListener(listener: (data: ProjectListVo.DataX) -> Unit) {
        onItemClickListener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ProjectListVo.DataX>() {
            override fun areItemsTheSame(
                oldItem: ProjectListVo.DataX,
                newItem: ProjectListVo.DataX
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProjectListVo.DataX,
                newItem: ProjectListVo.DataX
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
