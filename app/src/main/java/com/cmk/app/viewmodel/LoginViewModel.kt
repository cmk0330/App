package com.cmk.app.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cmk.app.base.App
import com.cmk.app.base.BaseViewModel
import com.cmk.app.net.*
import com.cmk.app.repository.MineRepository
import com.cmk.app.util.Preference
import com.cmk.app.util.countDown
import com.cmk.app.vo.GirlVo
import com.cmk.app.widget.ontouch.log
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.error

/**
 * @Author: romens
 * @Date: 2020-1-16 10:17
 * @Desc:
 */
class LoginViewModel : BaseViewModel() {

    val repository by lazy { MineRepository() }
    private val _loginState = MutableStateFlow<LoginViewState>(LoginViewState(false))
    val loginState = _loginState.asStateFlow()
    val countDownLiveData: MutableLiveData<Int> = MutableLiveData()
    val dynamicLiveData: MutableLiveData<List<GirlVo.Data>> = MutableLiveData()
    private var isLogin by Preference(Preference.IS_LOGIN, false)
    private var userGson by Preference(Preference.USER_GSON, "")

    fun login(userName: String, passWord: String) {
        launch {
            if (userName.isBlank() || passWord.isBlank()) return@launch
            flow { emit(Http.service.login(userName, passWord)) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    success {
                        userGson = Gson().toJson(it)
                        isLogin = true
                        App.CURRENT_USER = it
                        _loginState.value = LoginViewState(loginState = true)
                    }

                    error { errorCode, errorMsg ->
                        _loginState.value =
                            LoginViewState(loginState = false, errorMsg = errorMsg)
                        Log.e("登录错误", "$errorCode, $errorMsg")
                    }
                }
        }
    }

    fun loginOut() {
        launch {
            flow { emit(Http.service.loginOut()) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    error { errorCode, errorMsg ->
                        Log.e("登出错误", "$errorCode, $errorMsg")
                    }

                    empty {
                        userGson = ""
                        isLogin = false
                        _loginState.value = LoginViewState(loginState = false, tag = 110)
                    }
                }
        }
    }

    fun getUserInfo() {
        launch {
            flow { emit(Http.service.getUserInfo()) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    Log.e("--------->", "222222222222")
                    success {
                        Log.d("用户信息", it.toString())
                        Log.e("--------->", "333333333333333333333333")
                    }
                    error { errorCode, errorMsg ->
                        Log.e("获取用户信息出错", "$errorCode, $errorMsg")
                    }
                }
        }
    }

    fun regedit(userName: String, passWord: String, rePassWord: String) {
        launch {
            if (userName.isBlank() || passWord.isBlank() || rePassWord.isBlank()) return@launch
            flow { emit(Http.service.regedit(userName,passWord, rePassWord)) }
                .onStart { loadingState(isLoading = true) }
                .onCompletion { loadingState(isLoading = false) }
                .onCatch()
                .onCollect {
                    success {
                        _loginState.value = (LoginViewState(enableLoginButton = false))
                        login(userName, passWord)
                    }
                    error { errorCode, errorMsg ->
                        Log.e("注册从无", "$errorCode, $errorMsg")
                    }
                }
        }
    }

    fun sendCode() {
        launch {
            countDown(this.coroutineContext, 60, 0) {
                progress {
                    countDownLiveData.value = it
                }

                finish {
                    countDownLiveData.value = -1
                }
            }
        }
    }

    private fun inputValid(userName: String, passWord: String) =
        userName.isNotBlank() && passWord.isNotBlank()

    fun loginDataChange(userName: String, passWord: String) {
        _loginState.value = (LoginViewState(inputValid(userName, passWord)))
    }

    /**
     * 我的动态
     */
    fun getDynamic(page: Int? = 1, count: Int? = 10) {
        launch {
            splashApi(Http.gankService.myDynamic("Girl/page/$page/count/$count")) {
                success { dynamicLiveData.value = it }
                failure { Log.e("动态图片获取失败-->", "${it.message}") }
                error { errorCode, errorMsg -> Log.e("动态图片获取错误-->", "[$errorCode:$errorMsg]") }
            }
        }
    }

    fun loadDynamic() = repository.getDynamic()

    data class LoginViewState(
        val enableLoginButton: Boolean = false,
        val loginState: Boolean = false,
        val errorMsg: String = "",
        val tag:Int = 0
    )
}