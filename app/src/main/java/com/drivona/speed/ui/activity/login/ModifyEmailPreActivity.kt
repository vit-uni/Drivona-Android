package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeNetLife
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.extension.onClick
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.loginOff
import com.drivona.speed.databinding.ActivityModifyEmailPreBinding
import com.drivona.speed.ext.showEmailDeleteConfirmDialog
import com.drivona.speed.ext.start
import com.drivona.speed.ui.SplashLoginActivity


class ModifyEmailPreActivity : BaseActivity<ActivityModifyEmailPreBinding>() {


    override fun getViewBinding() = ActivityModifyEmailPreBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            tvEmail.text = UserInfoManager.get()!!.email
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }
            tvDelete.onClick {
                showEmailDeleteConfirmDialog {
                    scopeNetLife {
                        loginOff()
                        UserInfoManager.logout()
                        ActivityManager.getInstance().finishAllActivities()
                        start<SplashLoginActivity> { }
                    }
                }
            }
            tvUpdate.onClick {
                start <ModifyEmailActivity>{  }
            }


        }
    }


}