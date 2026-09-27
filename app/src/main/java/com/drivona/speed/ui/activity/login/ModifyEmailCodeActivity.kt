package com.drivona.speed.ui.activity.login

import com.drake.net.utils.scopeNetLife
import com.hjq.widget.view.afterTextChanged
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentString
import com.lalifa.extension.globalUITask
import com.lalifa.extension.onClick
import com.lalifa.extension.showKeyboard
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.visible
import com.drivona.speed.api.changeMail
import com.drivona.speed.api.checkMail
import com.drivona.speed.api.sendMail
import com.drivona.speed.databinding.ActivityModifyEmailCodeBinding


class ModifyEmailCodeActivity : BaseActivity<ActivityModifyEmailCodeBinding>() {


    override fun getViewBinding() = ActivityModifyEmailCodeBinding.inflate(layoutInflater)
    var email = ""
    override fun initView() {
        email = getIntentString("email")
        binding.apply {
            tvCodeDesc.start()
            etCode.afterTextChanged {
                next.isSelected = it.length >= 4
            }
            showKeyboard(etCode)

        }
    }

    override fun onClick() {
        binding.apply {
            tvCodeDesc.onClick {
                scopeNetLife {
                    sendMail(email,"change")
                    tvCodeDesc.start()
                }
            }
            next.onClick {
                if (next.isSelected) {

                    var code = etCode.text()
                    scopeNetLife {
                        val data = checkMail(email, code)

                        data.data?.apply {
                            if (this == 0) {
                                globalUITask {
                                    etCode.setError()
                                    tvErrorDesc.visible()
                                }

                                return@scopeNetLife
                            }
                             changeMail(email, code)
                            start<ModifyEmailSuccessActivity> {

                            }
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
