package com.cmk.app.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @Author: romens
 * @Date: 2020-1-13 15:04
 * @Desc:
 */
class LiveDataTestVM private constructor():ViewModel() {

    companion object {
        val instance:LiveDataTestVM by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED){
            LiveDataTestVM()
        }
    }

    val seekLiveData = MutableLiveData<Int>()
}