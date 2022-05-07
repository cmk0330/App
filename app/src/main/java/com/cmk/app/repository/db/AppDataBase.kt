package com.cmk.app.repository.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cmk.app.vo.ArticleItemConverter
import com.cmk.app.vo.ArticleVo
import com.cmk.app.vo.SearchResultVo

/**
 * @Author: romens
 * @Date: 2019-12-18 9:11
 * @Desc:
 */
@Database(
    entities = [SearchResultVo.History::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    val DATABASE_NAME = "app-search-history-db.db"
    abstract fun SearchHistoryDao(): SearchHistoryDao
//    abstract fun HomeDao(): HomeDao

    companion object {
        @Volatile
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {

                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(context: Context): AppDataBase {
//            return Room.databaseBuilder(context, AppDataBase::class.java, "app-search-history-db.db").build()
            return Room.databaseBuilder(
                context,
                AppDataBase::class.java,
                "app-db.db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Log.e("appdatabase-->", "buildDatabase")
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        Log.e("appdatabase-->", "onOpen")
                    }
                }).build()
        }
    }
}