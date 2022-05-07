package com.cmk.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cmk.app.R
import com.cmk.app.databinding.AdapterMineBinding
import com.cmk.app.vo.GirlVo

/**
 * @Author: romens
 * @Date: 2019-11-28 16:52
 * @Desc:
 */
class MineAdapter : ListAdapter<GirlVo.Data, MineAdapter.MineVH>(diffCallback) {

    private var commentClickListener: (() -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MineVH {
        val binding = AdapterMineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MineVH(binding)
    }

    override fun onBindViewHolder(holder: MineVH, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.binding.ivComment.setOnClickListener {
            commentClickListener?.invoke()
        }
    }

    fun setCommentClickListener(listener: () -> Unit) {
        this.commentClickListener = listener
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GirlVo.Data>() {
            override fun areItemsTheSame(oldItem: GirlVo.Data, newItem: GirlVo.Data): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(
                oldItem: GirlVo.Data,
                newItem: GirlVo.Data
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class MineVH(var binding: AdapterMineBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: GirlVo.Data) {
            var fixUrl = ""
            fixUrl = if (data.url.startsWith("http://"))
                data.url.replace("http://", "https://");
            else
                data.url
            val options = RequestOptions()
                //.placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .centerCrop()
            Glide.with(binding.imageView).load(fixUrl).apply(options).into(binding.imageView)
            binding.tvDesc.text = data.desc
        }
    }
}