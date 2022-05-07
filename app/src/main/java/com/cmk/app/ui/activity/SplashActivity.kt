package com.cmk.app.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.cmk.app.MainActivity
import com.cmk.app.R
import com.cmk.app.compose.TimeUtil
import com.cmk.app.util.Preference
import com.cmk.app.util.countDown
import com.cmk.app.viewmodel.SplashViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.launch
import java.io.File

//https://www.jianshu.com/p/e6656707f56c
/* 一般用于视频播放，用户的任何交互都会导致全屏状态被系统清除*/
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
/* 一般用于沉浸式阅读*/
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE)
/*用于严格的沉浸式，如游戏中*/
//        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

class SplashActivity : AppCompatActivity() {

    private val viewModel by viewModels<SplashViewModel>()
    private var showADImg by Preference(Preference.SPLASH_SHOW_AD, true)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        TimeUtil.getCurrDate()
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()
        showADImg = if (showADImg) {
            showImg()
            false
        } else {
            showVideo()
            true
        }
        lifecycleScope.launch {
            countDown(this.coroutineContext, 3, 0) {
                progress {
                    loadingProgress.setProgress(3, it.toFloat())
                }
                finish {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }

        llJump.setOnClickListener {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

        tvSwitchAD.setOnClickListener {
            if (showADImg) {
                Toast.makeText(this, "正在展示图片", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "正在展示视频", Toast.LENGTH_SHORT).show()
            }
            showADImg = !showADImg
        }

        viewModel.urlLiveData.observe(this, Observer {
            Log.e("监听到image", "$it")
            Glide.with(this).load(it).into(showAD)
        })
    }

    private fun showImg() {
        showAD.visibility = View.VISIBLE
        videoView.visibility = View.GONE
        viewModel.getADImg(this)
//        viewModel.requestADImg(this)
    }

    private fun showVideo() {
        videoView.visibility = View.VISIBLE
        showAD.visibility = View.GONE
        val movieFile =
            getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.path + "/splash_video_3.mp4"
        val file = File(movieFile)
        if (file.exists()) {
            videoView.setVideoPath(movieFile)
        } else {
            videoView.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_video_3))
            val inputStream = resources.openRawResource(R.raw.splash_video_3)
            viewModel.saveVideo(this, inputStream)
        }
        videoView.start()
        videoView.setOnCompletionListener {
            videoView.start()
        }
    }
}