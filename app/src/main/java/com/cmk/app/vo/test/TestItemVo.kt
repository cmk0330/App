package com.cmk.app.vo.test

import com.cmk.app.compose.ComposeActivity
import com.cmk.app.test.TestCoroutineActivity
import com.cmk.app.ui.activity.test.*
import com.cmk.app.widget.gallery.AlbumPreviewActivity

class TestItemVo() {
    var name: String = ""

    constructor(name: String) : this() {
        this.name = name
    }

    val activityNames = arrayListOf<String>(
        "MergeAdapter",
        "FabActivity",
        "DownLoadActivity",
        "TestCoroutineActivity",
        "TinkerActivity",
        "MapActivity",
        "NestScrollActivity",
        "Paging3Activity",
        "CommunicationActivity",
        "AlbumPreviewActivity",
        "ShareImageActivity",
        "ConstraintLayoutActivity",
        "MotionLayoutActivity",
        "ComposeActivity",
        "StateActivity",
        "ListNotifyActivity"
    )
    val activity = arrayOf(
        MergeAdapterDBActivity::class.java,
        FabActivity::class.java,
        DownloadActivityTest::class.java,
        TestCoroutineActivity::class.java,
        TinkerActivity::class.java,
        MapActivity::class.java,
        NestScrollActivity::class.java,
        Paging3Activity::class.java,
        CommunicationActivity::class.java,
        AlbumPreviewActivity::class.java,
        ShareImageActivity::class.java,
        ConstraintLayoutActivity::class.java,
        MotionLayoutActivity::class.java,
        ComposeActivity::class.java,
        StateActivity::class.java,
        ListNotifyActivity::class.java
    )
    val activityMap = mapOf("MergeAdapter" to MergeAdapterDBActivity::class.java)
}