package com.cmk.app.viewmodel

import com.cmk.app.base.BaseViewModel
import com.cmk.app.repository.PlazaRepository

/**
 * @Author: romens
 * @Date: 2019-11-25 16:29
 * @Desc:
 */
class PlazaViewModel : BaseViewModel() {

    private val repository by lazy { PlazaRepository() }

    fun getPlazaList() = repository.getPlazaList()
}