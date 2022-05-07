package com.cmk.app.repository

import androidx.paging.Pager
import com.cmk.app.base.Repository
import com.cmk.app.config.pagingConfig
import com.cmk.app.datasource.PlazaSource

/**
 * @Author: romens
 * @Date: 2019-11-27 9:22
 * @Desc:
 */
class PlazaRepository : Repository(){
    fun getPlazaList() = Pager(config = pagingConfig(), pagingSourceFactory = {PlazaSource()}).flow
}