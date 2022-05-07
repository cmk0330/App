package com.cmk.app.net

sealed class LoadState(val msg: String) {
    class Load(msg: String = "") : LoadState(msg)
    class Success(msg: String = "") : LoadState(msg)
    class Failure(msg: String) : LoadState(msg)
}