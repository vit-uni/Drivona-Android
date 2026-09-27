package com.drivona.speed.ui.activity

import androidx.core.view.isVisible
import com.drivona.speed.databinding.ActivityNavigationSettingBinding
import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick


class NavigationSettingActivity : BaseActivity<ActivityNavigationSettingBinding>() {


    override fun getViewBinding() = ActivityNavigationSettingBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

            ivBack.onClick {
                finish()
            }
            tvEn.isSelected = true
            ivEn.isVisible = true
            tvZh.onClick {
                tvZh.isSelected = true
                ivZh.isVisible = true
                tvEn.isSelected = false
                ivEn.isVisible = false
                tvDeutsh.isSelected = false
                ivDeutsh.isVisible = false
            }
            tvEn.onClick {
                tvZh.isSelected = false
                ivZh.isVisible = false
                tvEn.isSelected = true
                ivEn.isVisible = true
                tvDeutsh.isSelected = false
                ivDeutsh.isVisible = false
            }
            tvDeutsh.onClick {
                tvZh.isSelected = false
                ivZh.isVisible = false
                tvEn.isSelected = false
                ivEn.isVisible = false
                tvDeutsh.isSelected = true
                ivDeutsh.isVisible = true
            }

        }
    }

    override fun onClick() {
        binding.apply {


        }
    }


}