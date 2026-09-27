package com.drivona.speed.ui.activity.help

import com.drake.channel.receiveTag
import com.drake.channel.sendTag
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityHelpSa02FirstBinding
import com.drivona.speed.ui.MainNewActivity
import com.lalifa.extension.getIntentBoolean
import com.lalifa.utils.SPUtil


class HelpSa02FirstActivity : BaseActivity<ActivityHelpSa02FirstBinding>() {


    override fun getViewBinding() = ActivityHelpSa02FirstBinding.inflate(layoutInflater)

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
                start<HelpSa02SecondActivity> { putExtra("isSet",isSet)}
            }
            tvPre.onClick {
                finish()
            }


        }
    }


}