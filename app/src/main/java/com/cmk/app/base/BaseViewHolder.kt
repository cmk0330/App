package com.cmk.app.base

import android.widget.EdgeEffect
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView
import com.cmk.app.ui.adapter.OnViewClickListener
import com.cmk.app.ui.adapter.test.TestAdapter
import com.cmk.app.widget.recyclerview.FLING_TRANSLATION_MAGNITUDE
import com.cmk.app.widget.recyclerview.OVERSCROLL_TRANSLATION_MAGNITUDE
import com.cmk.app.widget.recyclerview.forEachVisibleHolder

/**
 * @Author: romens
 * @Date: 2019-12-2 16:15
 * @Desc:
 */
class BaseViewHolder<T>(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    private var onViewClickListener1: OnViewClickListener<T>? = null
    var onViewClickListener: ((T) -> Unit)? = null

    /**
     * 用闭包跳转可以省略OnItemClickListener接口
     */
    fun bind(data: T?, listener: ((T) -> Unit)?) {
        binding.setVariable(BR.data, data)
        binding.root.setOnClickListener { data?.let { listener?.invoke(it) } }
        binding.executePendingBindings()
    }

    fun bindData(data: T) {
        binding.setVariable(BR.data, data)
        binding.executePendingBindings()
    }

    fun bindItemViewClickListener(listener: ((T) -> Unit)?) {
        onViewClickListener = listener
    }

    fun bindItemViewClickListener(listener: OnViewClickListener<T>) {
        this.onViewClickListener1 = listener
    }

    val translationY: SpringAnimation = SpringAnimation(itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )

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
                    val translationYDelta =
                        sign * view.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                    view.forEachVisibleHolder { holder: TestAdapter.TestVH ->
                        holder.translationY.cancel()
                        holder.itemView.translationY += translationYDelta

                    }
                }

                override fun onRelease() {
                    super.onRelease()
                    view.forEachVisibleHolder { holder: BaseViewHolder<T> ->
                        holder.translationY.start()
                    }
                }

                override fun onAbsorb(velocity: Int) {
                    super.onAbsorb(velocity)
                    val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                    val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                    view.forEachVisibleHolder { holder: BaseViewHolder<T> ->
                        holder.translationY.setStartVelocity(translationVelocity).start()
                    }
                }
            }
        }
    }
}