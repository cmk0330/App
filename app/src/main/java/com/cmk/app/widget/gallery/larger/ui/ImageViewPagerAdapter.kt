package com.cmk.app.widget.gallery.larger.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.databinding.AdapterImageViewPagerBinding
import com.cmk.app.widget.gallery.larger.entity.LargerItemBean
import kotlinx.android.extensions.LayoutContainer

class ImageViewPagerAdapter : ListAdapter<LargerItemBean, ImageViewPagerAdapter.ImageViewPagerVH>(
    diffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewPagerVH {
        val binding =
            AdapterImageViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewPagerVH(
            binding.root
        )
    }

    override fun onBindViewHolder(holder: ImageViewPagerVH, position: Int) {
        getItem(position)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<LargerItemBean>() {
            override fun areItemsTheSame(
                oldItem: LargerItemBean,
                newItem: LargerItemBean
            ): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(
                oldItem: LargerItemBean,
                newItem: LargerItemBean
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ImageViewPagerVH(override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    }
}

