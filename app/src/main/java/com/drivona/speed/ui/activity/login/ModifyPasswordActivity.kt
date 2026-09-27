package com.drivona.speed.ui.activity.login

import android.text.InputType
import com.drake.channel.receiveTag
import com.drake.net.utils.scopeNetLife
import com.drivona.speed.api.changePwd
import com.drivona.speed.databinding.ActivityModifyPasswordBinding
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.start


class ModifyPasswordActivity : BaseActivity<ActivityModifyPasswordBinding>() {


    override fun getViewBinding() = ActivityModifyPasswordBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            etPhone.afterTextChanged {
                next.isSelected = it.length > 6
            }
        }
    }

    override fun onClick() {
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

            next.onClick {
                val phone = etPhone.text.toString()

                if (phone.isEmpty()) {

                    return@onClick
                }
                scopeNetLife {
                    val data = changePwd(phone)
                    start<ModifyPasswordNextActivity> {
                        putExtra("old",phone)
                    }
                }


            }


        }
        receiveTag("finishActivity") {
            finish()
        }
    }


}