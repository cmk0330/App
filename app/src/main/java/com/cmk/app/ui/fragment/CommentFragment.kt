package com.cmk.app.ui.fragment

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cmk.app.R
import com.cmk.app.databinding.FragmentBottomCommentBinding
import com.cmk.app.ui.adapter.CommentAdapter
import com.cmk.app.util.windowManager
import com.cmk.app.viewmodel.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CommentFragment : BottomSheetDialogFragment() {

    private var _binding:FragmentBottomCommentBinding?=null
    private val binding get() = _binding!!
    private lateinit var commentAdapter: CommentAdapter
    private val viewModel by viewModels<CommentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.editModel(false)
        binding.include.model = CommentViewModel.EditModel(false)
//        binding.include.model = viewModel.editModelLiveData.value
        commentAdapter = CommentAdapter()
        binding.recyclerView.adapter = commentAdapter

        binding.include.tvComment.setOnClickListener {
            binding.include.model = CommentViewModel.EditModel(true)
            binding.include.etWriteComment.isFocusable = true
            binding.include.etWriteComment.isFocusableInTouchMode = true
            binding.include.etWriteComment.requestFocus()
            binding.include.etWriteComment.findFocus()
//            keyWord(binding.include.etWriteComment)
        }
    }

    override fun onStart() {
        super.onStart()
        val mDialog = dialog as BottomSheetDialog
        val bottomSheet =
            mDialog.delegate.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
//            it.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.peekHeight = getHeight()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            viewModel.loadComment()
            binding.ivClose.setOnClickListener {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        viewModel.commentLiveData.observe(this, Observer {
            commentAdapter.submitList(it)
        })
    }

    private fun getHeight(): Int {
        val windowManager = context?.windowManager
        val metrics = DisplayMetrics()
        var height = metrics.heightPixels
        windowManager?.defaultDisplay?.getMetrics(metrics)
        val point = Point();
        // 使用Point已经减去了状态栏高度
        windowManager?.defaultDisplay?.getSize(point);
        height = point.y - getTopOffset();
        return height;
    }

    private fun getTopOffset(): Int {
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    private fun keyWord(view: View) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }


    /**
     * 软件盘隐藏||显示的监听
     */
    fun Activity.setOnSoftKeyBoardListener(listener: OnSoftKeyBoardChangeListener) {
        //activity的根视图
        val rootView = window.decorView
        //纪录根视图的显示高度
        var rootViewVisibleHeight = 0
        //监听视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            //获取当前根视图在屏幕上显示的大小
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val visibleHeight = rect.height()
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return@addOnGlobalLayoutListener
            }
            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (rootViewVisibleHeight - visibleHeight > 200) {
                listener.keyBoardShow()
                rootViewVisibleHeight = visibleHeight
                return@addOnGlobalLayoutListener
            }
            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            listener.keyBoardHide()
            rootViewVisibleHeight = visibleHeight
            return@addOnGlobalLayoutListener
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnSoftKeyBoardChangeListener {
        fun keyBoardShow()

        fun keyBoardHide()
    }
}