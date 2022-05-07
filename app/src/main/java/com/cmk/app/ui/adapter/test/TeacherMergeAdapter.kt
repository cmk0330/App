package com.cmk.app.ui.adapter.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterMergeTeacherBinding
import com.cmk.app.vo.test.MergeVo
import javax.inject.Inject

class TeacherMergeAdapter @Inject constructor() :
    ListAdapter<MergeVo.TeacherVo, TeacherMergeAdapter.TeacherVH>(CALLBACK()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherVH {
        val binding = DataBindingUtil.inflate<AdapterMergeTeacherBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_merge_teacher,
            parent,
            false
        )
        return TeacherVH(binding)
    }

    override fun onBindViewHolder(holder: TeacherVH, position: Int) {
        holder.binding.tvItem.text = getItem(position).name
        holder.binding.executePendingBindings()
    }

    class TeacherVH(val binding: AdapterMergeTeacherBinding) : RecyclerView.ViewHolder(binding.root)

    private class CALLBACK : DiffUtil.ItemCallback<MergeVo.TeacherVo>() {
        override fun areItemsTheSame(
            oldItem: MergeVo.TeacherVo,
            newItem: MergeVo.TeacherVo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: MergeVo.TeacherVo,
            newItem: MergeVo.TeacherVo
        ): Boolean {
            return oldItem == newItem
        }
    }

}