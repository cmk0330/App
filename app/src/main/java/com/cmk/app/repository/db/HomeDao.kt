package com.cmk.app.repository.db

import androidx.room.*
import com.cmk.app.vo.ArticleVo

//@Dao
interface HomeDao {

//    @Insert
    fun insertData(vararg data: ArticleVo?): LongArray?

//    @Query("SELECT * FROM article_data")
    fun getArticleData(): ArticleVo

//    @Update
    fun upDateArticleData(vararg data: ArticleVo): Int

//    @Delete
    fun delete(vararg data: ArticleVo): Int
}