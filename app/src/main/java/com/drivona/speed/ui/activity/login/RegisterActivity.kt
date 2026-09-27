package com.drivona.speed.ui.activity.login

import android.graphics.Color
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.R
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.isValidEmail
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.drivona.speed.api.sendMail
import com.drivona.speed.databinding.ActivityRegisterBinding
import com.drivona.speed.ext.showAgreeConfirmDialog
import com.drivona.speed.ext.showEmailDialog
import com.lalifa.activity.WebActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.appendSpan


class RegisterActivity : BaseActivity<ActivityRegisterBinding>() {


    override fun getViewBinding() = ActivityRegisterBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            etPhone.afterTextChanged {
                next.isSelected = it.length > 0 && ivAgree.isSelected
            }
        }
    }

    override fun onClick() {
        binding.apply {
            agreeBtn.apply {
                text = context.getString(R.string.i_agree_to_the)
                append("  ")
                appendSpan(
                    context.getString(R.string.user_agreement),
                    color = Color.parseColor("#ED5700")
                ) {
                    start(WebActivity::class.java) {
                        putExtra("title", context.getString(R.string.user_agreement))
                        putExtra("url", "${Tools.HOST}terms-of-service.html")
                    }

                }
                append("  ")
                append(context.getString(R.string.and))
                append("  ")
                appendSpan(
                    context.getString(R.string.privacy_policy),
                    color = Color.parseColor("#ED5700")
                ) {
                    start<WebActivity> {
                        putExtra("title", context.getString(R.string.privacy_policy))
                        putExtra("url", "${Tools.HOST}privacy-policy.html")
                    }
                }

            }
            ivAgree.onClick {
                it.isSelected = !it.isSelected
                next.isSelected = etPhone.text().pk().length > 0 && ivAgree.isSelected
            }
            ivBack.onClick {
                finish()
            }
            next.onClick {
                if (!ivAgree.isSelected) {
                    showAgreeConfirmDialog {
                        ivAgree.isSelected = true
                        next.isSelected = etPhone.text().pk().length > 0 && ivAgree.isSelected
                    }
                    return@onClick
                }
                if (next.isSelected) {
                    scopeDialog(BubbleDialog(this@RegisterActivity, "")) {
                        val email = etPhone.text()
                        if (!email.isValidEmail()) {
                            showEmailDialog { }
                            return@scopeDialog
                        }

                        sendMail(email)
                        start<RegisterCodeActivity> {
                            putExtra("email", email)
                        }
                    }

                }


            }


        }
    }


}