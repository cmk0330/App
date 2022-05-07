package com.cmk.app.widget

import com.cmk.app.R

/**
 *
 * @Author: romens
 * @Description: java类作用描述
 * @CreateDate: 2020-11-10 14:59
 * @UpdateUser:
 * @UpdateDate: 2020-11-10 14:59
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
val configList = listOf(
    StatusLayout.StatusConfig(
        StatusLayout.STATUS_LOADING,
        R.layout.layout_loading
    ),
    StatusLayout.StatusConfig(
        StatusLayout.STATUS_EMPTY,
        R.layout.layout_empty
    ),
    StatusLayout.StatusConfig(
        StatusLayout.STATUS_ERROR,
        R.layout.layout_error,
        clickRes = R.id.btn_retry
    )
)