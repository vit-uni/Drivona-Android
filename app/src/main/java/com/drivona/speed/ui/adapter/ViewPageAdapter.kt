package com.drivona.speed.ui.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.drivona.speed.ui.fragment.DeviceFragment

@SuppressLint("WrongConstant")
class ViewPageAdapter (
    fm: FragmentManager,
    private val fragments: MutableList<DeviceFragment>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // 当前总页数 = 集合大小
    override fun getCount(): Int = fragments.size

    // 根据position获取Fragment
    override fun getItem(position: Int): Fragment = fragments[position]

    // 关键：刷新时告知ViewPager页面已变更
    override fun getItemPosition(obj: Any): Int {
        // POSITION_NONE：标记该Fragment已被移除，系统销毁重建
        return POSITION_NONE
    }
}