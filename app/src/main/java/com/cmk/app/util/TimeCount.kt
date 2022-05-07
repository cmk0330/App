package com.cmk.app.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.CoroutineContext

/**
 * 倒计时
 */
interface CountDownListener {
    fun onProgress(count: Int): Int
    fun onFinish()
}

class CountDownAction : CountDownListener {
    private var onProgress: ((count: Int) -> Unit)? = null
    private var onFinish: (() -> Unit)? = null

    fun progress(func: ((count: Int) -> Unit)) {
        this.onProgress = func
    }

    fun finish(func: () -> Unit) {
        this.onFinish = func
    }

    override fun onProgress(count: Int): Int {
        onProgress?.invoke(count)
        return count
    }


    override fun onFinish() {
        onFinish?.invoke()
    }
}

/**
 * [delay]默认是1s间隔
 */
inline fun CoroutineScope.countDown(
    dispatcher: CoroutineContext,
    start: Int, end: Int, delay: Long? = 1000,
    listener: CountDownAction.() -> Unit
) {
    val action = CountDownAction().apply(listener)
    val out = flow<Int> {
        if (start < end) {
            for (index in start..end) {
                emit(index)
                delay?.let { kotlinx.coroutines.delay(it) }
            }
        } else {
            for (index in start downTo end) {
                emit(index)
                delay?.let { kotlinx.coroutines.delay(it) }
            }
        }
    }
    this.launch(dispatcher) {
        out.collect {
            action.onProgress(it)
        }
        action.onFinish()
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

object PrintlnTime {

    private var start: Long = 0
    private var end: Long = 0
    fun getStart(): Long {
        start = System.currentTimeMillis()
        println("TimeCount--startTime-->$start")
        return start
    }

    fun getEnd(): Long {
        end = System.currentTimeMillis()
        println("TimeCount--  endTime-->$end")
        return end
    }

    fun print(): Long {
        val t = end - start
        println("TimeCount--consumeTime-->$t")
        return t
    }
}