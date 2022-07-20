package com.cmk.app.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cmk.app.R
import com.cmk.app.net.Http
import com.cmk.app.util.countDown
import com.cmk.app.vo.ArticleVo
import kotlinx.android.synthetic.main.activity_test_coroutine.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @Author: romens
 * @Date: 2019-12-5 9:19
 * @Desc:
 */
class TestCoroutineActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_coroutine)
//        lifecycleScope.launch {
//            countDown(Dispatchers.Main, 0, 10) {
//                progress {
//                    loadingProgress.setProgress(it, 10f)
//                }
//            }
//        }
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true


        tv_launch.setOnClickListener {
            webView.loadUrl("file:///android_asset/agree.html")
            //            launchC()
//            launchThread()
//            launchScope()
//            scopeRelyOn()
//            flowThread()
            jobName()
//            loadingProgress.setText("当前")
//            loadingProgress.setProgress(10, 10f)
            lifecycleScope.launch {
                countDown(Dispatchers.Main, 0, 10) {
                    progress {
                        loadingProgress.setText("当前")
                        loadingProgress.setProgress(10, it.toFloat())
                    }
                }
            }

            lifecycleScope.launchWhenCreated {

            }
//            loadingProgress.setProgress(10, 25)
//            scopeConcurrence()

        }
    }

    /**
     * flow 线程测试
     */
    fun flowThread() {
        runBlocking {
            flow<Int> {
                for (i in 1..5) {
                    delay(1000)
                    emit(i)
                }
            }.map {
                println("flowThread: ${Thread.currentThread().name} -->map")
                it * it
            }
                .flowOn(Dispatchers.IO)
                .collect { println("flowThread: ${Thread.currentThread().name} -->$it") }
        }
    }

    /**
     * 协程job父子关系
     */
    fun jobName() {
        val scope = CoroutineScope(Job())
        val jobRoot = scope.launch(SupervisorJob()) {
            // new coroutine -> can suspend
            val job1 = launch {
                //child1
            }

            val job2 = launch {
                //child2

            }
            Log.e("job1-->", "$job1")
            Log.e("job2-->", "$job2")
        }
        Log.e("jobRoot-->", "$jobRoot")
    }

    fun launchThread() {
        //线程代码
        println("ThreadStart ${Thread.currentThread().name}")
        Thread {
            Thread.sleep(1000L)
            println("Thread_Hello World ${Thread.currentThread().name}")
        }.start()
        println("ThreadEnd ${Thread.currentThread().name}")
    }

    fun launchScope() {
        //协程代码
        println("ScopeStart ${Thread.currentThread().name}")
        GlobalScope.launch(Dispatchers.Main) {
            delay(1000L)
            println("Scope_Hello World ${Thread.currentThread().name}")
        }
        println("ScopeEnd ${Thread.currentThread().name}")
    }

    /**
     * 模拟对结果前后 依赖
     */
    fun scopeRelyOn() {
        GlobalScope.launch {
            val testBean = withContext(Dispatchers.IO) {
                TestBean("白展堂", 28)
            }
            val request1 = withContext(Dispatchers.IO) {
                request1(testBean)
            }
            println("request1-->${Thread.currentThread()}")
            println("request1-->${request1.age}==${request1.name}")
            val request2 = withContext(Dispatchers.IO) {
                request2(request1)
            }
            request2.name = "佟湘玉"
            request2.age = 24
            println("request2-->${request2.age}==${request2.name}")
            val request3 = withContext(Dispatchers.IO) {
                request3(request2)
            }
            println("request3-->${request3.age}==${request3.name}")
        }

    }

    suspend fun request1(parameter: TestBean): TestBean {
        return TestBean(parameter.name, parameter.age)
    }

    suspend fun request2(parameter: TestBean): TestBean {
        return TestBean(parameter.name, parameter.age)
    }

    suspend fun request3(parameter: TestBean): TestBean {
        return TestBean(parameter.name, parameter.age)
    }

    /**
     * 并发进行
     */
    fun scopeConcurrence() {
        GlobalScope.launch {
            val async1 = async { request1(TestBean("白展堂", 28)) }
            val async2 = async { request1(TestBean("佟湘玉", 26)) }
            val async3 = async { request1(TestBean("郭芙蓉", 24)) }
            println("async1-->${async1.await()}||async2-->${async2.await()}||async3-->${async3.await()}||")
        }
    }

    fun launchC() {
        val job: Job = GlobalScope.launch(Dispatchers.Default) {
            //启动协程
            Log.e("currThread1-->", "${Thread.currentThread()}")

            ////////////////////////////////////////////////////////////////
            val block: suspend CoroutineScope.() -> Unit = {
                //会启动新协程
                Log.e("currThread2-->", "${Thread.currentThread()}")
                runLaunch()
            }
            val job2 = launch(Dispatchers.Default, block = block)
            Log.e("job2-->", "$job2")

            ////////////////////////////////////////////////////////////
            val job3: Unit = runBlocking(Dispatchers.Default) {
                //会启动新协程
                Log.e("currThread3-->", "${Thread.currentThread()}")
                runLaunch()

            }
            Log.e("job3-->", "$job3")

            /////////////////////////////////////////////////////////////
            val runCatching = kotlin.runCatching {
                withContext(Dispatchers.Default) {
                    Log.e("currThread4-->", "${Thread.currentThread()}")
                    runLaunch()
                }
            }
            kotlin.runCatching {
                withContext(Dispatchers.Default) {
                    Log.e("currThread5-->", "${Thread.currentThread()}")
                    kotlin.runCatching {
                        withContext(Dispatchers.Default) {
                            Log.e("currThread6-->", "${Thread.currentThread()}")
                            kotlin.runCatching {
                                withContext(Dispatchers.Main) {
                                    Log.e("currThread7-->", "${Thread.currentThread()}")
                                }
                                runLaunch()
                            }
                        }
                    }
                }
            }
        }
        Log.e("job-->", "$job")

    }

    suspend fun runLaunch() {
        Log.e("suspend run-->", "${Thread.currentThread()}")
    }


}