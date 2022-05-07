package com.cmk.app.widget.gallery.widget

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.cmk.app.R

class CheckRadioView : AppCompatImageView {
    private var mDrawable: Drawable? = null
    private var mSelectedColor = 0
    private var mUnSelectUdColor = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    private fun init() {
        mSelectedColor = ResourcesCompat.getColor(
            resources, R.color.main,
            context.theme
        )
        mUnSelectUdColor = ResourcesCompat.getColor(
            resources, R.color.check_def_gray,
            context.theme
        )
        setChecked(false)
    }

    fun setChecked(enable: Boolean) {
        if (enable) {
            setImageResource(R.drawable.ic_preview_radio_on)
            mDrawable = drawable
            mDrawable?.setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN)
        } else {
            setImageResource(R.drawable.ic_preview_radio_off)
            mDrawable = drawable
            mDrawable?.setColorFilter(mUnSelectUdColor, PorterDuff.Mode.SRC_IN)
        }
    }

    fun setColor(color: Int) {
        if (mDrawable == null) {
            mDrawable = drawable
        }
        mDrawable!!.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
}