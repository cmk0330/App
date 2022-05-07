package com.cmk.app.net

import android.content.Context
import android.util.Log
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.OutputStream


fun downLoad(context: Context, fileName: String, response: ResponseBody, listener: DownLoadListener) {

    Result
    val file =
        File("${context.getExternalFilesDir("downLoad/$fileName")}")

//        val file =
//            File("${Environment.getExternalStorageDirectory().path}/kotlinApp/$fileName}")
    file.createNewFile()
    var inStream: InputStream? = null
    var outStream: OutputStream? = null
    try {
        inStream = response.byteStream()
        outStream = file.outputStream()
        //文件总长度
        val contentLength = response.contentLength()
        listener.onStart(contentLength)
        //当前已下载长度
        var currentLength = 0L
        //缓冲区
        val buff = ByteArray(1024)
        var len = inStream.read(buff)
        var percent = 0
        while (len != -1) {
            outStream.write(buff, 0, len)
            currentLength += len
            /*不要频繁的调用切换线程,否则某些手机可能因为频繁切换线程导致卡顿,
            这里加一个限制条件,只有下载百分比更新了才切换线程去更新UI*/
            if ((currentLength * 100 / contentLength).toInt() > percent) {
                percent = (currentLength / contentLength * 100).toInt()
                Log.e("正在下载:", "$currentLength / $contentLength")
                Log.e("百分比:", "$percent")
                //更新完成UI之后立刻切回IO线程
                listener.onProgress(currentLength)
            }
            len = inStream.read(buff)
        }
        listener.onCompleted()
        Log.e("downLoad-->", "下载完成---------------------")
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        inStream?.close()
        outStream?.close()
    }
}

interface DownLoadListener {
    fun onStart(totalLength: Long)
    fun onProgress(currentLength: Long)
    fun onCompleted()
}