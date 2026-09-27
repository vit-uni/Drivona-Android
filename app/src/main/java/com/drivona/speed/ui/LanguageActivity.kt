package com.drivona.speed.ui

import android.os.Build
import android.os.LocaleList
import com.drake.channel.sendTag
import com.drivona.speed.databinding.ActivityLanguageBinding
import com.drivona.speed.utils.LanguageUtil
import com.lalifa.api.InitNet
import com.lalifa.base.BaseActivity
import com.lalifa.extension.onClick
import com.lalifa.extension.pk
import com.lalifa.extension.start
import com.lalifa.utils.SPUtil
import java.util.Locale
import kotlin.text.isEmpty


class LanguageActivity : BaseActivity<ActivityLanguageBinding>() {

    override fun getViewBinding() = ActivityLanguageBinding.inflate(layoutInflater)

    override fun initView() {


        binding.apply {
            val language = SPUtil.get("language")
            val name = language.ifEmpty {
                if (isSystemGerman()) {
                    "de"
                } else {
                    "en"
                }
            }
            if (name == "en") {
                clEnglish.isSelected = true
            } else {
                when (name) {
                    "de" -> clDeutsch.isSelected = true
                    "fr" -> clFrance.isSelected = true
                    "it" -> clItaly.isSelected = true
                    "es" -> clSpain.isSelected = true
                }
            }
            ivBack.onClick {
                finish()
            }

            clEnglish.onClick {
                clEnglish.isSelected = true
                clDeutsch.isSelected = false
                clFrance.isSelected = false
                clItaly.isSelected = false
                clSpain.isSelected = false
                showSaveLanguage("en")
            }
            clDeutsch.onClick {
                clEnglish.isSelected = false
                clDeutsch.isSelected = true
                clFrance.isSelected = false
                clItaly.isSelected = false
                clSpain.isSelected = false
                showSaveLanguage("de")
            }
            clFrance.onClick {
                clEnglish.isSelected = false
                clDeutsch.isSelected = false
                clFrance.isSelected = true
                clItaly.isSelected = false
                clSpain.isSelected = false
                showSaveLanguage("fr")
            }
            clItaly.onClick {
                clEnglish.isSelected = false
                clDeutsch.isSelected = false
                clFrance.isSelected = false
                clItaly.isSelected = true
                clSpain.isSelected = false
                showSaveLanguage("it")
            }
            clSpain.onClick {
                clEnglish.isSelected = false
                clDeutsch.isSelected = false
                clFrance.isSelected = false
                clItaly.isSelected = false
                clSpain.isSelected = true
                showSaveLanguage("es")
            }
        }


    }

    override fun onClick() {
        super.onClick()


    }

    /**
     * 保存设置的语言
     */
    private fun showSaveLanguage(language: String) {
        //设置的语言、重启的类一般为应用主入口（微信也是到首页）
        LanguageUtil.changeAppLanguage(this, language, MainNewActivity::class.java)
        //保存设置的语言
        SPUtil.set("language", language)
        InitNet.initNetHttp(this,"")
        sendTag("changeLanguage")
        SPUtil.set("isFirst", false)
//        start<MainNewActivity> {
//            putExtra("isFirst", false)
//        }
        finish()
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