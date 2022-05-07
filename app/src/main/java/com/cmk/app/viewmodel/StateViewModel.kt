package com.cmk.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmk.app.net.Http
import com.cmk.app.net.api
import com.cmk.app.net.onCatch
import com.cmk.app.vo.ArticleVo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StateViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(ViewState())
    val viewState = _viewState.asStateFlow()
    private val _loading2: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading2 = _loading2.asStateFlow()

    fun changeState(state: Boolean) {
        _viewState.value = ViewState(!state)
    }

    fun changeState2() {
        _loading2.value = !_loading2.value
    }
}

class ViewState(
    val state: Boolean = false
)