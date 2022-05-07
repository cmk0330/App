package com.cmk.app.net

import com.cmk.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * @Author: romens
 * @Date: 2019-11-5 15:29
 * @Desc:
 */
class OkHttpConfig {

    companion object{
        private const val TIME_OUT = 30
    }

    private val okhttpClient: OkHttpClient
    get() {
        val builder = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            logging.level = HttpLoggingInterceptor.Level.BODY
        } else {
            logging.level = HttpLoggingInterceptor.Level.BASIC
        }

        builder.addInterceptor(logging)
            .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)

//        handleBuilder(builder)

        return builder.build()
    }
}