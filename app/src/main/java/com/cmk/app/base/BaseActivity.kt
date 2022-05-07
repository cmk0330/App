package com.cmk.app.base

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.cmk.app.util.lifecycle.ActivityStackManager
import com.cmk.app.viewmodel.ThemeViewModel
import com.githang.statusbar.StatusBarCompat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest


/**
 * @Author: romens
 * @Date: 2019-11-11 16:57
 * @Desc:
 */
open class BaseActivity : AppCompatActivity() {

    val themeViewModel by viewModels<ThemeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { // 适配手机刷新率
            // 获取系统window支持的模式
            val modes = window.windowManager.defaultDisplay.supportedModes
            // 对获取的模式，基于刷新率的大小进行排序，从小到大排序
            modes.sortBy {
                it.refreshRate
            }
            window.let {
                val lp = it.attributes
                // 取出最大的那一个刷新率，直接设置给window
                lp.preferredDisplayModeId = modes.last().modeId
                it.attributes = lp
            }
        }
        super.onCreate(savedInstanceState)
        val currentActivity = ActivityStackManager.currentActivity
        //关键代码,沉浸
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //设置专栏栏和导航栏的底色，透明
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }
//        setNativeLightStatusBar()
        ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
            ?.let { controller ->
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }

//        StatusBarCompat.setStatusBarColor(currentActivity, 0)
//        setAndroidNativeLightStatusBar(currentActivity, true)
//        StatusBarUtil.setTranslucentForImageViewInFragment(this, 0, null)
//        window.decorView.fitsSystemWindows = true
//        themeViewModel.currentTheme.observe(this) {
//            delegate.localNightMode = it.mode
//        }

        lifecycleScope.launchWhenCreated {
            themeViewModel.themeState.collectLatest { delegate.localNightMode = it.mode }
        }

        lifecycleScope.launchWhenCreated {
            subscribe()
        }
    }

    open suspend fun subscribe() {}

    /**
     * 设置沉浸后专栏栏和导航字体的颜色
     * true 黑色,false白色
     */
    fun setNativeLightStatusBar() {
        ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
            ?.let { controller ->
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
    }

    /**
     * 关闭状态栏
     */
    fun closeStatusBar() {
        ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
            ?.hide(WindowInsetsCompat.Type.statusBars())
    }

    /**
     * 全屏模式
     */
    fun Activity.hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * 推出全屏
     */
    fun Activity.showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        ViewCompat.getWindowInsetsController(findViewById<FrameLayout>(android.R.id.content))
            ?.show(WindowInsetsCompat.Type.systemBars())
    }

    /**
     * 获取状态栏高度
     */
    private val FragmentActivity.windowInsetsCompat: WindowInsetsCompat?
        get() = ViewCompat.getRootWindowInsets(findViewById<FrameLayout>(android.R.id.content))

    fun FragmentActivity.getStatusBarsHeight(): Int {
        val windowInsetsCompat = windowInsetsCompat ?: return 0
        return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
    }

    /**
     * 获取导航栏高度
     */
    fun FragmentActivity.getNavigationBarsHeight(): Int {
        val windowInsetsCompat = windowInsetsCompat ?: return 0
        return windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
    }

//    class DataBinding<DB : ViewDataBinding>(val owner: Activity, val layoutId: Int) {
//        val binding: DB by lazy { DataBindingUtil.setContentView<DB>(owner, layoutId) }
//
//    }
}

//@MainThread
//inline fun <reified DB: ViewDataBinding> FragmentActivity.dataBinding1(@LayoutRes layoutId: Int) : Lazy<DB> =
//    lazy { DataBindingUtil.setContentView<DB>(this, layoutId) }
//
///**
// * 用于延迟加载databinding
// */
//@MainThread
//inline fun <reified DB : ViewDataBinding> FragmentActivity.dataBinding(@LayoutRes layoutId: Int): Lazy<DB> {
//    val binding = DataBindingUtil.setContentView<DB>(this, layoutId)
//    binding.lifecycleOwner = this
//    return DatabindingLazy(this, layoutId)
//}
//
///**
// * 延迟初始化
// */
//class DatabindingLazy<DB : ViewDataBinding>(
//    private val activity: FragmentActivity,
//    @LayoutRes private val layoutId: Int
//) : Lazy<DB> {
//    private var cached: DB? = null
//    override val value: DB
//        get() {
//            val dataBinding = cached
//            return dataBinding
//                ?: DataBindingUtil.setContentView<DB>(activity, layoutId).also {
//                    cached = it
//                }
//        }
//
//    override fun isInitialized(): Boolean = cached != null
//}