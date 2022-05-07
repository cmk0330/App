package com.cmk.app.ui.adapter.test

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EdgeEffect
import androidx.databinding.DataBindingUtil
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.R
import com.cmk.app.databinding.AdapterTestBinding
import com.cmk.app.widget.recyclerview.FLING_TRANSLATION_MAGNITUDE
import com.cmk.app.widget.recyclerview.OVERSCROLL_TRANSLATION_MAGNITUDE
import com.cmk.app.widget.recyclerview.forEachVisibleHolder

class TestAdapter : ListAdapter<String, TestAdapter.TestVH>(CALLBACK()) {

    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestVH {
        val binding = DataBindingUtil.inflate<AdapterTestBinding>(
            LayoutInflater.from(parent.context),
            R.layout.adapter_test,
            parent,
            false
        )
        return TestVH(binding)
    }

    override fun onBindViewHolder(holder: TestVH, position: Int) {
        holder.binding.tvItem.text = getItem(position)
        holder.bind(position, onItemClickListener)
        holder.binding.executePendingBindings()
    }

    fun setOnItemClickListener(listener: (position: Int) -> Unit) {
        onItemClickListener = listener
    }

    class TestVH(val binding: AdapterTestBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position:  Int, listener: ((Int) -> Unit)?) {
            binding.root.setOnClickListener {
                listener?.invoke(position)
            }
        }

        val translationY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
            .setSpring(
                SpringForce()
                    .setFinalPosition(0f)
                    .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                    .setStiffness(SpringForce.STIFFNESS_LOW)
            )
    }

    private class CALLBACK : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem.length == newItem.length
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    val edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            return object : EdgeEffect(view.context) {
                override fun onPull(deltaDistance: Float) {
                    super.onPull(deltaDistance)
                    handlePull(deltaDistance)
                }

                override fun onPull(deltaDistance: Float, displacement: Float) {
                    super.onPull(deltaDistance, displacement)
                    handlePull(deltaDistance)
                }

                private fun handlePull(deltaDistance: Float) {
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationYDelta = sign * view.height *  deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    view.forEachVisibleHolder { holder: TestVH ->
                        holder.translationY.cancel()
                        holder.itemView.translationY += translationYDelta

                    }
                }

                override fun onRelease() {
                    super.onRelease()
                    view.forEachVisibleHolder { holder: TestVH ->
                        holder.translationY.start()
                    }
                }

                override fun onAbsorb(velocity: Int) {
                    super.onAbsorb(velocity)
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                    view.forEachVisibleHolder { holder: TestVH ->
                        holder.translationY.setStartVelocity(translationVelocity).start()
                    }
                }
            }
        }
    }
}