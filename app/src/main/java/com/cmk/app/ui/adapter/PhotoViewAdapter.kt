package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cmk.app.R
import com.cmk.app.databinding.AdapterPhotoViewBinding

class PhotoViewAdapter : ListAdapter<String, PhotoViewAdapter.PhotoViewVH>(diffCallback) {

    private var onItemClickListener: ((String) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewVH {
        val binding =
            AdapterPhotoViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewVH(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewVH, position: Int) {
        holder.bind(getItem(position))
        holder.binding.root.setOnClickListener { onItemClickListener?.invoke(getItem(position)) }
    }

    fun setOnItemClickListener(listener: (String) -> Unit) {
        this.onItemClickListener = listener
    }

    class PhotoViewVH(val binding: AdapterPhotoViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(string: String) {
            val options = RequestOptions()
                //.placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
            Glide.with(binding.imageView).load(string).apply(options).into(binding.imageView)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
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


}