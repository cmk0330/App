package com.cmk.app.datasource

//@ExperimentalPagingApi
//class ArticleMediator() :RemoteMediator<Int, ArticleVo.DataX>(){
//    override suspend fun load(
//        loadType: LoadType,
//        state: PagingState<Int, ArticleVo.DataX>
//    ): MediatorResult {
//      return  when(loadType) {
//            // 首次访问 或者调用 PagingDataAdapter.refresh()
//             LoadType.REFRESH->null
//            // 在当前加载的数据集的开头加载数据时
//            LoadType.PREPEND-> MediatorResult.Success(endOfPaginationReached = true)
//            // 在当前数据集末尾添加数据
//            LoadType.APPEND-> {
//                state.pages
//                val data = Http.service.articleList(1).data
//            }
//        }
//    }
//}