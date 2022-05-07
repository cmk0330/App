package com.cmk.app.test

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * @Author: romens
 * @Date: 2019-12-23 14:57
 * @Desc:
 */
object RetrofitUtil {
    fun get(): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS) //允许失败重试
            .retryOnConnectionFailure(true)
            .build()

        val retrofit =
            Retrofit.Builder() //设置基站地址(基站地址+描述网络请求的接口上面注释的Post地址,就是要上传文件到服务器的地址,
                // 这只是一种设置地址的方法,还有其他方式,不在赘述)
                .baseUrl("https://dldir1.qq.com/weixin/") //设置委托,使用OKHttp联网,也可以设置其他的;
                .client(okHttpClient) //设置数据解析器,如果没有这个类需要添加依赖:
                .addConverterFactory(GsonConverterFactory.create()) //设置支持rxJava
                // .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit
    }
}