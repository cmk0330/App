package com.cmk.app.repository.db

import androidx.paging.DataSource
import androidx.room.*
import com.cmk.app.vo.SearchResultVo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * @Author: romens
 * @Date: 2019-12-18 8:51
 * @Desc:
 */
@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history")
    fun getSearchHistoryAll(): DataSource.Factory<Int, SearchResultVo.History>

    @Query("SELECT * FROM search_history")
    fun getSearchHistoryAllFlow(): Flow<SearchResultVo.History>

    @Query("SELECT * FROM search_history WHERE id = :id")
    fun getSearchHistoryId(id: Int): DataSource.Factory<Int, SearchResultVo.History>

    @Query("SELECT * FROM search_history WHERE id = :id")
    fun getSearchHistoryIdFlow(id: Int): Flow<SearchResultVo.History>

    fun getSearchHistoryUntilChanged(id: Int) = getSearchHistoryIdFlow(id).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(vo: SearchResultVo.History): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(vo: List<SearchResultVo.History>): List<Long>

    @Update
    fun upDateHistory(vo: SearchResultVo.History): Int

    @Update
    fun upDateHistory(vo: List<SearchResultVo.History>): Int

    @Delete
    fun deleteHistory(vo: SearchResultVo.History): Int

    @Delete
    fun deleteHistory(vo: List<SearchResultVo.History>): Int
}