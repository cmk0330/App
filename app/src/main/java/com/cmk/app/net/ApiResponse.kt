package com.cmk.app.net

/**
 * @Author: romens
 * @Date: 2019-11-1 16:04
 * @Desc:
 */
data class ApiResponse<T>(
    val errorCode: Int = -1,
    val errorMsg: String,
    val data: T,
    val ret: Int,
    val msg: String
)

sealed class Response<out T> {
    companion object {

        fun <T> create(response: ApiResponse<T>): Response<T> {
            return if (response.errorCode == 0 && response.data != null) {
                ApiSuccessResponse(response.data)
            } else if (response.errorCode == -1) {
                ApiErrorResponse(response.errorCode, response.errorMsg)
            } else {
                ApiEmptyResponse("data is null")
            }
        }
    }

    data class ApiEmptyResponse<T>(val msg: String) : Response<T>()

    data class ApiSuccessResponse<T>(val data: T) : Response<T>()

    data class ApiErrorResponse<T>(val errorCode: Int, val errorMsg: String) : Response<T>()

    data class ApiFailureResponse<T>(val throwable: Throwable) : Response<T>()
}

