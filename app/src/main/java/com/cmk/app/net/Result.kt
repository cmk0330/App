package com.cmk.app.net

/**
 * @Author: romens
 * @Date: 2019-12-4 15:48
 * @Desc:
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: Any?) : Result<T>()
    data class Failure(val exception: Throwable) : Result<Nothing>()
    data class Error(val code: Int, val msg: String) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Failure[exception=${exception.message}]"
            is Error -> "Nothing[msg=${msg}]"
        }
    }
}

inline fun <reified T : Any> Result<T>.doSuccess(callback: (value: T) -> Unit) {
    if (this is Result.Success) callback(data as T)
}

inline fun <reified T : Any> Result<T>.doFailure(callback: (exception: Throwable?) -> Unit) {
    if (this is Result.Failure) callback(exception)
}

inline fun <reified T : Any> Result<T>.doError(callback: (code: Int, msg: String?) -> Unit) {
    if (this is Result.Error) callback(code, msg)
}

sealed class Result1<T> {

    data class Success<T>(val data: ApiResponse<T>) : Result1<T>()
    data class Failure(val exception: Throwable) : Result1<Nothing>()
    data class Error(val code: Int, val msg: String) : Result1<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Failure -> "Failure[exception=${exception.message}]"
            is Error -> "Nothing[msg=${msg}]"
        }
    }
}



