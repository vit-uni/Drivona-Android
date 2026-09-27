package com.drivona.speed.ui

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.drivona.speed.databinding.ActivitySplashLoginBinding
import com.drivona.speed.ext.start
import com.drivona.speed.ui.activity.login.LoginActivity
import com.drivona.speed.ui.activity.login.RegisterActivity

class SplashLoginActivity : BaseActivity<ActivitySplashLoginBinding>() {
    override fun getViewBinding() = ActivitySplashLoginBinding.inflate(layoutInflater)

    override fun initView() {


        binding.tvCreate.onClick {
            start<RegisterActivity> { }
        }
        binding.tvLogin.onClick {
            start<LoginActivity> { }
        }
//        }
    }

}