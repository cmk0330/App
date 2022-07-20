package com.cmk.app.viewmodel

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.paging.PagedList
import androidx.paging.PagingData
import com.cmk.app.base.App
import com.cmk.app.base.BaseViewModel
import com.cmk.app.net.Http
import com.cmk.app.net.api
import com.cmk.app.net.onCatch
import com.cmk.app.net.onCollect
import com.cmk.app.repository.SearchRepository
import com.cmk.app.repository.db.AppDataBase
import com.cmk.app.vo.HotKeyVo
import com.cmk.app.vo.SearchResultVo
import com.cmk.app.vo.SearchVo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * @Author: romens
 * @Date: 2019-12-17 15:01
 * @Desc:
 */
class SearchViewModel : BaseViewModel() {

    val repository by lazy { SearchRepository() }
    private val _state = MutableStateFlow(SearchModel())
    val state = _state.asStateFlow()
    private val _typeState = MutableStateFlow(emptyList<SearchVo>())
    val typeState = _typeState.asStateFlow()
    val searchAuthorLiveData = MediatorLiveData<PagingData<SearchResultVo.Author.DataX>>()
    val resultStateLiveData = MediatorLiveData<SearchResultVo.Author>()
    private val _searchState = MutableStateFlow(emptyList<HotKeyVo>())
    val searchState = _searchState.asStateFlow()


    fun loadType() {
        launch {
            val list = ArrayList<SearchVo>()
            list.add(SearchVo("公众号"))
            list.add(SearchVo("热词"))
            list.add(SearchVo("作者"))
            _typeState.value = list
        }
    }

    fun loadHotKey() {
        launch {
            flow { emit(Http.service.hotKey()) }
                .onCatch()
                .onCollect { success { _searchState.value = it } }
        }
    }

    /**
     * 所搜作者文章
     */
//    fun loadSearchAuthor(page: Int, author: String) {
//        launch {
//            api(Http.service.searchAuthor(page, author)) {
//                success {
//                    searchAuthorLiveData.value = it
//                }
//
//                failure {
//                    Log.e("loadSearchAuthor--", "error${it.message}")
//                }
//            }
//        }
//    }

    fun getSearchAuthor(author: String) {
        val asLiveData = repository.getSearchAuthor(author).asLiveData()
        searchAuthorLiveData.addSource(asLiveData) {
            searchAuthorLiveData.postValue(it)
        }
    }

    fun loadSearchAuthor(author: String) = repository.getSearchAuthor(author)

    fun insertHistory(vo: SearchResultVo.History) {
        launch {
            val result = withContext(Dispatchers.IO) {
                AppDataBase.getInstance(App.context).SearchHistoryDao().insertHistory(vo)
//                repository.insertHistory(vo)
            }
            Log.e("insert-->", "$result")
            if (result > 0)
                getSearchHistoryAll()
        }

    }

    fun deleteHistory(vo: SearchResultVo.History) {
        launch {
            val result = withContext(Dispatchers.IO) {
                AppDataBase.getInstance(App.context).SearchHistoryDao().deleteHistory(vo)
//                repository.deleteHistory(vo)
            }
            if (result > 0)
                getSearchHistoryAll()
        }
    }

    fun getSearchHistoryAll() {
        launch {
//            val dataSource = withContext(Dispatchers.IO) {
//                AppDataBase.getInstance(App.context).SearchHistoryDao().getSearchHistoryAll()
//            }
//            val liveData = dataSource.toLiveData(pagingConfig))
//            historyPagedLiveData.addSource(liveData) {
//                historyPagedLiveData.value = it
//            }
        }
    }

    data class SearchModel(
        val backVisibility: Boolean = false,
        val cleanVisibility: Boolean = false,
        val editIsNull: Boolean = true,
        val resultRvVisibility: Boolean = false,
        val typeRvVisibility: Boolean = true
    )
}