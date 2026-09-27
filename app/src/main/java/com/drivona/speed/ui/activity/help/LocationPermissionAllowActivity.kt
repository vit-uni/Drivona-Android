package com.drivona.speed.ui.activity.help

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.drake.channel.receiveTag
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.extension.getIntentString
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.drivona.speed.databinding.ActivityHelpFirstBinding
import com.drivona.speed.databinding.ActivityLocationPermissionAllowBinding
import com.drivona.speed.databinding.ActivityLocationPermissionBinding
import com.drivona.speed.ui.MainNewActivity
import com.lalifa.utils.SPUtil


class LocationPermissionAllowActivity : BaseActivity<ActivityLocationPermissionAllowBinding>() {


    override fun getViewBinding() = ActivityLocationPermissionAllowBinding.inflate(layoutInflater)

    override fun initView() {
        receiveTag("finishActivity") {
            finish()
        }
    }

    override fun onClick() {
        binding.apply {

            tvConnect.onClick {

                jumpToAppPermissionSetting(this@LocationPermissionAllowActivity)
            }


        }
    }


    fun jumpToAppPermissionSetting(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                // 指定当前app包名
                data = Uri.fromParts("package", context.packageName, null)
                // 非Activity上下文必须加NEW_TASK，否则崩溃
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // 兜底：打开系统设置首页
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }
}