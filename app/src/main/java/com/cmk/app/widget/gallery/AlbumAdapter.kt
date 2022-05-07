package com.cmk.app.widget.gallery

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.databinding.AdapterAlbumCaptureBinding
import com.cmk.app.databinding.AdapterAlbumMediaBinding
import com.cmk.app.widget.gallery.entity.Item

class AlbumAdapter : ListAdapter<Item, RecyclerView.ViewHolder>(diffCallback) {

    private var onItemClickListener: ((Item) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isCapture) VIEW_TYPE_CAPTURE else VIEW_TYPE_MEDIA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_CAPTURE) {
            val binding =
                AdapterAlbumCaptureBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            AlbumCaptureVH(binding)
        } else {
            val binding =
                AdapterAlbumMediaBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            AlbumMediaVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumCaptureVH) {
            holder.binding.root.setOnClickListener { onItemClickListener?.invoke(getItem(position)) }
        }
        if (holder is AlbumMediaVH) {

        }
    }

    fun setOnItemClickListener(listener: ((Item) -> Unit)){
        this.onItemClickListener = listener
    }

    class AlbumCaptureVH(val binding: AdapterAlbumCaptureBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    class AlbumMediaVH(val binding: AdapterAlbumMediaBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    companion object {
        private const val VIEW_TYPE_CAPTURE: Int = 0x0
        private const val VIEW_TYPE_MEDIA: Int = 0x1
        private val diffCallback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(
                oldItem: Item,
                newItem: Item
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Item,
                newItem: Item
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

