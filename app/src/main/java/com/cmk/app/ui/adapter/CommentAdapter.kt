package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.databinding.AdapterCommentBinding

class CommentAdapter : ListAdapter<String, CommentAdapter.CommentVH>(CALLBACK()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val binding =
            AdapterCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentVH(binding)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(getItem(position))
    }

    class CommentVH(val binding: AdapterCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: String) {
            binding.tvComment.text = data
        }
    }

    private class CALLBACK : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem.length == newItem.length
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}