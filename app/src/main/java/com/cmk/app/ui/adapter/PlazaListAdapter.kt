package com.cmk.app.ui.adapter

import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterPlazaListBinding
import com.cmk.app.vo.PlazaListVo
import kotlinx.android.synthetic.main.adapter_plaza_head.view.*

/**
 * @Author: romens
 * @Date: 2019-11-26 11:03
 * @Desc:
 */
class PlazaListAdapter :
    PagingDataAdapter<PlazaListVo.DataX, RecyclerView.ViewHolder>(diffCallback) {

    private val HEAD_VIEW: Int = 0
    private val CONTENT_VIEW: Int = 1
    private var onItemClickListener: ((PlazaListVo.DataX) -> Unit)? = null


    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            HEAD_VIEW
        else
            CONTENT_VIEW
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEAD_VIEW) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_plaza_head, parent, false)
            HeadViewVH(view)
        } else {
            val binding = DataBindingUtil.inflate<AdapterPlazaListBinding>(
                LayoutInflater.from(parent.context),
                R.layout.adapter_plaza_list,
                parent,
                false
            )
            ContentVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ContentVH) {
            getItem(position)?.let { holder.bind(it, onItemClickListener) }
        }
    }

    fun setOnClickListener(listener: ((PlazaListVo.DataX) -> Unit)? = null) {
        onItemClickListener = listener
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class HeadViewVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
//            tvUser.text = "${App.CURRENT_USER.nickname}   你好"

            val bColorSpan = BackgroundColorSpan(itemView.context.getColor(R.color.blue))
            val fColorSpan = ForegroundColorSpan(itemView.context.getColor(R.color.blue))
            val spannable1 = SpannableString(itemView.textView1.text)
            spannable1.setSpan(bColorSpan, 5, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            itemView.textView1.text = spannable1
            val spannable2 = SpannableString(itemView.textView2.text)
            spannable2.setSpan(
                fColorSpan,
                6,
                itemView.textView2.text.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            itemView.textView2.text = spannable2
            val spannable3 = SpannableString(itemView.textView3.text)
            spannable3.setSpan(
                fColorSpan,
                itemView.textView3.text.length - 5,
                itemView.textView3.text.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            itemView.textView3.text = spannable3
        }
    }

    class ContentVH(val binding: AdapterPlazaListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PlazaListVo.DataX, listener: ((PlazaListVo.DataX) -> Unit)? = null) {
            binding.data = data
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener?.invoke(data) }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PlazaListVo.DataX>() {
            override fun areItemsTheSame(
                oldItem: PlazaListVo.DataX,
                newItem: PlazaListVo.DataX
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlazaListVo.DataX,
                newItem: PlazaListVo.DataX
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
