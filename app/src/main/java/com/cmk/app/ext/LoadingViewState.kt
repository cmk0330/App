package com.cmk.app.ext

data class LoadingViewState(
    val state: Boolean = false,
    val content: String = ""
)

sealed class LoadingViewEvent {
    data class ShowToast(val message: String) : LoadingViewEvent()
    object ShowLoadingDialog : LoadingViewEvent()
    object DismissLoadingDialog : LoadingViewEvent()
}

sealed class LoadingViewAction {
    object PageRequest : LoadingViewAction()
    object PartRequest : LoadingViewAction()
    object MultiRequest : LoadingViewAction()
    object ErrorRequest : LoadingViewAction()
}


sealed class PageStatus {
    object Loading : PageStatus()
    object Success : PageStatus()
    data class Error(val throwable: Throwable) : PageStatus()
}