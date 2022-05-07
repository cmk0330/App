package com.cmk.app.ui.adapter.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterMergeFooterBinding
import com.cmk.app.vo.test.MergeVo

class FooterMergeAdapter :
    ListAdapter<MergeVo.FooterVo, FooterMergeAdapter.FooterVH>(CALLBACK()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FooterVH {
        val binding = DataBindingUtil.inflate<AdapterMergeFooterBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_merge_footer, parent, false
        )
        return FooterVH(binding)
    }

    override fun onBindViewHolder(holder: FooterVH, position: Int) {
        holder.binding.tvFooter.text = getItem(position).name
        holder.binding.executePendingBindings()
    }

    class FooterVH(val binding: AdapterMergeFooterBinding) : RecyclerView.ViewHolder(binding.root)

    private class CALLBACK : DiffUtil.ItemCallback<MergeVo.FooterVo>() {
        override fun areItemsTheSame(
            oldItem: MergeVo.FooterVo,
            newItem: MergeVo.FooterVo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: MergeVo.FooterVo,
            newItem: MergeVo.FooterVo
        ): Boolean {
            return oldItem == newItem
        }
    }

}