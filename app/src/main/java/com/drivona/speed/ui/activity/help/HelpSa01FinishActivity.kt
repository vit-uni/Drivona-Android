package com.drivona.speed.ui.activity.help

import com.drake.channel.receiveTag
import com.drake.channel.sendTag
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityHelpFirstBinding
import com.drivona.speed.databinding.ActivityHelpSa01FinishBinding
import com.drivona.speed.databinding.ActivityHelpSa01FirstBinding
import com.drivona.speed.ui.MainNewActivity
import com.lalifa.extension.getIntentBoolean
import com.lalifa.utils.SPUtil


class HelpSa01FinishActivity : BaseActivity<ActivityHelpSa01FinishBinding>() {


    override fun getViewBinding() = ActivityHelpSa01FinishBinding.inflate(layoutInflater)

    override fun initView() {
        receiveTag("finishActivity","finishActivity1") {
            finish()
        }
    }

    override fun onClick() {
        val isSet = getIntentBoolean("isSet")
        binding.apply {
            tvSkip.onClick {

                SPUtil.set(Tools.isHaveOpen, true)
                if (!isSet){
                    start<MainNewActivity> { }
                    sendTag("finishActivity")
                }else{
                    sendTag("finishActivity1")
                }

            }
            tvConnect.onClick {
                SPUtil.set(Tools.isHaveOpen, true)
                if (!isSet){
                    start<MainNewActivity> { }
                    sendTag("finishActivity")
                }else{
                    sendTag("finishActivity1")
                }

            }


        }
    }


}