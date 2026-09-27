package com.drivona.speed.ui.activity.login

import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityModifyPasswordSuccessBinding
import com.drivona.speed.ui.SplashLoginActivity
import com.lalifa.tools.UserManager
import com.lalifa.utils.SPUtil


class ModifyPasswordSuccessActivity : BaseActivity<ActivityModifyPasswordSuccessBinding>() {


    override fun getViewBinding() = ActivityModifyPasswordSuccessBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            next.onClick {

                SPUtil.set(Tools.IS_LOGIN, false)
                SPUtil.set(Tools.Token, "")
                UserManager.logout()
                ActivityManager.getInstance().finishAllActivities()
                InitNet.initNetHttp(this@ModifyPasswordSuccessActivity, "")
                start <SplashLoginActivity>{  }
            }


        }
    }

    override fun onBackPressed() {
        SPUtil.set(Tools.IS_LOGIN, false)
        SPUtil.set(Tools.Token, "")
        UserManager.logout()
        ActivityManager.getInstance().finishAllActivities()
        InitNet.initNetHttp(this@ModifyPasswordSuccessActivity, "")
        start <SplashLoginActivity>{  }

    }


}