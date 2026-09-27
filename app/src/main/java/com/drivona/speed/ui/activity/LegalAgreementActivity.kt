package com.drivona.speed.ui.activity

import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.drivona.speed.databinding.ActivityLegalAgreementBinding


class LegalAgreementActivity : BaseActivity<ActivityLegalAgreementBinding>() {


    override fun getViewBinding() = ActivityLegalAgreementBinding.inflate(layoutInflater)

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