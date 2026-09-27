package com.drivona.speed.ui

import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.blankj.utilcode.util.ActivityUtils
import com.drake.channel.receiveTag
import com.drake.net.utils.scopeNetLife
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.extension.toast
import com.lalifa.extension.uiTask
import com.drivona.speed.api.getEquipmentList
import com.drivona.speed.databinding.ActivitySplashBinding
import com.drivona.speed.ext.start
import com.drivona.speed.ui.activity.AddDeviceActivity
import com.drivona.speed.ui.activity.login.LoginActivity
import com.drivona.speed.utils.LanguageUtil
import com.lalifa.utils.SPUtil
import java.util.Locale

class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    override fun getViewBinding() = ActivitySplashBinding.inflate(layoutInflater)

    override fun initView() {
        if (intent.flags and Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT != 0) {
            finish()
            return
        }


        receiveTag("HttpLogout"){
            SPUtil.set(Tools.IS_LOGIN, false)
            SPUtil.set(Tools.Token, "")
            scopeNetLife {

                val intent = Intent(this@SplashActivity, SplashLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
        }
        checkPermissions()
        val language = SPUtil.get("language")
        val nanme = language.ifEmpty {
            if (isSystemGerman()) {
                "de"
            } else {
                "en"
            }
        }
        val resources: Resources = getResources()
        val configuration = resources.configuration

        val locale = LanguageUtil.getLocaleByLanguage(nanme)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }
    }

    fun checkPermissions() {


        XXPermissions.with(this)
            // 申请单个权限
            .permission(Permission.ACCESS_COARSE_LOCATION)
            .permission(Permission.ACCESS_FINE_LOCATION)
            .permission(Permission.BLUETOOTH_SCAN)
            .permission(Permission.BLUETOOTH_ADVERTISE)
            .permission(Permission.BLUETOOTH_CONNECT)
            // 设置权限请求拦截器（局部设置）
            //.interceptor(new PermissionInterceptor())
            // 设置不触发错误检测机制（局部设置）
            .unchecked()
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {

                    uiTask(1000) {
                        SPUtil.set(Tools.isHaveOpen, true)
                        if (SPUtil.getBoolean(Tools.IS_LOGIN, false)) {
                            scopeNetLife {
                                val data = getEquipmentList()
                                if (data?.data.isNullOrEmpty()) {
                                    start<AddDeviceActivity> { }
                                } else {
                                    start<MainNewActivity> { }
                                }
                                finish()
                            }

                        } else {
                            start<SplashLoginActivity> {}
                            finish()
                        }

                    }

                }

                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    if (never) {
//                        toast("被永久拒绝授权，请手动授予权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                        XXPermissions.startPermissionActivity(this@SplashActivity, permissions)
                        uiTask(1000) {
                            if (SPUtil.getBoolean(Tools.IS_LOGIN, false)) {
                                scopeNetLife {
                                    val data = getEquipmentList()
                                    if (data?.data.isNullOrEmpty()) {
                                        start<AddDeviceActivity> { }
                                    } else {
                                        start<MainNewActivity> { }
                                    }
                                    finish()
                                }
                            } else {
                                start<SplashLoginActivity> {}
                                finish()
                            }

                        }
                    } else {
                        toast("获取权限失败")
                    }
                }
            })
    }

    fun isSystemGerman(): Boolean {
        val systemLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        // language 只拿2字母语言码，de=德语，不管地区DE/AT/CH
        return systemLocale.language == "de"
    }
}