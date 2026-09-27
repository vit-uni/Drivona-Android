package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.api.checkMail
import com.drivona.speed.api.sendMail
import com.drivona.speed.databinding.ActivityForgotCodeBinding
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.globalUITask
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.visible


class ForgotCodeActivity : BaseActivity<ActivityForgotCodeBinding>() {


    override fun getViewBinding() = ActivityForgotCodeBinding.inflate(layoutInflater)
    var email = ""
    override fun initView() {
        email = getIntentString("email")
        binding.apply {
            tvCodeDesc.start()
            etCode.afterTextChanged {
                next.isSelected = it.length >= 4
            }
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }
            tvCodeDesc.onClick {

                scopeDialog(BubbleDialog(this@ForgotCodeActivity, "")) {
                    sendMail(email, "forget")
                    tvCodeDesc.start()
                }
            }
            next.onClick {


                if (next.isSelected) {
                    val code = etCode.text()
                    scopeNetLife {
                        val data = checkMail(email, code, "2")
                        data.data?.apply {
                            if (this == 0) {
                                globalUITask {
                                    etCode.setError()
                                    tvError.visible()
                                }

                                return@scopeNetLife
                            }
                            start<ForgotNewPasswordActivity> {
                                putExtra("email", email)
                                putExtra("code", code)
                            }
                        }

                    }

                }


            }


        }
    }


}