package com.cmk.app.ui.activity.test

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ConcatAdapter
import com.cmk.app.R
import com.cmk.app.base.BaseActivity
import com.cmk.app.base.dataBinding
import com.cmk.app.databinding.ActivityMergeAdapterBinding
import com.cmk.app.test.TestVM
import com.cmk.app.ui.adapter.test.FooterMergeAdapter
import com.cmk.app.ui.adapter.test.StudentMergeAdapter
import com.cmk.app.ui.adapter.test.TeacherMergeAdapter
import com.cmk.app.util.permission.handlePermissionsResult
import com.cmk.app.vo.test.MergeVo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject
import javax.inject.Qualifier

@AndroidEntryPoint
class MergeAdapterDBActivity : BaseActivity() {

    val binding by dataBinding<ActivityMergeAdapterBinding>(R.layout.activity_merge_adapter)
    val viewModel by viewModels<TestVM>()

    @Inject
    lateinit var teacherAdapter: TeacherMergeAdapter

    @Inject
    @ManType
    lateinit var man: Human

    @Inject
    @WomanType
    lateinit var woman: Human

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("---->", "man:$man + woman:$woman")
//        val teacherAdapter = TeacherMergeAdapter()
        val studentAdapter = StudentMergeAdapter()
        val footerAdapter = FooterMergeAdapter()

        val mergeAdapter =
            ConcatAdapter(teacherAdapter, footerAdapter, studentAdapter, footerAdapter)
        binding.recyclerView.adapter = mergeAdapter
        val students = listOf(
            "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12",
            "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12", "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12", "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12", "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12", "student1", "student2", "student3",
            "student4", "student5", "student6",
            "student7", "student8", "student9",
            "student10", "student11", "student12"
        )
        val studentList = ArrayList<MergeVo.StudentVo>()
        students.forEach {
            studentList.add(MergeVo.StudentVo(it))
        }
        val teachers = listOf(
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12",
            "teacher1", "teacher2", "teacher3",
            "teacher4", "teacher5", "teacher6",
            "teacher7", "teacher8", "teacher9",
            "teacher10", "teacher11", "teacher12"
        )
        val teacherList = ArrayList<MergeVo.TeacherVo>()
        teachers.forEach {
            teacherList.add(MergeVo.TeacherVo(it))
        }
        val footerList = ArrayList<MergeVo.FooterVo>()
        footerList.add(MergeVo.FooterVo("footer"))

        teacherAdapter.submitList(teacherList)
        studentAdapter.submitList(studentList)
        footerAdapter.submitList(footerList)

        binding.toolbar.setNavigationOnClickListener {
            //            DownLoadManagerExt.download(
//                context = this,
//                url = ApiService.DOWN_WX,
//                downloadName = "weixin7014android1660.apk",
//                isWIFI = true,
//                description = ""
//            )

//            mViewModel.banner()
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

@Qualifier
annotation class ManType

@Qualifier
annotation class WomanType

interface Human {
    fun sex(): String
}

class Man @Inject constructor() : Human {
    override fun sex(): String {
        return "男"
    }
}

class Woman @Inject constructor() : Human {
    override fun sex(): String {
        return "女"
    }
}

@Module
@InstallIn(ActivityComponent::class)
object HumanModule {

    @ManType
    @Provides
    fun providerMan(man: Man): Human {
        return man
    }

    @WomanType
    @Provides
    fun providerWoman(woman: Woman): Human {
        return woman
    }
}