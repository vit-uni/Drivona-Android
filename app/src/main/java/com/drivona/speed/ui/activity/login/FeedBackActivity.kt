package com.drivona.speed.ui.activity.login

import com.drake.channel.receiveTag
import com.drake.net.utils.scopeNetLife
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.isValidEmail
import com.lalifa.extension.onClick
import com.lalifa.extension.text
import com.drivona.speed.api.feedback
import com.drivona.speed.databinding.ActivityFeedBackBinding
import com.drivona.speed.ext.showEmailDialog


class FeedBackActivity : BaseActivity<ActivityFeedBackBinding>() {


    override fun getViewBinding() = ActivityFeedBackBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            etEmail.afterTextChanged {
                next.isSelected =
                    etEmail.text().isNotEmpty() && etName.text().isNotEmpty() && etContent.text()
                        .isNotEmpty()
            }
            etName.afterTextChanged {
                next.isSelected =
                    etEmail.text().isNotEmpty() && etName.text().isNotEmpty() && etContent.text()
                        .isNotEmpty()
            }
            etContent.afterTextChanged {
                next.isSelected =
                    etEmail.text().isNotEmpty() && etName.text().isNotEmpty() && etContent.text()
                        .isNotEmpty()
            }
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }


            next.onClick {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val content = etContent.text.toString()

                if (name.isEmpty()) {
                    return@onClick
                }
                if (email.isEmpty()) {
                    return@onClick
                }
                if (!email.isValidEmail()) {
                    showEmailDialog { }
                    return@onClick
                }
                if (content.isEmpty()) {
                    return@onClick
                }

                scopeNetLife {
                    feedback(name, email, content)
                    finish()
                }

            }


        }
        receiveTag("finishActivity") {
            finish()
        }
    }


}