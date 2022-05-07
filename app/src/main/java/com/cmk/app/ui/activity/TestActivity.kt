package com.cmk.app.ui.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.cmk.app.base.BaseActivity
import com.cmk.app.databinding.ActivityTestBinding
import com.cmk.app.ext.toast
import com.cmk.app.ui.adapter.test.TestAdapter
import com.cmk.app.util.permission.askPermissions
import com.cmk.app.util.permission.handlePermissionsResult
import com.cmk.app.vo.test.TestItemVo
import kotlinx.android.synthetic.main.activity_constraintlayout.*
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TestActivity() : BaseActivity() {

    private lateinit var binding: ActivityTestBinding
    private val stateFlow = MutableStateFlow<String>("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val mAdapter = TestAdapter()
        val testItemVo = TestItemVo()
        mAdapter.submitList(testItemVo.activityNames)
        binding.recyclerView.edgeEffectFactory = mAdapter.edgeEffectFactory
        binding.recyclerView.adapter = mAdapter
        mAdapter.setOnItemClickListener {
//            askPermissions(Manifest.permission.CAMERA) {
//                onGranted {
//                    toast("Camera permission is granted.")
//                }
//
//                onDenied {
//                    toast("Camera permission is denied")
//                }
//
//                onShowRationale { request ->
//                    //                    snack("You should grant permission for Camera") { request.retry() }
//                }
//
//                onNeverAskAgain {
//                    toast("Never ask again for camera permission")
//                }
//            }
            startActivity(Intent(this, testItemVo.activity[it]))
        }

        binding.editText.doOnTextChanged { text, start, before, count ->
            stateFlow.value = text.toString()
        }

        lifecycleScope.launch {
            stateFlow.sample(3000).filter {
                it.isNotEmpty()
            }.collect {
                Log.e("输入--->", it)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handlePermissionsResult(requestCode, permissions, grantResults)
    }
}