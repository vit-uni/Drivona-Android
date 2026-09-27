package com.drivona.speed.ui.activity.help

import com.drake.channel.receiveTag
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentString
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityHelpFirstBinding
import com.drivona.speed.ui.MainNewActivity
import com.lalifa.extension.getIntentBoolean
import com.lalifa.utils.SPUtil


class HelpFirstActivity : BaseActivity<ActivityHelpFirstBinding>() {


    override fun getViewBinding() = ActivityHelpFirstBinding.inflate(layoutInflater)

    override fun initView() {
        receiveTag("finishActivity","finishActivity1") {
            finish()
        }
    }

    override fun onClick() {
        val type = getIntentString("type")
        val isSet = getIntentBoolean("isSet")
        binding.apply {
            tvSkip.onClick {

                SPUtil.set(Tools.isHaveOpen, true)
                if (!isSet){
                    start<MainNewActivity> { }
                }

                finish()
            }
            tvConnect.onClick {
                if (type == "02") {
                    start<HelpSa02FirstActivity> {
                        putExtra("isSet",isSet)
                    }
                } else {
                    start<HelpSa01FirstActivity> {
                        putExtra("isSet",isSet)
                    }
                }

            }


        }
    }


}