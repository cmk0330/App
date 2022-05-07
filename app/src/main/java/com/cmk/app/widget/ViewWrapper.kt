package com.cmk.app.widget

import android.view.View

/**
 * @Author: romens
 * @Date: 2019-12-25 9:08
 * @Desc:
 */
class ViewWrapper(val view:View) {

    fun setWidth(width:Int){
        view.layoutParams.width = width
    }

    fun getWidth() :Int{
        return view.layoutParams.width
    }
}