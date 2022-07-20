package com.cmk.app.viewmodel

import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel :ViewModel() {

    private val _themeState = MutableStateFlow(Theme.SYSTEM)
    val themeState = _themeState.asStateFlow()

    fun setCurrentTheme(theme: Theme) {
        _themeState.value = theme
    }
}

enum class Theme(val mode: Int) {
    LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
    DARK(AppCompatDelegate.MODE_NIGHT_YES),
    SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}