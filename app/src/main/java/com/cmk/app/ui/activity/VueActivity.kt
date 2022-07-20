package com.cmk.app.ui.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityVueBinding
import com.cmk.app.util.AndroidFunction

//https://blog.csdn.net/warmandfull/article/details/106728944
class VueActivity : BaseActivity() {

    private lateinit var binding: ActivityVueBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVueBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        settings.setAllowUniversalAccessFromFileURLs(true)
        val androidFunction = AndroidFunction(this)
        binding.webView.addJavascriptInterface(androidFunction, androidFunction.toString())
//        binding.webView.loadUrl("file:///android_asset/index.html")

        binding.webView.loadUrl("http://localhost:9090/index.html")
        binding.button.setOnClickListener {
            binding.webView.loadUrl("javascript:setRed()")
        }
        binding.button1.setOnClickListener {
            binding.webView.loadUrl("javascript:setColor('#00f','这是android 原生调用JS代码的触发事件')")
        }

        Log.e("onCreate宽度-->","${binding.button.width}")

        binding.button.postDelayed(Runnable {
            Log.e("thread宽度-->","${binding.button.width}")
        }, 500)


    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume宽度-->","${binding.button.width}")
    }

    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @param cookie 要同步的cookie
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    fun syncCookie(url: String?, cookie: String?): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(this)
        }
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setCookie(url, cookie) //如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        val newCookie: String = cookieManager.getCookie(url)
        return !TextUtils.isEmpty(newCookie)
    }
}