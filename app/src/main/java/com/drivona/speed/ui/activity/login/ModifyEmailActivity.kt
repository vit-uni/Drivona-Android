package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeNetLife
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.isValidEmail
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.drivona.speed.api.sendMail
import com.drivona.speed.databinding.ActivityModifyEmailBinding
import com.drivona.speed.ext.showEmailDialog


class ModifyEmailActivity : BaseActivity<ActivityModifyEmailBinding>() {


    override fun getViewBinding() = ActivityModifyEmailBinding.inflate(layoutInflater)

    override fun initView() {

        binding.apply {
            etPhone.afterTextChanged {
                next.isSelected = it.length > 0
            }
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }

            next.onClick {
                scopeNetLife {
                    val email = etPhone.text()
                    if (!email.isValidEmail()) {
                        showEmailDialog { }
                        return@scopeNetLife
                    }

                    sendMail(email,"change")
                    start<ModifyEmailCodeActivity> {
                        putExtra("email", email)
                    }
                }



            }


        }
    }


}