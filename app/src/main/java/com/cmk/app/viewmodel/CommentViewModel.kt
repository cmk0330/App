package com.cmk.app.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.cmk.app.base.BaseViewModel

class CommentViewModel : BaseViewModel() {

    val editModelLiveData: MutableLiveData<EditModel> = MutableLiveData()
    val commentLiveData: MutableLiveData<List<String>> = MutableLiveData()

    fun loadComment() {
        val list: MutableList<String> = ArrayList()
        for (index in 0..30) {
            list.add("这是第 $index 条评论")
        }
        commentLiveData.value = list
    }

    fun editModel(state: Boolean) {
        editModelLiveData.value = EditModel(state = state)
    }

    private fun emitUi(state: Boolean = false) {
        editModelLiveData.value = EditModel(state = state)
    }

    data class EditModel(var state: Boolean = false) {
        var visibility: Int = View.GONE

        init {
            visibility = if (state) View.VISIBLE else View.GONE
        }
    }
}