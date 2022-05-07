package com.cmk.app

import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityMainBinding
import com.cmk.app.test.KotilnClassDemo
import com.cmk.app.test.TestBean
import com.cmk.app.ui.activity.LoginActivity
import com.cmk.app.ui.fragment.HomeFragment
import com.cmk.app.ui.fragment.MineFragment
import com.cmk.app.ui.fragment.PlazaFragment
import com.cmk.app.ui.fragment.WxFragment
import com.cmk.app.util.Preference
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*

import retrofit2.Call

import retrofit2.SkipCallbackExecutor
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import okhttp3.ResponseBody
import org.koin.androidx.scope.lifecycleScope


@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private val fragments: MutableList<Fragment> = ArrayList()
    private val isLogin by Preference(Preference.IS_LOGIN, false)
    private lateinit var binding: ActivityMainBinding

    /**
     * 沉浸式
     */
//    override fun onWindowFocusChanged(hasFocus: Boolean) {
//        super.onWindowFocusChanged(hasFocus)
//        val decorView = window.decorView
//        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
//        Debug.startMethodTracing("App")
//        StatusBarCompat.setStatusBarColor(this, resources.getColor(R.color.white))

        val homeFragment = HomeFragment()
        val plazaFragment = PlazaFragment()
        val wxFragment = WxFragment()
        val mineFragment = MineFragment()
        fragments.apply {
            add(homeFragment)
            add(plazaFragment)
            add(wxFragment)
            add(mineFragment)
        }
        val tabs = arrayOf("首页", "广场", "公众号", "我")
        val transaction = supportFragmentManager.beginTransaction()
        for (index in fragments.indices) {
            transaction.apply {
                add(R.id.fl_content, fragments[index], tabs[index])
                hide(fragments[index])
            }
        }
        transaction.show(fragments[0]).commit()
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.tab_home -> switchFragmentState("首页")
                R.id.tab_plaza -> switchFragmentState("广场")
                R.id.tab_wx -> switchFragmentState("公众号")
                R.id.tab_mine -> {
                    if (isLogin) switchFragmentState("我")
                    else {
                        startActivity(Intent(this, LoginActivity::class.java))
                        switchFragmentState("我")
                    }
                }
            }
            true
        }

        Debug.stopMethodTracing()
    }

    override fun onResume() {
        super.onResume()
        val kotilnClassDemo = KotilnClassDemo("str1", "str2")
    }

    private fun switchFragmentState(tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        fragments.forEach {
            if (!it.tag.equals(tag)) transaction.hide(it) else transaction.show(it)
        }
        transaction.commit()
    }

    /**
     * https://juejin.im/post/5d9e85ae51882509751b172a
     *
     * run
     * 函数体内使用this代替本对象。返回值为函数最后一行或者return指定的表达式
     * let
     * 函数内使用it代替本对象。返回值为函数最后一行或者return指定的表达式。
     * apply
     * 函数内使用this代替本对象。返回值为本对象。
     * also
     * 函数内使用it代替本对象。返回值为本对象。
     * takeIf
     * 条件为真返回对象本身否则返回null。
     * takeUnless
     * 条件为真返回null否则返回对象本身。
     * with
     * with比较特殊，不是以扩展方法的形式存在的，而是一个顶级函数。传入参数为对象，函数内使用this代替对象。返回值为函数最后一行或者return指定的表达式。
     * repeat 将函数体执行多次。
     *
     * run内部的this->com.cmk.app.test.TestBean@81c3c7b    run返回值->24
     * let内部的it->com.cmk.app.test.TestBean@81c3c7b   let返回的->25
     * apply内部的this->com.cmk.app.test.TestBean@81c3c7b   apply返回值->com.cmk.app.test.TestBean@81c3c7b
     * also内部的it->com.cmk.app.test.TestBean@81c3c7b   also返回的->com.cmk.app.test.TestBean@81c3c7b
     * with内部的this->com.cmk.app.test.TestBean@81c3c7b   with返回值->28
     * takeIf返回值->com.cmk.app.test.TestBean@81c3c7b
     * takeUnless返回值->null
     */
    private fun testFun() {
        val test = TestBean("", 1)

        val runResult = test.run {
            this.name = "郭芙蓉"
            this.age = 24
            println("run内部的this->$this")
            this.age
        }
        println("run返回值->$runResult")

        val letResult = test.let {
            it.name = "郭芙蓉"
            it.age = 25
            println("let内部的it->$it")
            it.age
        }
        println("let返回的->$letResult")

        val applyResult = test.apply {
            this.name = "郭芙蓉"
            this.age = 26
            println("apply内部的this->$this")
            this.age
        }
        println("apply返回值->$applyResult")

        val alsoResult = test.also {
            it.name = "郭芙蓉"
            it.age = 27
            println("also内部的it->$it")
            it.age
        }
        println("also返回的->$alsoResult")

        val withResult = with(test) {
            this.name = "郭芙蓉"
            this.age = 28
            println("with内部的this->$this")
            this.age
        }
        println("with返回值->$withResult")

        test.age = 36
        val takeIfResult = test.takeIf {
            it.age > 24
        }
        println("takeIf返回值->$takeIfResult")

        val takeUnlessResult = test.takeUnless {
            it.age > 24
        }
        println("takeUnless返回值->$takeUnlessResult")
    }
}
