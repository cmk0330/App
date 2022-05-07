package com.cmk.app.vo

import android.os.Parcelable
import androidx.room.*
import com.cmk.app.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.parcel.Parcelize

/**
 * @Author: romens
 * @Date: 2019-11-11 14:05
 * @Desc:
 */
//@Entity(tableName = "article_data")
//@TypeConverters(value = [ArticleItemTagConverter::class, ArticleItemTagConverter::class])
data class ArticleVo(
//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "id")
    var id: Int = 0,
    var curPage: Int,
    var datas: List<DataX>,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int
) {
    @Parcelize
//    @Entity

    data class DataX(
        var apkLink: String,
        var audit: Int,
        var author: String,
        var chapterId: Int,
        var chapterName: String,
        var collect: Boolean,
        var courseId: Int,
        var desc: String,
        var envelopePic: String,
        var fresh: Boolean,
        var id: Int,
        var link: String,
        var niceDate: String,
        var niceShareDate: String,
        var origin: String,
        var prefix: String,
        var projectLink: String,
        var publishTime: Long,
        var selfVisible: Int,
        var shareDate: Long? = 0,
        var shareUser: String,
        var superChapterId: Int,
        var superChapterName: String,
        var tags: List<Tag>,
        var title: String,
        var type: Int,
        var userId: Int,
        var visible: Int,
        var zan: Int,
        var check: Boolean
    ) : Parcelable {
        fun getIcon(): Int {
            return when {
                link.contains("wanandroid.com") -> R.drawable.ic_logo_wan
                link.contains("www.jianshu.com") -> R.drawable.ic_logo_jianshu
                link.contains("juejin.im") -> R.drawable.ic_logo_juejin
                link.contains("blog.csdn.net") -> R.drawable.ic_logo_csdn
                link.contains("weixin.qq.com") -> R.drawable.ic_logo_wxi
                else -> R.drawable.ic_logo_other
            }
        }

        fun getUser(): String {

            var user: String = ""
            if (author.isNotEmpty()) user = "作者：$author"
            else if (shareUser.isNotEmpty()) user = "分享人：$shareUser"
            return user
        }

        fun getTag(): Boolean {
            return tags.isNotEmpty()
        }

        @Parcelize
        data class Tag(
            var name: String,
            var url: String
        ) : Parcelable
    }
}

class ArticleItemConverter {
    @TypeConverter
    fun objectToString(list: List<ArticleVo.DataX>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String) {
        val type = object : TypeToken<List<ArticleVo.DataX>>() {}.type
        return Gson().fromJson(json, type)
    }
}

class ArticleItemTagConverter {
    @TypeConverter
    fun objectToString(list: List<ArticleVo.DataX.Tag>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun stringToObject(json: String) {
        val type = object : TypeToken<List<ArticleVo.DataX.Tag>>() {}.type
        return Gson().fromJson(json, type)
    }
}