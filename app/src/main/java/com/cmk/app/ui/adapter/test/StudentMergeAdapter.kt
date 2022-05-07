package com.cmk.app.ui.adapter.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterMergeStudentBinding
import com.cmk.app.vo.test.MergeVo

class StudentMergeAdapter :
    RecyclerView.Adapter<StudentMergeAdapter.StudentVH>() {

    private var dataList: MutableList<MergeVo.StudentVo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentVH {
        val binding = DataBindingUtil.inflate<AdapterMergeStudentBinding>(
            LayoutInflater.from(parent.context), R.layout.adapter_merge_student,
            parent, false
        )
        return StudentVH(binding)
    }

    override fun onBindViewHolder(holder: StudentVH, position: Int) {
        val item = dataList[position]
        holder.binding.tvItem.text = item.name
        holder.binding.executePendingBindings()
        holder.binding.ivArrow.setOnClickListener {
            removeItem(position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun submitList(list:List<MergeVo.StudentVo>) {
        val positionStart = dataList.size
        dataList.addAll(list)
        notifyItemRangeInserted(positionStart, list.size)
    }

    fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, dataList.size- position)
//        notifyItemRangeRemoved(position, itemCount)
    }

    class StudentVH(val binding: AdapterMergeStudentBinding) : RecyclerView.ViewHolder(binding.root)

    private class CALLBACK : DiffUtil.ItemCallback<MergeVo.StudentVo>() {
        override fun areItemsTheSame(
            oldItem: MergeVo.StudentVo,
            newItem: MergeVo.StudentVo
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: MergeVo.StudentVo,
            newItem: MergeVo.StudentVo
        ): Boolean {
            return oldItem == newItem
        }
    }


}