package com.drivona.speed.ui.activity.login

import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityRegisterChoosePictureBinding
import com.drivona.speed.databinding.ActivityRegisterSetNameBinding


class RegisterChoosePictureActivity : BaseActivity<ActivityRegisterChoosePictureBinding>() {


    override fun getViewBinding() = ActivityRegisterChoosePictureBinding.inflate(layoutInflater)
    var email = ""
    override fun initView() {
        email = getIntentString("email")
        binding.apply {

        }
    }

    override fun onClick() {
        binding.apply {

            next.onClick {
                start<RegisterSuccessActivity> { }

            }


        }
    }


}
