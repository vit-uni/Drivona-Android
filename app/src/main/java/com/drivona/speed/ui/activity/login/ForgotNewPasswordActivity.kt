package com.drivona.speed.ui.activity.login

import android.text.InputType
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.toast
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.invisible
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.visible
import com.drivona.speed.api.forget
import com.drivona.speed.databinding.ActivityForgotPasswordNewBinding

/**
 * 忘记密码
 */
class ForgotNewPasswordActivity : BaseActivity<ActivityForgotPasswordNewBinding>() {


    override fun getViewBinding() = ActivityForgotPasswordNewBinding.inflate(layoutInflater)
    var email = ""
    var code = ""
    override fun initView() {
        email = getIntentString("email")
        code = getIntentString("code")
        binding.apply {
            etNew.afterTextChanged {
                next.isSelected =
                    it.toString().length > 0 && etNewConfirm.text.toString().length >= 4

            }
            etNewConfirm.afterTextChanged {
                next.isSelected =
                    it.toString().length >= 4 && etNew.text.toString().length > 0
            }
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }
            icEyeNew.onClick {
                icEyeNew.isSelected = !icEyeNew.isSelected
                if (icEyeNew.isSelected) {
                    etNew.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etNew.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
            icEyeConfirm.onClick {
                icEyeConfirm.isSelected = !icEyeConfirm.isSelected
                if (icEyeConfirm.isSelected) {
                    etNewConfirm.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etNewConfirm.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
            next.onClick {
                if (next.isSelected) {
                    if (etNew.text() != etNewConfirm.text()) {

                        etNewConfirm.isSelected = true
                        tvError.visible()
                        return@onClick
                    }
                    etNewConfirm.isSelected = false
                    tvError.invisible()
                    scopeNetLife {
                        forget(email, code, etNew.text())
                        start<ForgotSuccessActivity> { }
                    }

                }

            }

        }
    }


}