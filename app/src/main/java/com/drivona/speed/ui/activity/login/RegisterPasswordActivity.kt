package com.drivona.speed.ui.activity.login

import android.text.InputType
import com.drake.net.utils.scopeNetLife
import com.drivona.speed.api.getPublicKey
import com.drivona.speed.api.loginByEmail
import com.drivona.speed.api.sign
import com.drivona.speed.databinding.ActivityRegisterPasswordBinding
import com.drivona.speed.utils.RSAUtil
import com.hjq.widget.view.afterTextChanged
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentString
import com.lalifa.extension.invisible
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.text
import com.lalifa.extension.visible
import com.lalifa.utils.SPUtil


class RegisterPasswordActivity : BaseActivity<ActivityRegisterPasswordBinding>() {


    override fun getViewBinding() = ActivityRegisterPasswordBinding.inflate(layoutInflater)
    var email = ""
    var code = ""
    override fun initView() {
        email = getIntentString("email")
        code = getIntentString("code")
        binding.apply {
            etPassCode1.afterTextChanged {
                next.isSelected = it.length >= 4 && etPassCode.text().length >= 4
            }
            etPassCode.afterTextChanged {
                next.isSelected = it.length >= 4 && etPassCode1.text().length >= 4
            }
        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }
            icEyeNew1.onClick {
                icEyeNew1.isSelected = !icEyeNew1.isSelected
                if (icEyeNew1.isSelected) {
                    etPassCode1.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etPassCode1.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
            icEyeNew.onClick {
                icEyeNew.isSelected = !icEyeNew.isSelected
                if (icEyeNew.isSelected) {
                    etPassCode.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etPassCode.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
            next.onClick {
                if (next.isSelected) {
                    var pass = etPassCode.text()
                    var pas1 = etPassCode1.text()
                    if (pass != pas1) {
                        tvError.visible()
                        etPassCode1.isSelected = true
                        return@onClick
                    }
                    tvError.invisible()
                    etPassCode1.isSelected = false

                    scopeNetLife {
                        sign(email, code, pass)
                        val dataKey = getPublicKey()
                        val key = dataKey.data!!
                        val entity= RSAUtil.encrypt(pass,key)
                        val data = loginByEmail(email, entity)
                        SPUtil.set(Tools.IS_LOGIN, true)
                        SPUtil.set(Tools.Token, data.data)
                        InitNet.initNetHttp(this@RegisterPasswordActivity, data.data.pk())
//                        start<RegisterPasswordActivity> { }
                        start<RegisterSetNameActivity> { }
                    }

                }


            }


        }
    }


}
