package com.cmk.app.util

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

/**
 * 倒计时
 */
interface CountDownListener {
    fun onStart()
    fun onTick(count: Int)
    fun onFinish()
}

class CountDownAction : CountDownListener {
    private var onStart: (() -> Unit)? = null
    private var onTick: ((count: Int) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null

    fun start(func: () -> Unit) {
        this.onStart = func
    }

    fun tick(func: ((count: Int) -> Unit)) {
        this.onTick = func
    }

    fun finish(func: () -> Unit) {
        this.onFinish = func
    }

    override fun onStart() {
        onStart?.invoke()
    }

    override fun onTick(count: Int) {
        onTick?.invoke(count)
    }

    override fun onFinish() {
        onFinish?.invoke()
    }
}

/**
 * [delay]默认是1s间隔
 * 返回job用于取消
 */
fun CoroutineScope.countDown(
    start: Int,
    end: Int,
    delay: Long? = 1000,
    listener: CountDownAction.() -> Unit
): Job {
    val action = CountDownAction().apply(listener)
    return launch {
        flow {
            if (start < end) {
                for (index in start..end) {
                    emit(index)
                }
            } else {
                for (index in start downTo end) {
                    emit(index)
                }
            }
        }
            .onEach { delay(delay!!) }
            .onStart { action.onStart() }
            .catch { Log.e("countDown-->", "$it") }
            .onCompletion { action.onFinish() }
            .collect { action.onTick(it) }
    }
}

/**
 * 超时等待任务
 * [dispatcher] 调度器
 * [outTimeMillis]超时时间
 * [onTimeBlock] 超时内的任务
 * [outTimeBlock]超时后执行的任务
 */
fun CoroutineScope.timeOutWait(
    dispatcher: CoroutineContext,
    outTimeMillis: Long,
    onTimeBlock: suspend () -> Unit,
    outTimeBlock: suspend () -> Unit
) {
    this.launch(dispatcher) {
        val result = withTimeoutOrNull(outTimeMillis) {
            onTimeBlock.invoke()
        }
        if (result == null) {
            outTimeBlock.invoke()
        }
    }
}
