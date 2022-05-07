package com.cmk.app.viewmodel

import com.cmk.app.base.BaseViewModel
import com.cmk.app.net.Http
import com.cmk.app.net.api
import com.cmk.app.net.onCatch
import com.cmk.app.net.onCollect
import com.cmk.app.repository.WxRepository
import com.cmk.app.vo.WxTabVo
import kotlinx.coroutines.flow.*

/**
 * @Author: romens
 * @Date: 2019-11-8 9:54
 * @Desc:
 */
class WxViewModel : BaseViewModel() {

    val repository by lazy { WxRepository() }
    private val _wxTabState = MutableStateFlow<List<WxTabVo>>(emptyList())
    val wxTabState = _wxTabState.asStateFlow()

    fun loadWxTab() {
        launch {
            flow { emit(Http.service.wxTab()) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    success { _wxTabState.value = it }
                }
        }
    }

    fun loadWxList(id: Int) = repository.getWxList(id)
}