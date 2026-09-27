package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.globalUITask
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityPairingDeviceBinding


class PairingDeviceActivity : BaseActivity<ActivityPairingDeviceBinding>() {


    override fun getViewBinding() = ActivityPairingDeviceBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            globalUITask(2000) {
                start<PairingFailedDeviceActivity> { }
                finish()
            }
        }
    }

    override fun onClick() {
        binding.apply {

            ivBack.onClick { finish() }
        }
    }


}