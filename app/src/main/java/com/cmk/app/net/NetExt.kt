package com.cmk.app.net

import android.util.Log
import com.cmk.app.base.App
import com.cmk.app.ext.toast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 拒绝过度封装，抛弃花里胡哨，坚守kiss原则
 */

/*------------------------------------------------------------------------------------------------*/

const val DEFAULT_UTL = ""
const val TOOL_URL = "http://www.wanandroid.com/tools"
const val GITHUB_PAGE = "https://github.com/lulululbj/wanandroid"
const val ISSUE_URL = "https://github.com/lulululbj/wanandroid/issues"
const val ERROR = "出了点问题"


/**
 * 图片上传
 */
suspend inline fun <T : Any> CoroutineScope.upLoad(
    response: ApiResponse<T>,
    crossinline resultAction: ResultAction<T>.() -> Unit
): Result<T> {
    val action = ResultAction<T>().apply(resultAction)
    val result = try {
        if (response.ret == 200) {
            Result.Success(response.data)
        } else {
            Result.Error(response.ret, response.msg)
        }
    } catch (e: Exception) {
        Result.Failure(e)
    }

    when (result) {
        is Result.Success -> action.onSuccess(result.data as T)
        is Result.Failure -> action.onFailure(result.exception)
        is Result.Error -> action.onError(result.code, result.msg)
    }

    return result
}

/**
 * 启动页图片专用
 */
suspend inline fun <T : Any> CoroutineScope.splashApi(
    response: ApiResponse<T>,
    crossinline resultAction: ResultAction<T>.() -> Unit
): Result<T> {
    val action = ResultAction<T>().apply(resultAction)
    val result = try {
        Result.Success(response.data)
    } catch (e: Exception) {
        Result.Failure(e)
    }

    when (result) {
        is Result.Success -> action.onSuccess(result.data as T)
        is Result.Failure -> action.onFailure(result.exception)
        is Result.Error -> action.onError(result.code, result.msg)
    }

    return result
}

/**
 * Channel 用于协程之间的通信，
 * 2020-7-20 , retrofit会自动切换线程，不需要withcontext切换了
 * 2020-7-27, Result的意义何在？[apiOld]
 */

inline fun <T> CoroutineScope.api(
    responseBlock: () -> ApiResponse<T>,
    resultAction: ResultAction<T>.() -> Unit
) {
    val action = ResultAction<T>().apply(resultAction)
    try {
        val response = responseBlock()
        Log.e("response:", response.toString())
        if (response.errorCode == 0)
            action.onSuccess(response.data)
        else
            action.onError(response.errorCode, response.errorMsg)
    } catch (e: Exception) {
        action.onFailure(e)
    }
}

inline fun <T> CoroutineScope.api1(
    responseBlock: () -> ApiResponse<T>,
    resultAction: ResultAction<T>.() -> Unit
) {
    val action = ResultAction<T>().apply(resultAction)
    try {
        val response = Response.create(responseBlock())
        when (response) {
            is Response.ApiSuccessResponse ->
                action.onSuccess(response.data)
            is Response.ApiErrorResponse ->
                action.onError(response.errorCode, response.errorMsg)
            else -> action.onFailure(Throwable("错误--->"))
        }

    } catch (e: Exception) {
        action.onFailure(e)
    }
}

/**
 * 2020-05-14
 * 期待的调用格式：
 *  apiRequest(Http.service.bannerList()) {
 *      success {}
 *      failure {}
 *      error{}
 *  }
 *  但是此方式由于请求部分Http.service.bannerList()需要放在io线程中
 *  要在launch{}中调用withContext(Dispatchers.IO){}再执行,不够简洁。

 *  目前调用格式：
 *  apiRequest({ Http.service.bannerList() }) {
 *      success {}
 *      failure {}
 *      error{}
 *  }
 *  把网路请求放在{}中
 *
 *  2020-05-20,已完成期望的方式，[api]
 */
