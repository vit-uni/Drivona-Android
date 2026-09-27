package com.drivona.speed.ui.activity.login

import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.databinding.ActivityModifyEmailSuccessBinding
import com.drivona.speed.ui.SplashLoginActivity


class ModifyEmailSuccessActivity : BaseActivity<ActivityModifyEmailSuccessBinding>() {


    override fun getViewBinding() = ActivityModifyEmailSuccessBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            next.onClick {
                UserInfoManager.logout()
                ActivityManager.getInstance().finishAllActivities()
                start<SplashLoginActivity> { }

            }


        }
    }


}