package com.cmk.app.compose

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.paging.compose.collectAsLazyPagingItems
import com.cmk.app.viewmodel.HomeViewModel
import androidx.paging.compose.items
import com.cmk.app.compose.TimeUtil.getCurrDate
import com.cmk.app.compose.TimeUtil.getCurrTime
import java.util.*

class ComposeActivity : AppCompatActivity() {

    val viewModel by viewModels<HomeViewModel>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArticleList()
//            TimeUtil.test()
//            AnimateAsStateDemo()
        }
    }


    @Composable
    fun ArticleList() {
        val rememberLazyListState = rememberLazyListState()
        val lazyPagingItems = viewModel.getArticle().flow.collectAsLazyPagingItems()
//        val observeAsState = viewModel.articleLiveData.observeAsState(emptyList())
//        viewModel.getArticle()
        Column (horizontalAlignment = Alignment.CenterHorizontally){
            EditText()
            LazyColumn(state = rememberLazyListState) {
                items(lazyPagingItems) { data ->
                    data?.let {
                        ItemViewCompose(this@ComposeActivity, data)
                    }
                }
            }

//            LazyColumn(state = rememberLazyListState) {
//                val dataList = observeAsState.value
//                items(dataList!!) { data->
//                    ItemViewCompose(this@ComposeActivity, data)
//                }
//            }
        }
    }
}