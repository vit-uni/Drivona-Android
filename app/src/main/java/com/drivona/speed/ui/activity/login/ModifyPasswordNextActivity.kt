package com.drivona.speed.ui.activity.login

import android.text.InputType
import com.drake.channel.receiveTag
import com.drake.net.utils.scopeNetLife
import com.hjq.widget.view.afterTextChanged
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentString
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.toast
import com.drivona.speed.api.changePwd
import com.drivona.speed.databinding.ActivityModifyPasswordNextBinding
import com.lalifa.utils.SPUtil


class ModifyPasswordNextActivity : BaseActivity<ActivityModifyPasswordNextBinding>() {


    override fun getViewBinding() = ActivityModifyPasswordNextBinding.inflate(layoutInflater)
    var old = ""
    override fun initView() {
        old=  getIntentString("old")
        binding.apply {
            ivBack.onClick {
                finish()
            }
            icEyeNext.onClick {
                icEyeNext.isSelected = !icEyeNext.isSelected
                if (icEyeNext.isSelected) {
                    etPhone.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etPhone.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }

            etPhone.afterTextChanged {
                next.isSelected = it.length > 5
            }

        }
        receiveTag("finishActivity") {
            finish()
        }
    }

    override fun onClick() {
        binding.apply {

            next.onClick {
                if (next.isSelected) {
                    val pa = etPhone.text()
                    scopeNetLife {
                        val data = changePwd(old,pa,"2")

                        start<ModifyPasswordSuccessActivity> { }
                    }

                }


            }


        }
    }


}