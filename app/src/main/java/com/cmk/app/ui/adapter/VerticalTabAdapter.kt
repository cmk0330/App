package com.cmk.app.ui.adapter

import com.cmk.app.vo.WxTabVo
import q.rorbin.verticaltablayout.adapter.TabAdapter
import q.rorbin.verticaltablayout.widget.ITabView

/**
 * @Author: romens
 * @Date: 2019-12-2 10:15
 * @Desc:
 */
class VerticalTabAdapter(private val titles: List<WxTabVo>) : TabAdapter {
    override fun getIcon(position: Int): ITabView.TabIcon? = null

    override fun getBadge(position: Int): ITabView.TabBadge? =  null

    override fun getBackground(position: Int): Int  = -1

    override fun getTitle(position: Int): ITabView.TabTitle {
        return ITabView.TabTitle.Builder()
            .setContent(titles[position].name)
            .setTextColor(-0xc94365, -0x8a8a8b)
            .build()
    }

    override fun getCount(): Int  = titles.size
}