suspend inline fun <T : Any> CoroutineScope.apiRequest(
    crossinline responseBlock: suspend () -> ApiResponse<T>,
    crossinline resultAction: ResultAction<T>.() -> Unit
): Result<T> {
    Log.e("请求开始线程", "${Thread.currentThread()}")
    val action = ResultAction<T>().apply(resultAction)
    val result = withContext(Dispatchers.IO) {
        Log.e("网络请求线程", "${Thread.currentThread()}")

        try {
            val response = responseBlock()
            if (response.errorCode == 0) {
                Result.Success(response.data)
            } else {
                Result.Error(response.errorCode, response.errorMsg)
            }
        } catch (e: Throwable) {
            Result.Failure(e)
        }
    }

    when (result) {
        is Result.Success -> action.onSuccess(result.data as T)
        is Result.Failure -> action.onFailure(result.exception)
        is Result.Error -> action.onError(result.code, result.msg)
    }

    return result
}

/**
 * 待测试和[apiRequest]哪个性能更好
 * Channel 用于协程之间的通信，
 * 2020-7-20 , retrofit会自动切换线程，不需要withcontext切换了
 */

inline fun <T : Any> CoroutineScope.apiOld(
    response: ApiResponse<T>,
    crossinline resultAction: ResultAction<T>.() -> Unit
): Result<T> {
    val action = ResultAction<T>().apply(resultAction)
    val result = try {
        if (response.errorCode == 0) {
            Result.Success(response.data)
        } else {
            Result.Error(response.errorCode, response.errorMsg)
        }
    } catch (e: Exception) {
        Result.Failure(e)
    }

    when (result) {
        is Result.Failure -> action.onFailure(result.exception)
        is Result.Error -> action.onError(result.code, result.msg)
    }

    return result
}

/**
 * @param errorMsg 设置errorMsg是为了在服务器返回数据为null时，防止报空，执行{Result.Failure(e)}
 * 在服务器返回不为null时，无需手动添加errorMsg，出现错误自动执行(Result.Failure(Exception(errorMsg, e)))
 */
suspend fun <T : Any> apiCall(
    call: suspend () -> Result<T>,
    errorMsg: String? = null
): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        if (errorMsg.isNullOrBlank()) {
            Result.Failure(Exception(errorMsg, e))
        } else {
            Result.Failure(e)
        }
    }
}

/**
 * @param failureBlock 尽量不要使用，错误处理交给apiCall()
 */
suspend fun <T : Any> executeResponse(
    response: ApiResponse<T>,
    successBlock: (suspend CoroutineScope.() -> Unit)? = null,
    failureBlock: (suspend CoroutineScope.() -> Unit)? = null
): Result<T> {
    return coroutineScope {
        if (response.errorCode == 0) {
            successBlock?.let { it() }
            Result.Success(response.data)
        } else {
            failureBlock?.let { it() }
            Result.Failure(Exception(response.errorMsg))
        }
    }
}

/**
 * 网络请求异常扩展函数，自定义针对各种异常自行处理
 * 如：onCatch() {
 *          _viewStates.setState { copy(pageStatus = PageStatus.Error(it)) }
 *    }
 */
fun <T> Flow<T>.onCatch(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T> {
    return this.catch { action(it) }
}

/**
 * 网络请求异常扩展函数，默认处理方式
 */
fun <T> Flow<T>.onCatch(): Flow<T> {
    return this.catch {
        if (it is UnknownHostException || it is SocketTimeoutException) {
            App.get().toast("发生网络错误，请稍后重试")
        } else {
            App.get().toast("请求失败，请重试")
            Log.e("请求失败--${it.cause}", "${it.message}")
            println("请求失败${it.cause}:${it.message}")
        }
    }
}

suspend fun <T> Flow<ApiResponse<T>>.onCollect(
    resultAction: ResultAction<T>.() -> Unit
) {
    return this.collect {
        val action = ResultAction<T>().apply(resultAction)
        when (val response = Response.create(it)) {
            is Response.ApiSuccessResponse ->
                action.onSuccess(response.data)
            is Response.ApiErrorResponse ->
                action.onError(response.errorCode, response.errorMsg)
            is Response.ApiEmptyResponse ->
                action.onEmpty("data is null")
            else -> {}
        }
    }
}
