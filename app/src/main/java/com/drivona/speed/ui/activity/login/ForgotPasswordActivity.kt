package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.isValidEmail
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.drivona.speed.api.ImageCodeBean
import com.drivona.speed.api.sendMail
import com.drivona.speed.databinding.ActivityForgotPasswordBinding
import com.drivona.speed.ext.showEmailDialog

/**
 * 忘记密码
 */
class ForgotPasswordActivity : BaseActivity<ActivityForgotPasswordBinding>() {


    override fun getViewBinding() = ActivityForgotPasswordBinding.inflate(layoutInflater)

    override fun initView() {
        binding.apply {
            etPhone.afterTextChanged {
                next.isSelected = it.length > 0
            }
        }
    }

    override fun onClick() {
        binding.apply {
            next.onClick {
                if (next.isSelected) {
                    val email = etPhone.text()
                    if (!email.isValidEmail()) {
                        showEmailDialog { }
                        return@onClick
                    }
                    scopeDialog(BubbleDialog(this@ForgotPasswordActivity, "")) {
                        sendMail(email, "forget")
                        start<ForgotCodeActivity> {
                            putExtra("email", email)
                        }
                    }

                }

            }
            ivBack.onClick {
                finish()
            }
        }
    }


}