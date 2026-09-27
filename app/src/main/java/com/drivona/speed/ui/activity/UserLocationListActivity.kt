package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.fragmentAdapter
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.onClick
import com.lalifa.extension.pageChangedListener
import com.drivona.speed.api.DeviceData
import com.drivona.speed.databinding.ActivityUserLocationBinding
import com.drivona.speed.ui.fragment.UserLocationCompanyFragment
import com.drivona.speed.ui.fragment.UserLocationFavoriteFragment
import com.drivona.speed.ui.fragment.UserLocationHomeFragment


class UserLocationListActivity : BaseActivity<ActivityUserLocationBinding>() {


    override fun getViewBinding() = ActivityUserLocationBinding.inflate(layoutInflater)
    var device: DeviceData?= null
    override fun initView() {
        val indx = getIntentInt("index", 0)
        var   typeFrom = getIntentInt("type", 0)
        if (typeFrom == 1) {
            device = getIntentSerializable<DeviceData>("device")
        }
        binding.apply {
            viewPager.fragmentAdapter(supportFragmentManager) {
                add(UserLocationHomeFragment.newInstance(typeFrom,device))
                add(UserLocationCompanyFragment.newInstance(typeFrom,device))
                add(UserLocationFavoriteFragment.newInstance(typeFrom,device))


            }.pageChangedListener {
                clHome.isSelected = it == 0
                clCompany.isSelected = it == 1
                clFavorite.isSelected = it == 2
            }
            viewPager.currentItem = indx
            clHome.isSelected = indx == 0
            clCompany.isSelected = indx == 1
            clFavorite.isSelected = indx == 2
            ivBack.onClick {
                finish()
            }
            clHome.onClick {
                viewPager.currentItem = 0
            }
            clCompany.onClick {
                viewPager.currentItem = 1
            }
            clFavorite.onClick {
                viewPager.currentItem = 2
            }
        }
    }


    override fun onClick() {
        binding.apply {


        }
    }


}