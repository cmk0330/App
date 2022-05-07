package com.cmk.app.test

import android.view.View

sealed class UIOperation {
    object Show : UIOperation()
    object Hide : UIOperation()
    class TranslateX(val x: Float) : UIOperation()
    class TranslateY(val y: Float) : UIOperation()

    fun execute(view: View, op: UIOperation) = when (op) {
        is Show -> view.visibility = View.VISIBLE
        is Hide -> view.visibility = View.GONE
        is TranslateX -> view.translationX = op.x
        is TranslateY -> view.translationY = op.y
    }
}