package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityPairingDeviceFailedBinding
import com.drivona.speed.ui.MainNewActivity


class PairingFailedDeviceActivity : BaseActivity<ActivityPairingDeviceFailedBinding>() {


    override fun getViewBinding() = ActivityPairingDeviceFailedBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            tvExit.onClick {
                start<MainNewActivity> { }
            }
        }
    }

    override fun onClick() {
        binding.apply {


        }
    }


}