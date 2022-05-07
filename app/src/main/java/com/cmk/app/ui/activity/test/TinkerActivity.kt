package com.cmk.app.ui.activity.test

import android.os.Bundle
import android.os.Process
import com.cmk.app.R
import com.cmk.app.base.BaseDBActivity
import com.cmk.app.databinding.ActivityTinkerBinding

class TinkerActivity: BaseDBActivity<ActivityTinkerBinding>() {
    override val layoutId: Int
        get() = R.layout.activity_tinker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.requestPatch.setOnClickListener {
//            TinkerPatch.with().fetchPatchUpdate(true)
        }

        binding.requestConfig.setOnClickListener {
//            TinkerPatch.with().fetchDynamicConfig(object : ConfigRequestCallback {
//                override fun onSuccess(configs: HashMap<String, String>) {
//                    TinkerLog.w("TinkerActivity-->", "request config success, config:$configs")
//                }
//
//                override fun onFail(e: Exception) {
//                    TinkerLog.w("TinkerActivity-->", "request config failed, exception:$e")
//                }
//            }, true)
        }

        binding.cleanPatch.setOnClickListener {
//            TinkerPatch.with().cleanAll()
        }

        binding.killSelf.setOnClickListener {
//            ShareTinkerInternals.killAllOtherProcess(applicationContext)
            Process.killProcess(Process.myPid())
        }
    }
}