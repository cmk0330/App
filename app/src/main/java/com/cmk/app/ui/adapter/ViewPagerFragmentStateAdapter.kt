package com.cmk.app.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * @Author: romens
 * @Date: 2019-11-8 14:02
 * @Desc:
 */
class ViewPagerFragmentStateAdapter(
    var fragmentManage: FragmentManager,
    var lifecycle: Lifecycle,
    var fragments: List<Fragment>
): FragmentStateAdapter(fragmentManage, lifecycle) {

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}