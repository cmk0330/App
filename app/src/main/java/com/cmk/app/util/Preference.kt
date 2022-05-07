package com.cmk.app.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.cmk.app.base.App
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @Author: romens
 * @Date: 2019-11-15 9:01
 * @Desc:
 */
class Preference<T>(var name: String, private val value: T) : ReadWriteProperty<Any?, T> {

    companion object {
        const val IS_LOGIN = "is_login"
        const val USER_GSON = "user_gson"
        const val SPLASH_SHOW_AD = "splash_show_ad"
    }

//    private val pre:SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(App.context) }
    private val pre:SharedPreferences by lazy { App.context.getSharedPreferences(name, Context.MODE_PRIVATE) }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getValue(name, value)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putValue(name, value)
    }

    @SuppressLint("CommitPrefEdits")
    private fun <T> putValue(name: String, value: T) = with(pre.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> putString(name, serialize(value))
        }.apply()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(name: String, default: T): T = with(pre) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)?:""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> deSerialization(getString(name, serialize(default))?:"")
        }
        return res as T
    }

    /**
     * 删除全部数据
     */
    fun clearPreference() {
        pre.edit().clear().apply()
    }

    /**
     * 根据key删除存储数据
     */
    fun clearPreference(key: String) {
        pre.edit().remove(key).apply()
    }

    /**
     * 序列化对象
     * @param person
     * *
     * @return
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun <A> serialize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(
            byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr
    }

    /**
     * 反序列化对象
     * @param str
     * *
     * @return
     * *
     * @throws IOException
     * *
     * @throws ClassNotFoundException
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun <A> deSerialization(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(
            redStr.toByteArray(charset("ISO-8859-1")))
        val objectInputStream = ObjectInputStream(
            byteArrayInputStream)
        val obj = objectInputStream.readObject() as A
        objectInputStream.close()
        byteArrayInputStream.close()
        return obj
    }


    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String): Boolean {
        return pre.contains(key)
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    fun getAll(): Map<String, *> {
        return pre.all
    }
}