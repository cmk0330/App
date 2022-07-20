package com.cmk.app.util

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.widget.Toast

class AndroidFunction(val context: Context) {

    //H5调用方法：javascript:button.click0()
    @JavascriptInterface
    fun click0() {
        show("title", "")
        Log.e("AndroidFunction", "点击了click0()")
    }

    @JavascriptInterface
    fun click0(data1: String, data2: String) {
        show(data1, data2)
    }

    private fun show(title: String, data: String) {
        Toast.makeText(context, title + data, Toast.LENGTH_SHORT).show()
    }

    @JavascriptInterface //必须添加，这样才可以标志这个类的名称是 button
    override fun toString(): String {
        return "button"
    }
}