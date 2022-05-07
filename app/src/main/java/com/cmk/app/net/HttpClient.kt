package com.cmk.app.net

import com.cmk.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Author: romens
 * @Date: 2019-11-14 13:32
 * @Desc:
 */
abstract class HttpClient {

    companion object {
        private const val TIMEOUT: Long = 10
    }

    private val okHttpClient: OkHttpClient
        get() {
            val builder = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG)
                logging.level = HttpLoggingInterceptor.Level.BODY
            else
                logging.level = HttpLoggingInterceptor.Level.BASIC
            builder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
            okHttpClientConfig(builder)
            return builder.build()
        }

    fun <T> createService(t: Class<T>, baseUrl: String): T {
        val build = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        return build.create(t)
    }

    private val retrofit: Retrofit
        get() {
            val builder = Retrofit.Builder()
            retrofitConfig(builder)
            builder
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
            return builder.build()
        }

    /**
     *
     */
    open fun okHttpClientConfig(builder: OkHttpClient.Builder) {}

    open fun retrofitConfig(builder: Retrofit.Builder) {}
}