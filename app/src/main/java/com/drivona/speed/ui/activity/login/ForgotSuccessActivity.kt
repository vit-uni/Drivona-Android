package com.drivona.speed.ui.activity.login

import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityRegisterSuccessBinding
import com.drivona.speed.databinding.ActivityResetSuccessBinding


class ForgotSuccessActivity : BaseActivity<ActivityResetSuccessBinding>() {


    override fun getViewBinding() = ActivityResetSuccessBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            next.onClick {
                ActivityManager.getInstance().finishAllActivities()
                start<LoginActivity> { }
                finish()

            }


        }
    }

    override fun onBackPressed() {
        ActivityManager.getInstance().finishAllActivities()
        start<LoginActivity> { }
        finish()

    }

}