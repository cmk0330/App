package com.cmk.app.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cmk.app.base.BaseViewModel
import com.cmk.app.net.Http
import com.cmk.app.net.splashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*


class SplashViewModel : BaseViewModel() {

    val urlLiveData = MediatorLiveData<Bitmap>()

    fun requestADImg(context: Context) {
        launch {
            splashApi(Http.gankService.showGirl()) {
                success {
                    saveImg(context, it[(0..10).random()].url)
                }
            }
        }
    }

    fun getADImg(context: Context) {
        urlLiveData.addSource(getImage(context)) {
            urlLiveData.value = it
        }
    }

    private fun saveImg(context: Context, url: String) {
        var fixUrl = ""
        fixUrl = if (url.startsWith("http://"))
            url.replace("http://", "https://");
        else
            url
        Glide.with(context).asBitmap()
            .load(fixUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    launch {
                        withContext(Dispatchers.IO) {
                            val imgName = "splash_ad.jpg"
                            val file = File(
                                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                                imgName
                            )
                            try {
                                val fos = FileOutputStream(file)
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                                fos.flush()
                                fos.close()
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
    }

    private fun getImage(context: Context): LiveData<Bitmap> {
        return liveData<Bitmap> {
            withContext(Dispatchers.IO) {
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "splash_ad.jpg"
                )
                var fis: FileInputStream? = null
                var bis: BufferedInputStream? = null
                try {
                    fis = FileInputStream(file)
                    bis = BufferedInputStream(fis)
                    val bitmap = BitmapFactory.decodeStream(bis)
                    emit(bitmap)
                    bis.close()
                    fis.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        bis?.close()
                        fis?.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun saveVideo(context: Context, inputStream:InputStream) {
        try {
            val file =
                File(
                    context.getExternalFilesDir(Environment.DIRECTORY_MOVIES),
                    "splash_video_3.mp4"
                )
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int? = inputStream.available()
            val bufferSize = bytesAvailable?.let { it.coerceAtMost(maxBufferSize) }
            val buffers = bufferSize?.let { ByteArray(it) }
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
            file.parent
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
