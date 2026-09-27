package com.drivona.speed.ui.activity

import android.content.Intent
import android.util.Log
import com.drake.channel.receiveTag
import com.drake.net.utils.scopeNetLife
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lalifa.base.BaseActivity
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.Avatar_Path
import com.lalifa.ext.Tools.Companion.BASE_URL
import com.lalifa.extension.load
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.toJson
import com.lalifa.extension.toast
import com.drivona.speed.api.DeviceInfo
import com.drivona.speed.api.UserInfoManager
import com.drivona.speed.api.downloadFile
import com.drivona.speed.api.getUserinfo
import com.drivona.speed.databinding.ActivityAddDeviceBinding
import com.drivona.speed.ui.UserSettingActivity
import com.drivona.speed.ui.activity.login.LoginActivity
import com.drivona.speed.ui.activity.map.MapMainActivity
import com.lalifa.utils.SPUtil


class AddDeviceActivity : BaseActivity<ActivityAddDeviceBinding>() {

    val needBle = listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT
    )
    val needLocation = listOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,

        )

    override fun getViewBinding() = ActivityAddDeviceBinding.inflate(layoutInflater)
    val list = arrayListOf<DeviceInfo>()

    override fun initView() {
        receiveTag("finishActivity") {
            finish()
        }
        receiveTag("HttpLogout") {
            SPUtil.set(Tools.IS_LOGIN, false)
            SPUtil.set(Tools.Token, "")
            scopeNetLife {

                val intent = Intent(this@AddDeviceActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
        }
//        if (json.isNotEmpty()) {
//            val listData = GsonUtil.json2List<DeviceInfo>(json, DeviceInfo::class.java)
//            list.addAll(listData)
//        }
//        ble.startScan(scanCallback)
        binding.apply {
            tvConnect.onClick {


                XXPermissions.with(this@AddDeviceActivity)
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

                            start<SearchDeviceActivity> { }

                        }

                        override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                            Log.e("permissions", permissions.toJson())
                            start<MapMainActivity> {
                                putExtra("type", 1)
                            }

                        }
                    })
            }


        }
    }

    override fun onClick() {
        binding.apply {
            ivBack.onClick {
//                finish()
                start<MapMainActivity> {

                }
            }
            ivAvatar.onClick {
                start<UserSettingActivity> { }
            }


        }
    }

    override fun onResume() {
        super.onResume()
        scopeNetLife {
            val data = getUserinfo()
            data.data?.apply {
                binding.apply {
                    ivAvatar.load(avatar)
                }
                downloadFile(this@AddDeviceActivity, "${id}.png", BASE_URL + avatar) {
                    SPUtil.set(Avatar_Path, it.absolutePath)

                }
                UserInfoManager.save(this)


            }
        }
    }


}