package com.cmk.app.base

import com.cmk.app.net.ApiResponse
import com.cmk.app.net.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * @Author: romens
 * @Date: 2019-11-5 9:40
 * @Desc:
 */

open class Repository {

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
}

inline fun <reified R : Repository> repository(): RepositoryLazy<R> = RepositoryLazy(R::class)

class RepositoryLazy<R : Repository>(private val clazz: KClass<R>) : Lazy<R> {
    private var cached: R? = null
    override val value: R
        get() {
            val repository = cached
            return repository ?: clazz.primaryConstructor!!.call()
        }

    override fun isInitialized(): Boolean = cached != null
}


