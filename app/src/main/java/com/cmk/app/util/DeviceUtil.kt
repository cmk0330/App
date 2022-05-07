import android.annotation.SuppressLint
import android.os.Build
import android.provider.Settings
import com.cmk.app.base.App

/**
 * 系统版本名
 */
fun getSDKVersionName(): String? {
    return Build.VERSION.RELEASE
}

/**
 * 系统版本号
 */
fun getSDKVersionCode(): Int {
    return Build.VERSION.SDK_INT
}

/**
 * 获取Android唯一id
 */
@SuppressLint("HardwareIds")
fun getAndroidId():String {
    val id = Settings.Secure.getString(
        App.application.contentResolver,
        Settings.Secure.ANDROID_ID
    )
    return if ("9774d56d682e549c" == id) "" else id ?: ""
}


