package com.cmk.app.viewmodel

import androidx.lifecycle.viewModelScope
import com.cmk.app.base.BaseViewModel
import com.cmk.app.net.Http
import com.cmk.app.net.onCatch
import com.cmk.app.net.onCollect

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * @Author: romens
 * @Date: 2019-11-12 8:51
 * @Desc:
 * SharedFlow是不防抖的
 * stateFlow是不防抖的
 */
class WebViewModel : BaseViewModel() {
    private val _collectionState = MutableStateFlow(ViewState())
    val collectionState = _collectionState.asStateFlow()

    fun collect(id: Int) {
        launch {
            flow { emit(Http.service.collect(id)) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    empty {
                        _collectionState.value = ViewState(collect = true, id = 0)
                    }
                }
        }
    }

    fun unCollect(id: Int) {
        launch {
            flow { emit(Http.service.unCollect(id)) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    empty {
                        _collectionState.value = ViewState(collect = false, id = -1)
                    }
                }
        }
    }

    data class ViewState(val collect: Boolean = false, val id: Int = 0)
}