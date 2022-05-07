package com.cmk.app.net

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.TextUtils
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.cmk.app.ext.toast
import com.cmk.app.util.permission.askPermissions
import java.io.File

class AppDownloadManager(
    var context: Context,
    var url: String?,
    var downloadName: String?,
    var isWIFI: Boolean,
    var description: String?
) {

    init {
//        val context = builder.context
//        val url = builder.url
//        var isWiFi = builder.isWiFi
//        val description = builder.description
//        val downloadName = builder.downloadName
//        if (isWiFi == null) {
//            isWiFi = true
//        }
        download(context, url, downloadName, isWIFI, description)
    }

    companion object {
        private const val WRITE_EXTERNAL_STORAGE = 123
        private val STORAGE =
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        @SuppressLint("CheckResult")
        fun download(
            context: Context,
            url: String?,
            downloadName: String?,
            isWIFI: Boolean,
            description: String?
        ) {
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(context, "下载地址不能为空", Toast.LENGTH_SHORT).show()
                return
            }

            (context as FragmentActivity).askPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                onGranted {
                    downloadApk(
                        context,
                        url,
                        isWIFI,
                        description,
                        downloadName
                    )
                }

                onDenied {
                    context.toast("权限未开启")
                }

                onShowRationale {
                    it.retry()
                }

                onNeverAskAgain {
                    context.toast("永久拒绝下载权限")
                }
            }
        }

        private fun downloadApk(
            context: Context,
            url: String?,
            isWIFI: Boolean,
            description: String?,
            downloadName: String?
        ) {
            val request =
                DownloadManager.Request(Uri.parse(url))
            //判断是在wifi还是在移动网络下进行下载
            if (isWIFI) {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
            } else {
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
            }
            //下载时显示notification
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            //添加描述信息
            request.setDescription(description)
            //file:///storage/emulated/0/Download/downloadName.apk
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "$downloadName.apk"
            )
            request.setMimeType("application/vnd.android.package-archive")
            val systemService =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            systemService.enqueue(request)
        }

        fun intallApk(context: Context, downloadName: String) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) { //6.0及以下安装
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(
                    Uri.parse("file:///storage/emulated/0/Download/$downloadName.apk"),
                    "application/vnd.android.package-archive"
                )
                //为这个新apk开启一个新的activity栈
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //开始安装
                context.startActivity(intent)
            } else { //7.0及以上
                val file = File(
                 context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    , "/$downloadName.apk"
                )
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                val apkUri =
                    FileProvider.getUriForFile(context, "com.qubin.downloadmanager", file)
                val intent = Intent(Intent.ACTION_VIEW)
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                context.startActivity(intent)
            }
        }
    }

}
