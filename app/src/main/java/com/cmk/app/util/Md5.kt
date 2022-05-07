package com.cmk.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * 获取app的MD5值
 * @return
 */
@SuppressLint("PackageManagerGetSignatures")
fun getSignMd5Str(context: Context): String? {
    try {
        val packageInfo = context.packageManager
            .getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
        val signs = packageInfo.signatures
        val sign = signs[0]
        return encryptionMD5(sign.toByteArray())
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * MD5加密
 * @param byteStr 需要加密的内容
 * @return 返回 byteStr的md5值
 */
fun encryptionMD5(byteStr: ByteArray?): String {
    val messageDigest: MessageDigest?
    val md5StrBuff = StringBuffer()
    try {
        messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.reset()
        messageDigest.update(byteStr)
        val byteArray = messageDigest.digest()
        for (i in byteArray.indices) {
            if (Integer.toHexString(0xFF and byteArray[i].toInt()).length == 1) {
                md5StrBuff.append("0")
                    .append(Integer.toHexString(0xFF and byteArray[i].toInt()))
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF and byteArray[i].toInt()))
            }
        }
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return md5StrBuff.toString()
}