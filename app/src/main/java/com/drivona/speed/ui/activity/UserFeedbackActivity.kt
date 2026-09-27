package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.drivona.speed.databinding.ActivityUserFeedbackBinding


class UserFeedbackActivity : BaseActivity<ActivityUserFeedbackBinding>() {


    override fun getViewBinding() = ActivityUserFeedbackBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {

            ivBack.onClick {
                finish()
            }


        }
    }

    override fun onClick() {
        binding.apply {


        }
    }


}