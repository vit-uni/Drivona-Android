package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityPairingDeviceSuccessBinding
import com.drivona.speed.ui.MainNewActivity


class PairingSuccessDeviceActivity : BaseActivity<ActivityPairingDeviceSuccessBinding>() {


    override fun getViewBinding() = ActivityPairingDeviceSuccessBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            tvDone.onClick {
                start<MainNewActivity> { }
            }
        }
    }

    override fun onClick() {
        binding.apply {


        }
    }


}