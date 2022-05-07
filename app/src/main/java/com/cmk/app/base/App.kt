package com.cmk.app.base

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import com.cmk.app.di.appModule
import com.cmk.app.util.Preference
import com.cmk.app.util.lifecycle.AppLifeObserver
import com.cmk.app.util.lifecycle.KtxLifeCycleCallBack
import com.cmk.app.vo.LoginVo
import com.google.gson.Gson
import dagger.hilt.android.HiltAndroidApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import kotlin.properties.Delegates

/**
 * @Author: romens
 * @Date: 2019-11-14 13:26
 * @Desc:
 */
@HiltAndroidApp
class App : Application() {

    //    private lateinit var tinkerApplicationLike: ApplicationLike
    companion object {
        const val MI_APP_ID = "2882303761518598908"
        const val MI_APP_KEY = "5731859815908"
        var isLogin by Preference(Preference.IS_LOGIN, false)
        var userGson by Preference(Preference.USER_GSON, "")
        var context: Context by Delegates.notNull()
        var application: Application by Delegates.notNull()
        lateinit var CURRENT_USER: LoginVo
        lateinit var instance: App
        fun get(): App {
            return instance
        }
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
//        MultiDex.install(base)
//        Beta.installTinker()
    }

    override fun onCreate() {
        super.onCreate()

        context = applicationContext
        application = this
        instance = this
        registerActivityLifecycleCallbacks(KtxLifeCycleCallBack())
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifeObserver())
//        Bugly.init(this, "81f3a06a33", true)
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
//        initMiPush()
//        initTinkerPatch()
//        initTpns()

        Log.e("app-isLogin:", "$isLogin")

        if (isLogin) CURRENT_USER = Gson().fromJson(userGson, LoginVo::class.java)

    }

    /**
     * tpns
     */
//    private fun initTpns() {
//        XGPushConfig.enableDebug(this, true);
//        XGPushManager.registerPush(this, object : XGIOperateCallback {
//            override fun onSuccess(p0: Any?, p1: Int) {
//                //token在设备卸载重装的时候有可能会变
//                Log.d("TPush", "注册成功，设备token为：$p0");
//                val userBean = Gson().fromJson<LoginVo>(userGson, LoginVo::class.java)
//            }
//
//            override fun onFail(p0: Any?, p1: Int, p2: String?) {
//                Log.d("TPush", "注册失败，错误码：$p1,错误信息：$p2");
//            }
//        })
//        XGPushManager.bindAccount(this, "app_tpns")
//
//    }

    /**
     * 我们需要确保至少对主进程跟patch进程初始化 TinkerPatch
     */
    private fun initTinkerPatch() {
//        // 我们可以从这里获得Tinker加载过程的信息
//        if (BuildConfig.TINKER_ENABLE) {
//            tinkerApplicationLike =
//                TinkerPatchApplicationLike.getTinkerPatchApplicationLike()
//            // 初始化TinkerPatch SDK
//            TinkerPatch.init(
////                tinkerApplicationLike,
//                TinkerPatch.Builder(tinkerApplicationLike)
//                    .requestLoader(OkHttp3Loader())
//                    .build()
//            )
//                .reflectPatchLibrary()
//                .setPatchRollbackOnScreenOff(true)
//                .setPatchRestartOnSrceenOff(true)
//                .setFetchPatchIntervalByHours(3)
//            // 获取当前的补丁版本
//            Log.d("App-ktx:", "Current patch version is " + TinkerPatch.with().patchVersion)
//
//            // fetchPatchUpdateAndPollWithInterval 与 fetchPatchUpdate(false)
//            // 不同的是，会通过handler的方式去轮询
//            TinkerPatch.with().fetchPatchUpdateAndPollWithInterval()
//        }
    }

    /**
     * 在这里给出TinkerPatch的所有接口解释
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private fun useSample() {
//        TinkerPatch.init(tinkerApplicationLike) //是否自动反射Library路径,无须手动加载补丁中的So文件
//            //注意,调用在反射接口之后才能生效,你也可以使用Tinker的方式加载Library
//            .reflectPatchLibrary() //向后台获取是否有补丁包更新,默认的访问间隔为3个小时
//            //若参数为true,即每次调用都会真正的访问后台配置
//            .fetchPatchUpdate(false) //设置访问后台补丁包更新配置的时间间隔,默认为3个小时
//            .setFetchPatchIntervalByHours(3) //向后台获得动态配置,默认的访问间隔为3个小时
//            //若参数为true,即每次调用都会真正的访问后台配置
//            .fetchDynamicConfig(object : ConfigRequestCallback {
//                override fun onSuccess(hashMap: HashMap<String, String>) {}
//                override fun onFail(e: Exception) {}
//            }, false) //设置访问后台动态配置的时间间隔,默认为3个小时
//            .setFetchDynamicConfigIntervalByHours(3) //设置当前渠道号,对于某些渠道我们可能会想屏蔽补丁功能
//            //设置渠道后,我们就可以使用后台的条件控制渠道更新
//            .setAppChannel("default") //屏蔽部分渠道的补丁功能
//            .addIgnoreAppChannel("googleplay") //设置tinkerpatch平台的条件下发参数
//            .setPatchCondition("test", "1") //设置补丁合成成功后,锁屏重启程序
//            //默认是等应用自然重启
//            .setPatchRestartOnSrceenOff(true) //我们可以通过ResultCallBack设置对合成后的回调
//            //例如弹框什么
//            //注意，setPatchResultCallback 的回调是运行在 intentService 的线程中
//            .setPatchResultCallback(ResultCallBack {
//                Log.i("App-ktx:", "onPatchResult callback here")
//            }) //设置收到后台回退要求时,锁屏清除补丁
//            //默认是等主进程重启时自动清除
//            .setPatchRollbackOnScreenOff(true) //我们可以通过RollbackCallBack设置对回退时的回调
//            .setPatchRollBackCallback(RollbackCallBack {
//                Log.i("App-ktx:", "onPatchRollback callback here")
//            })
    }

    /**
     * 自定义Tinker类的高级用法, 使用更灵活，但是需要对tinker有更进一步的了解
     * 更详细的解释请参考:http://tinkerpatch.com/Docs/api
     */
    private fun complexSample() {
//        //修改tinker的构造函数,自定义类
//        val builder = TinkerPatch.Builder(tinkerApplicationLike)
//            .listener(DefaultPatchListener(this))
//            .loadReporter(DefaultLoadReporter(this))
//            .patchReporter(DefaultPatchReporter(this))
//            .resultServiceClass(TinkerServerResultService::class.java)
//            .upgradePatch(UpgradePatch())
//            .patchRequestCallback(TinkerPatchRequestCallback())
//        //.requestLoader(new OkHttpLoader());
//        TinkerPatch.init(builder.build())
    }
}