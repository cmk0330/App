package com.cmk.app.net

import com.cmk.app.base.App
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @Author: romens
 * @Date: 2019-11-1 16:55
 * @Desc:
 */
class OldHttp private constructor() {

    private val BASE_URL = ApiService.BASE_URL
    private val TIMEOUT: Long = 30
    private lateinit var mOkHttpClient: OkHttpClient
    private lateinit var mRetrofit: Retrofit
    private val cookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(), SharedPrefsCookiePersistor(
                App.context
            )
        )
    }

    init {
        initOkhttp()
        initRetrofit()
    }

    companion object {
        fun get(): OldHttp = SingletonHolder.INSTANCE
    }

    private object SingletonHolder {
        val INSTANCE: OldHttp = OldHttp()
    }

    private fun initRetrofit() {
        mRetrofit = Retrofit.Builder().baseUrl(BASE_URL)
//            .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(mOkHttpClient)
            .build()
    }

    private fun initOkhttp() {
        val builder = OkHttpClient.Builder()
        builder.readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .cookieJar(cookieJar)
//                .addNetworkInterceptor(new NetCacheInterceptor())
//                .addInterceptor(new NoNetCacheInterceptor());
//        val sslParams = SSLUtils.getSslSocketFactory()
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
        val loggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(loggingInterceptor)
        mOkHttpClient = builder.build()
    }

    fun <T> create(clazz: Class<T>): T = mRetrofit.create(clazz)

}