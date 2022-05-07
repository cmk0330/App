package com.cmk.app.vo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @Author: romens
 * @Date: 2019-12-25 11:39
 * @Desc:
 */
class SearchResultVo {

    @Entity(tableName = "search_history")
    class History {
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        private var id = 0
        private var name: String? = null
        private var time: String? = null
        private var type = 0

        fun getId(): Int {
            return id
        }

        fun setId(id: Int) {
            this.id = id
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String) {
            this.name = name
        }

        fun getTime(): String? {
            return time
        }

        fun setTime(time: String?) {
            this.time = time
        }

        fun getType(): Int {
            return type
        }

        fun setType(type: Int) {
            this.type = type
        }
    }

    @Parcelize
    data class Author(
        var curPage: Int,
        var datas: List<DataX>,
        var offset: Int,
        var over: Boolean,
        var pageCount: Int,
        var size: Int,
        var total: Int
    ) : Parcelable {
        @Parcelize
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
            var shareDate: Long,
            var shareUser: String,
            var superChapterId: Int,
            var superChapterName: String,
            var tags: List<Tag>,
            var title: String,
            var type: Int,
            var userId: Int,
            var visible: Int,
            var zan: Int
        ) : Parcelable {
            @Parcelize
            data class Tag(
                var name: String,
                var url: String
            ) : Parcelable
        }
    }
}