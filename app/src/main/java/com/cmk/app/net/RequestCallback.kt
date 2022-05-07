package com.cmk.app.net

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

interface ResultCallback<T> {

    fun onSuccess(data: T)
    fun onFailure(e: Throwable)
    fun onError(errorCode: Int, errorMsg: String)
    fun onEmpty(msg: String)
}

class ResultAction<T> : ResultCallback<T> {
    private var onSuccess: ((data: T) -> Unit)? = null
    private var onFailure: ((e: Throwable) -> Unit)? = null
    private var onError: ((errorCode: Int, errorMsg: String) -> Unit)? = null
    private var onEmpty: ((msg: String) -> Unit)? = null

    fun success(func: (data: T) -> Unit) {
        this.onSuccess = func
    }

    fun failure(func: (e: Throwable) -> Unit) {
        this.onFailure = func
    }

    fun error(func: (errorCode: Int, errorMsg: String) -> Unit) {
        this.onError = func
    }

    fun empty(func: (msg: String) -> Unit) {
        this.onEmpty = func
    }

    override fun onSuccess(data: T) {
        this.onSuccess?.invoke(data)
    }

    override fun onFailure(e: Throwable) {
        this.onFailure?.invoke(e)
    }

    override fun onError(errorCode: Int, errorMsg: String) {
        this.onError?.invoke(errorCode, errorMsg)
    }

    override fun onEmpty(msg: String) {
        this.onEmpty?.invoke(msg)
    }
}