package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cmk.app.R
import com.cmk.app.net.ApiService
import com.cmk.app.net.AppDownloadManager
import okhttp3.ResponseBody
import java.io.*


/**
 * @Author: romens
 * @Date: 2019-12-23 15:01
 * @Desc:
 */
class DownloadActivityTest : AppCompatActivity() {

    private val SD_HOME_DIR: String = Environment.getExternalStorageDirectory().path

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_down_test)

        findViewById<TextView>(R.id.tv_down).setOnClickListener {
           download()
        }
    }

    private fun download() {
        AppDownloadManager.download(this, ApiService.DOWN_WX, "weixin", true,"")
    }

    private fun down() {
//        Http.downService.downLoadWX2().enqueue(object :Callback<ResponseBody>{
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Log.e("下载失败", "${t.message}")
//            }
//
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                if (response.isSuccessful) {
//                    Thread(Runnable {
//                        val b = response.body()?.let { writeResponseBodyToDisk(it) }
//                        if (b!!) Log.e("下载成功", "----") else Log.e("下载失败", "----")
//                    }).start()
//                }
//            }
//        })
    }

    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
            return try { //判断文件夹是否存在
                Log.e("开始下载-->","====")
                val files = File(SD_HOME_DIR) //跟目录一个文件夹
                if (!files.exists()) { //不存在就创建出来
                    files.mkdirs()
                }
                //创建一个文件
                val futureStudioIconFile = File(SD_HOME_DIR + "/kotlin_down_demo")
                Log.e("文件路径--->", futureStudioIconFile.path)
                //初始化输入流
                var inputStream: InputStream? = null
                //初始化输出流
                var outputStream: OutputStream? = null
                try { //设置每次读写的字节
                    val fileReader = ByteArray(4096)
                    val fileSize = body.contentLength()
//                    Log.e("文件大小--->", "$fileSize")
                    var fileSizeDownloaded: Long = 0
                    //请求返回的字节流
                    inputStream = body.byteStream()
                    //创建输出流
                    outputStream = FileOutputStream(futureStudioIconFile)
                    //进行读取操作
                    while (true) {
                        val read: Int = inputStream.read(fileReader)
                        if (read == -1) {
                            break
                        }
                        //进行写入操作
                        outputStream.write(fileReader, 0, read)
                        fileSizeDownloaded += read.toLong()
//                        Log.e("正在写入文件--->", "写入大小-->$fileSize")
                    }
                    //刷新
                    outputStream.flush()
                    true
                } catch (e: IOException) {
                    Log.e("异常-->","${e.message}")
                    false
                } finally {
                    inputStream?.close()
                    outputStream?.close()
                }
            } catch (e: IOException) {
                false
            }
    }
}