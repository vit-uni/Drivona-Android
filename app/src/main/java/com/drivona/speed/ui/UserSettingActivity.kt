package com.drivona.speed.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.LocaleList
import com.drake.channel.receiveTag
import com.drake.channel.sendEvent
import com.drake.net.utils.scopeNetLife
import com.drivona.speed.R
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.Avatar_Path
import com.lalifa.ext.Tools.Companion.BASE_URL
import com.lalifa.extension.gone
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.visible
import com.drivona.speed.api.CMDMsg
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.downloadFile
import com.drivona.speed.api.getUserinfo
import com.drivona.speed.api.userEdit
import com.drivona.speed.databinding.ActivityUserSettingBinding
import com.drivona.speed.ext.showExitConfirmDialog
import com.drivona.speed.ext.showExitDialog
import com.drivona.speed.ui.activity.help.HelpFirstActivity
import com.drivona.speed.ui.activity.login.EditSetNameActivity
import com.drivona.speed.ui.activity.login.FeedBackActivity
import com.drivona.speed.ui.activity.login.ModifyEmailPreActivity
import com.drivona.speed.ui.activity.login.ModifyPasswordActivity
import com.drivona.speed.ui.adapter.deviceInfoList
import com.lalifa.activity.WebActivity
import com.lalifa.tools.UserManager
import com.lalifa.utils.SPUtil
import java.util.Locale


class UserSettingActivity : BaseActivity<ActivityUserSettingBinding>() {

    override fun getViewBinding() = ActivityUserSettingBinding.inflate(layoutInflater)
    var avatarPath = ""
    override fun initView() {


        binding.apply {
            tvAccount.isSelected = true
            tvTitleSystem.isSelected = false
            clUser.visible()
            clSystem.gone()
            recInfo.deviceInfoList().apply {
                models = arrayListOf(0, 1)
                onFastClick(R.id.iv_0){
                    if (modelPosition==0){
                        start<HelpFirstActivity> {
                            putExtra("type", "01")
                            putExtra("isSet",true)
                        }
                    }else{
                        start<HelpFirstActivity> {
                            putExtra("type", "02")
                            putExtra("isSet",true)

                        }
                    }
                }
            }
            tvKm.isSelected = true
            var isMPH = SPUtil.getBoolean(Tools.isMPH, false)
            tvKms.isSelected = !isMPH
            tvMph.isSelected = isMPH
        }
        receiveTag("changeLanguage") {
            finish()
        }

    }

    override fun onClick() {
        super.onClick()

        binding.apply {
            ivAvatar.onClick {
                start<EditSetNameActivity> {
                    putExtra("path", avatarPath)
                }
            }
            tvUserName.onClick {
                start<EditSetNameActivity> {
                    putExtra("path", avatarPath)
                }
            }
            cl0.onClick {
                start<FeedBackActivity> { }
            }
            cl1.onClick {
                start<ModifyEmailPreActivity> { }
            }
            cl2.onClick {
                start<ModifyPasswordActivity> { }
            }
            clPp.onClick {
                start <WebActivity>{
                    putExtra("title",  getString(R.string.privacy_policy))
                    putExtra("url", "${Tools.HOST}privacy-policy.html")
//                    putExtra("url", "https://online.vit-uni.com/terms-of-service.html")
                }
//                start<ModifyPasswordActivity> { }
            }
            ivBack.onClick {
                finish()
            }
            ivExit.onClick {
                showExitConfirmDialog {
                    SPUtil.set(Tools.IS_LOGIN, false)
                    SPUtil.set(Tools.Token, "")
                    UserManager.logout()
                    ActivityManager.getInstance().finishAllActivities()
                    InitNet.initNetHttp(this@UserSettingActivity, "")
                    start<SplashLoginActivity> { }
                }

            }
            tvAccount.onClick {
                tvAccount.isSelected = true
                tvTitleSystem.isSelected = false
                clUser.visible()
                clSystem.gone()
            }
            tvTitleSystem.onClick {
                tvTitleSystem.isSelected = true
                tvAccount.isSelected = false
                clUser.gone()
                clSystem.visible()
            }
            tvKm.onClick {
                tvKm.isSelected = true
                tvMi.isSelected = false
                val cmd = CMDMsg("55 AA 04 00 02 05 02 00")
                sendEvent(cmd)
                scopeNetLife {
                    userEdit(distance = "km")
                }

            }
            tvMi.onClick {
                tvKm.isSelected = false
                tvMi.isSelected = true
                val cmd = CMDMsg("55 AA 04 00 02 05 02 01")
                sendEvent(cmd)
                scopeNetLife {
                    userEdit(distance = "mi")

                }
            }
            tvKms.onClick {
                tvKms.isSelected = true
                tvMph.isSelected = false
                val cmd = CMDMsg("55 AA 04 00 03 05 02 00")
                sendEvent(cmd)
                SPUtil.set(Tools.isMPH, false)
                scopeNetLife {
                    userEdit(speed = "km/h")
                }
            }
            tvMph.onClick {
                tvKms.isSelected = false
                tvMph.isSelected = true
                val cmd = CMDMsg("55 AA 04 00 03 05 02 01")
                sendEvent(cmd)
                SPUtil.set(Tools.isMPH, true)
                scopeNetLife {
                    userEdit(speed = "mph")
                }
            }

            clLanguage.onClick {
                start<LanguageActivity> { }
            }
            clUpdate.onClick {
                val packageName = "com.drivona.speed" // 目标应用包名
//                val packageName = "com.google.android.apps.maps" // 目标应用包名
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("market://details?id=$packageName")
                        setPackage("com.android.vending") // 指定用Google Play打开
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    // 设备没有Google Play，跳网页版
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                    startActivity(intent)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val data = getUserinfo()
            data.data?.apply {
                binding.apply {
                    avatarPath = avatar
                    ivAvatar.load(avatar)
                    tvUserName.text = nickname
                    tvEmail.text = email
                    tvDeviceEmail.text = email
                    tvKm.isSelected = distance.isNullOrEmpty() ||  distance != "mi"
                    tvMi.isSelected = distance == "mi"
                    tvKms.isSelected = speed.isNullOrEmpty() || speed != "mph"
                    tvMph.isSelected = speed == "mph"

                }
                UserInfoManager.save(this)
                downloadFile(this@UserSettingActivity, "${id}.png", BASE_URL + avatar) {
                    SPUtil.set(Avatar_Path, it.absolutePath)

                }

            }
        }
        val language = SPUtil.get("language")
        val name = language.ifEmpty {
            if (isSystemGerman()) {
                "de"
            } else {
                "en"
            }
        }

        if (name== "en") {
            binding.tvLanguage.text = "English"
        } else {
            when (name) {
                "de" -> binding.tvLanguage.text = "Deutsch"
                "fr" -> binding.tvLanguage.text = "Français"
                "it" -> binding.tvLanguage.text = "Italiano"
                "es" -> binding.tvLanguage.text = "Español"
            }
        }
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