package com.cmk.app.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cmk.app.MainActivity
import com.cmk.app.R
import com.cmk.app.base.App
import com.cmk.app.base.BaseFragment
import com.cmk.app.databinding.FragmentMineBinding
import com.cmk.app.ext.dp2px
import com.cmk.app.ext.getScreenHeight
import com.cmk.app.ui.activity.LoginActivity
import com.cmk.app.ui.activity.ShowPhotoDBActivity
import com.cmk.app.ui.activity.TestActivity
import com.cmk.app.ui.adapter.CommentAdapter
import com.cmk.app.ui.adapter.LoadMoreAdapter
import com.cmk.app.ui.adapter.MineAdapter
import com.cmk.app.viewmodel.LoginViewModel
import com.cmk.app.viewmodel.Theme
import com.cmk.app.widget.recyclerview.itemPadding1
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

/**
 * @Author: romens
 * @Date: 2019-11-8 9:20
 * @Desc:
 */
class MineFragment : BaseFragment(),
    NavigationView.OnNavigationItemSelectedListener {

    val flags =
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    private var _binding: FragmentMineBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<LoginViewModel>()
    private val mAdapter by lazy { MineAdapter() }
    private var changeU: Boolean = false
    private var commentAdapter: CommentAdapter? = null
    private lateinit var commentDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        context?.actionBar?.hide()
//        val decorView =  activity?.window?.decorView
//        decorView?.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        activity?.window?.statusBarColor = Color.TRANSPARENT
//        activity?.window?.decorView?.systemUiVisibility = flags
//        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val bitmapUser = BitmapFactory.decodeResource(resources, R.mipmap.ic_user_photo_bg) // 效率高
//
//        Glide.with(this).load(bitmapUser)
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//            .into(binding.include.ivBackground)

//        binding.ctl.setContentScrimResource(R.mipmap.ic_user_photo)

        val context = requireContext()
        val layoutManager = GridLayoutManager(context, 2)
        binding.recyclerview.layoutManager = layoutManager
//        binding.recyclerview.itemPadding(context.dp2px(8), 0, context)
        binding.recyclerview.isNestedScrollingEnabled = false
        binding.recyclerview.itemPadding1(context.dp2px(8))
        val loadMoreAdapter = LoadMoreAdapter()
        binding.recyclerview.adapter = mAdapter

//        commentDialog()
//        val commentFragment = CommentFragment()
//        binding.toolbar.setNavigationOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.include.ivArrow.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }
        binding.include.tvUserName.setOnClickListener {
            viewModel.getUserInfo()
        }

        mAdapter.setCommentClickListener {
//            loadComment()
            CommentFragment().show(childFragmentManager, "dialog")
        }

        lifecycleScope.launchWhenCreated {

//            viewModel.loadDynamic().collectLatest {
//                mAdapter.submitData(it)
//            }
        }
//        val viewX = binding.toolbar.getChildAt(0).paddingStart
//        val viewY = binding.toolbar.getChildAt(0).paddingTop
//        val logoX = binding.ivUserPhoto.paddingStart
//        val logoY = binding.ivUserPhoto.paddingTop
//
//        Log.e("viewX-->","$viewX")
//        Log.e("viewY-->","$viewY")
//        Log.e("logoX-->","$logoX")
//        Log.e("logoY-->","$logoY") 22:10

//        binding.appbarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
//           binding.ivUserPhoto.translationX = viewX.toFloat()
//            binding.ivUserPhoto.translationY = viewY.toFloat()
//            Log.e("verticalOffset-->", "${-verticalOffset}")
//            if (-verticalOffset>0) {
//                val blur = EasyBlur.with(context)
//                    .bitmap(bitmapUser)
//                    .radius( -verticalOffset / 16 )
//                    .scale(4)
//                    .blur()
//                binding.ivPhotoBg.setImageBitmap(blur)
//            }
//            Glide.with(this).load(blur).into(binding.ivPhotoBg)
//        })


//        binding.ctl.setContentScrimResource(R.mipmap.ic_user_photo)
//        binding.ctl.scrimVisibleHeightTrigger
    }

    override fun onResume() {
        super.onResume()
        loginState()
        binding.include.ivUserLogo.setOnClickListener {
            Log.e("App-isLogin---", "${App.isLogin}")
            startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    private fun loginState() {
        Log.e("App-isLogin", "${App.isLogin}")
        if (!App.isLogin) {
            Glide.with(this).load(R.drawable.ic_login_not)
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                .into(binding.include.ivUserLogo)
            binding.include.ivUserLogo.isEnabled = true
        } else {
            Glide.with(this).load(R.mipmap.ic_user_photo)
                .apply(RequestOptions.bitmapTransform(CropCircleTransformation()))
                .into(binding.include.ivUserLogo)
            binding.include.ivUserLogo.isEnabled = false
            binding.include.tvUserName.text = App.CURRENT_USER.nickname
//            viewModel.getDynamic()
        }
    }

    private fun commentDialog() {
        val view = View.inflate(context, R.layout.fragment_bottom_comment, null)
        commentDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        commentDialog.setContentView(view)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val tvCommentCount = view.findViewById<TextView>(R.id.tv_comment_count)
        val ivSort = view.findViewById<ImageView>(R.id.iv_sort)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val behavior = BottomSheetBehavior.from(view.parent as View)
        val height = requireContext().getScreenHeight()
//        behavior.peekHeight = height * 3 / 4

        commentAdapter = CommentAdapter()
        recyclerView.adapter = commentAdapter

        val list: MutableList<String> = ArrayList()
        for (index in 0..20) {
            list.add("这是第 $index 条评论")
        }
//        commentAdapter?.submitList(list)

        ivClose.setOnClickListener { commentDialog.dismiss() }
    }

//    fun addSpringBackDisLimit(targetLimitH: Int) {
//        if (coordinator == null) return
//        // totalHeight 屏幕的总像素高度
//        val totalHeight = context!!.resources.displayMetrics.heightPixels
//        // currentH 当前我们的 列表控件 展开的高度
//        val currentH =
//            (totalHeight.toFloat() * 0.618).toInt() // 0.618 是黄金分割点，随便自定义，对应 contentView
//        val leftH = totalHeight - currentH
//        coordinator.setOnTouchListener(
//            object : OnTouchListener() {
//                fun onTouch(v: View?, event: MotionEvent): Boolean {
//                    when (event.action) {
//                        MotionEvent.ACTION_MOVE ->                             // 计算相对于屏幕的 坐标
//                            bottomSheet.getGlobalVisibleRect(r)
//                        MotionEvent.ACTION_UP -> {
//                            // 抬手的时候判断
//                            val limitH: Int
//                            limitH = if (targetLimitH < 0) leftH + currentH / 3 else targetLimitH
//                            if (r.top <= limitH) if (mBehavior != null) // 范围内，让它继续是 半展开的状态
//                                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
//                        }
//                    }
//                    return false
//                }
//            }
//        )
//    }


    override suspend fun subscribe() {
        super.subscribe()

        viewModel.loginState.collect {
            loginState()
        }


//        viewModel.loadDynamic().observe(viewLifecycleOwner, Observer {
//            mAdapter.submitData(lifecycle,it)
//        })

//        viewModel.getDynamic()
        viewModel.dynamicLiveData.observe(this, Observer {
            mAdapter.submitList(it)
        })
    }

    private fun changeSystemUI(visible: Boolean) {
        var newVis = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//        activity?.actionBar.visibility = View.VISIBLE
        if (!visible) {
            newVis =
                newVis or (ImageView.SYSTEM_UI_FLAG_LOW_PROFILE or ImageView.SYSTEM_UI_FLAG_FULLSCREEN
                        or ImageView.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//            llActionBar.visibility = View.GONE
        }
        val decorView = activity?.window?.decorView
        decorView?.systemUiVisibility = newVis
    }


    @SuppressLint("RestrictedApi")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_dark -> (activity as MainActivity).themeViewModel.setCurrentTheme(Theme.DARK)
            R.id.item_collect_article -> Toast.makeText(context, "收藏文章", Toast.LENGTH_SHORT).show()
            R.id.item_collect_net -> Toast.makeText(context, "收藏的网站", Toast.LENGTH_SHORT).show()
            R.id.item_share_article -> Toast.makeText(context, "分享的文章", Toast.LENGTH_SHORT).show()
            R.id.item_show_photo -> startActivity(Intent(context, ShowPhotoDBActivity::class.java))
            R.id.item_test -> startActivity(Intent(context, TestActivity::class.java))
            R.id.item_share_project -> startActivity(
                Intent(
                    context,
//                    ConsumeRecordActivity::class.java
//                    TestCoroutineActivity::class.java
//                    NestActivity::class.java
                    TestActivity::class.java
                )
            )
            R.id.item_exit -> {
                MaterialDialog(requireContext()).show {
                    cornerRadius(literalDp = 8F)
                    title(text = "退出")
                    message(text = "是否退出")
                    positiveButton(text = "是") {
                        viewModel.loginOut()
                    }
                    negativeButton(text = "否")
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
