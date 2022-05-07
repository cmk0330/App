package com.cmk.app.test

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmk.app.R
import com.cmk.app.net.Http
import com.cmk.app.net.api
import com.cmk.app.net.apiRequest
import com.cmk.app.net.upLoad
import com.cmk.app.vo.BannerVo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class TestVM @ViewModelInject constructor(val retrofit: Retrofit) : ViewModel() {

    fun apiTime() {
        viewModelScope.launch {
            val start = System.currentTimeMillis()
//            api({ Http.service.articleList(0) }) {
//                success {
//                    val end = System.currentTimeMillis()
//                    Log.e("api所需时间-->", "${end - start}ms")
//                }
//            }
            Log.e("retrofit---->", " ${retrofit.hashCode()}")
        }
    }

    fun requestTime() {
        viewModelScope.launch {
            val start = System.currentTimeMillis()
            apiRequest({ Http.service.articleList(0) }) {

                success {
                    val end = System.currentTimeMillis()
                    Log.e("request所需时间-->", "${end - start}ms")
                }
            }
        }
    }

    fun saveVideo(context: Context) {
        try {
            val inputStream = context.resources.openRawResource(R.raw.splash_video_3)
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

    fun upLoadImage(context: Context) {
        val appKey: String = "0EE0A5AF831068465327FA692C7307FD"
        val appSecrect: String =
            "tlPgQB1h9yMqbdRHPzz1lGeoYgwRhQRiQIzeoovjXy084kadCxGUFcaS75F1NFnZItY9xrBxyAA"
        var uuid: String = "B9C4091FA358FE3FE29A51B6461B2312"
        var token: String = "8D4E7A5B5517DFE8C33F9D6B2EC778E671375579CE286B16321B97B5D00C51D0"

        val mediaType = "multipart/form-data".toMediaTypeOrNull()

        val requestAppKey = appKey.toRequestBody(mediaType)
        val requestUuid = uuid.toRequestBody(mediaType)

        val imageName = "妹子图片".toRequestBody(mediaType)
        val imageFile = drawableToFile(context, R.mipmap.ic_user_photo_bg, File("upload_tx"))
        val imageToBase64 = T.imageToBase64(imageFile)
        Log.e("base64-->", imageToBase64)
        val requestImageFile = imageFile?.asRequestBody(mediaType)
        val request64Image = imageToBase64.toRequestBody(mediaType)
        val requestImagePart =
            MultipartBody.Part.createFormData("image_file", imageFile!!.name, request64Image!!)
        val bodyBuilder = FormBody.Builder()
        val encodedNames = listOf("app_key", "file", "file_name")
        val encodedValues = listOf(appKey, imageToBase64, "test.png")
        val upBuild = bodyBuilder
            .add("app_key", appKey)
            .add("file", imageToBase64)
            .add("file_name", "上传图片测试")
            .build()

        viewModelScope.launch {
            upLoad(
                Http.upService.upLogin(
//                    "B58D65E6511914803F21173BDA8C3928",
//                    "0EE0A5AF831068465327FA692C7307FD",
                    "cmk0330",
                    "0793ae47572012e4ecbda76616fb98cb"
//                    "1",
//                    "2"
                )
            ) {
                success {
                    Log.e("登录完成-->", it.token)
                    uuid = it.uuid
                    token = it.token
                }

                failure {
                    Log.e("登录失败-->", "${it.message}")
                }
                error { errorCode, errorMsg ->
                    Log.e("登录错误-->", "[$errorCode:$errorMsg]")
                }
            }

            upLoad(
                Http.uploadService.upLoadImageBase64(upBuild)
            ) {
                success {
                    Log.e("图片上传完成-->", "$it")
                }

                failure {
                    Log.e("图片上传失败-->", "${it.message}")
                }

                error { errorCode, errorMsg ->
                    Log.e("图片上传错误-->", "[$errorCode:$errorMsg]")
                }
            }
        }

//        viewModelScope.launch {


//            val requestUuid = uuid.toRequestBody(mediaType)
//            val requestToken = token.toRequestBody(mediaType)
//
//            upLoad(
//                PhotoHttp.uploadService.upLoadImageBase64(
//                    requestUuid,
//                    requestToken,
//                    requestImagePart,
//                    imageName
//                )
//            ) {
//                success {
//                    Log.e("图片上传完成-->","$it")
//                }
//
//                failure {
//                    Log.e("图片上传失败-->","${it.message}")
//                }
//
//                error { errorCode, errorMsg ->
//                    Log.e("图片上传错误-->","[$errorCode:$errorMsg]")
//                }
//            }
//        }
    }

    /**
     * 模拟串行请求
     */
    fun httpResult() {
        viewModelScope.launch {
            val bannerResult = api({ Http.service.bannerList() }) {
                success {
                    Log.e("TestVM-->", "banner请求完成-->$it")
                    Thread.sleep(3000)
                }
            }
        }

        viewModelScope.launch {

        }
    }

    fun drawableToFile(
        mContext: Context,
        drawableId: Int,
        fileName: File
    ): File? {
//        InputStream is = view.getContext().getResources().openRawResource(R.drawable.logo);
        val bitmap = BitmapFactory.decodeResource(mContext.resources, drawableId)
        //        Bitmap bitmap = BitmapFactory.decodeStream(is);
        val defaultPath = mContext.filesDir
            .absolutePath + "/defImgPath"
        var file = File(defaultPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val defaultImgPath = "$defaultPath/$fileName"
        file = File(defaultImgPath)
        try {
            file.createNewFile()
            val fOut = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, fOut)
            //            is.close();
            fOut.flush()
            fOut.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file
    }


//    fun banner() {
//        val liveData = viewModelScope.requestLiveData<List<BannerVo>> {
////            api { Http.service.bannerList() }
//
//        }
//        bannerLiveData.addSource(liveData) {
//
//            bannerLiveData.value = it.data
//        }
//    }
}