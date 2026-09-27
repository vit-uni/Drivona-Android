package com.drivona.speed.ui.activity.login

import android.graphics.Color
import android.text.InputType
import com.drake.net.utils.scopeDialog
import com.drake.net.utils.scopeNetLife
import com.drake.tooltip.dialog.BubbleDialog
import com.drivona.speed.R
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.api.getPublicKey
import com.drivona.speed.api.loginByEmail
import com.drivona.speed.databinding.ActivityLoginPassBinding
import com.drivona.speed.ext.showAgreeConfirmDialog
import com.drivona.speed.ui.MainNewActivity
import com.drivona.speed.ui.activity.AddDeviceActivity
import com.drivona.speed.utils.RSAUtil
import com.hjq.widget.view.afterTextChanged
import com.lalifa.activity.WebActivity
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.extension.appendSpan
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.visible
import com.lalifa.utils.SPUtil


class LoginActivity : BaseActivity<ActivityLoginPassBinding>() {


    override fun getViewBinding() = ActivityLoginPassBinding.inflate(layoutInflater)
    var key = ""
    override fun initView() {
        scopeNetLife {
            val data = getPublicKey()
            key = data.data.toString()
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
            tvCreate.onClick {
                start<RegisterActivity> { }
            }
            ivAgree.onClick {
                it.isSelected = !it.isSelected
                login.isSelected =
                    etCode.text.toString().length >= 1 && etPhone.text.toString().length > 0
                            && ivAgree.isSelected
            }
            icEyeNew.onClick {
                icEyeNew.isSelected = !icEyeNew.isSelected
                if (icEyeNew.isSelected) {
                    etCode.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    etCode.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }
            }
            etPhone.afterTextChanged {
                login.isSelected =
                    it.toString().length > 0 && etCode.text.toString().length >= 1
                            && ivAgree.isSelected
            }
            etCode.afterTextChanged {
                login.isSelected =
                    it.toString().length >= 1 && etPhone.text.toString().length > 0
                            && ivAgree.isSelected
            }
            login.onClick {

                val phone = etPhone.text.toString()
                val pass = etCode.text.toString()
                if (!ivAgree.isSelected) {
                    showAgreeConfirmDialog {
                        ivAgree.isSelected = true
                        login.isSelected =
                            it.toString().length >= 1 && etPhone.text.toString().length > 0
                                    && ivAgree.isSelected
                    }
                    return@onClick
                }
                if (phone.isEmpty()) {

                    return@onClick
                }

                if (pass.isEmpty()) {

                    return@onClick
                }
                scopeDialog(BubbleDialog(this@LoginActivity, "")) {

                    val entity= RSAUtil.encrypt(pass,key)
                    val data = loginByEmail(phone, entity)
                    if (data.code == 0) {
                        etCode.isSelected = true
                        tvError.visible()
                        etCode.setTextColor(getColor(R.color.color_tv_error))
                        return@scopeDialog
                    } else {
                        etCode.isSelected = false
                        tvError.gone()
                        etCode.setTextColor(getColor(R.color.color_login_input))
                        //                    SPUtil.set(Tools.IS_LOGIN, true)


                        SPUtil.set(Tools.IS_LOGIN, true)
                        SPUtil.set(Tools.Token, data.data)
                        InitNet.initNetHttp(this@LoginActivity, data.data.pk())
                        scopeNetLife {
                            val data = getEquipmentList()
                            ActivityManager.getInstance().finishAllActivities()
                            if (data?.data.isNullOrEmpty()) {
                                start<AddDeviceActivity> { }
                            } else {
                                start<MainNewActivity> { }
                            }
                            finish()
                        }

                    }

                }


//                scopeNetLife {
//                    val loginMake = loginMake(phone, pass)
//                    if (loginMake.code == 200) {
//                        loginMake.data?.let {
//                            InitNet.initNetHttp(this@LoginActivity, it.token.pk(""))
//                            if (it.user?.birthday.isNullOrEmpty()) {
////                                start(OnDayUniqueIdentityActivity::class.java) {
////                                    putExtra("bean", it)
////                                }
//                            } else {
//                                UserManager.save(it)
//                                SPUtil.set(Tools.IS_LOGIN, true)
//                                InitNet.initNetHttp(this@LoginActivity, it?.token.pk())
//                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//                    } else {
//                        ToastUtils.showShort(loginMake.message)
//                    }
//
////                    SPUtil.set(Tools.IS_LOGIN, true)
////                    val intent = Intent(this@LoginPassActivity, MainActivity::class.java)
////                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
////                    startActivity(intent)
////                    finish()
//                }

            }
            forgotPassword.onClick {
                start(ForgotPasswordActivity::class.java)
            }

        }
    }


}