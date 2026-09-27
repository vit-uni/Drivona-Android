package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.drivona.speed.databinding.ActivityContactUsBinding
import com.drivona.speed.databinding.ActivityProductSpecificationBinding
import com.drivona.speed.databinding.ActivityUserFeedbackBinding


class ContactUsActivity : BaseActivity<ActivityContactUsBinding>() {


    override fun getViewBinding() = ActivityContactUsBinding.inflate(layoutInflater)

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