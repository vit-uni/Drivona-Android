package com.drivona.speed.ui.activity.login

import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityRegisterSuccessBinding
import com.drivona.speed.ui.activity.AddDeviceActivity


class RegisterSuccessActivity : BaseActivity<ActivityRegisterSuccessBinding>() {


    override fun getViewBinding() = ActivityRegisterSuccessBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            tvConnect.onClick {
                ActivityManager.getInstance().finishAllActivities()
                start<AddDeviceActivity> { }


            }


        }
    }


}