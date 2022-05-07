package com.cmk.app.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cmk.app.base.BaseViewModel

class CommunicationViewModel: BaseViewModel() {

    val progressLiveData:MutableLiveData<String> = MutableLiveData()

}