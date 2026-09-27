package com.drivona.speed.ui.activity.map

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import cn.com.heaton.blelibrary.ble.Ble
import com.hjq.permissions.XXPermissions
import com.lalifa.base.BaseActivity
import com.lalifa.extension.getIntentInt
import com.lalifa.extension.getIntentSerializable
import com.lalifa.extension.gone
import com.lalifa.extension.onClick
import com.lalifa.extension.start
import com.lalifa.extension.visible
import com.drivona.speed.api.BleRssiDevice
import com.drivona.speed.api.DeviceData
import com.drivona.speed.databinding.ActivityMapMainBinding
import com.drivona.speed.ui.activity.SearchDeviceActivity
import com.drivona.speed.ui.activity.SearchLocationActivity


class MapMainActivity : BaseActivity<ActivityMapMainBinding>() {


    override fun getViewBinding() = ActivityMapMainBinding.inflate(layoutInflater)
    var device: DeviceData? = null
    var type = 0
    override fun initView() {
        device = getIntentSerializable<DeviceData>("device")
        type = getIntentInt("type", -1)
    }


    override fun onClick() {
        binding.apply {
            ivBack.onClick {
                finish()
            }
            tvSearch.onClick {
                start<SearchLocationActivity> {
                    putExtra("device", device)
                    putExtra("type", 1)
                }
            }
            tvConnect.onClick {
                if (device == null) {
                    start<SearchDeviceActivity> { }
                }
            }
            tvBle.onClick {
                if (!isBleGranted) {
                    jumpToAppPermissionSetting(this@MapMainActivity)
                    return@onClick
                }
                openBluetoothSettings()
            }
            tvLocation.onClick {
                if (!isLocationGranted) {
                    jumpToAppPermissionSetting(this@MapMainActivity)
                    return@onClick
                }
                jumpLocationSourceSetting(this@MapMainActivity)
            }


        }
    }

    var isBleGranted = false
    var isLocationGranted = false
    override fun onResume() {
        super.onResume()
        binding.apply {
            clBle.gone()
            clConnect.gone()
            clLocation.gone()
            clSearch.gone()
            val isEnable = Ble.getInstance<BleRssiDevice>().isBleEnable
// 需要检测的权限，举例如存储、相机、麦克风
            val needBle = listOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_ADVERTISE,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
            val needLocation = listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,

                )

// 方式1：只检查是否已经拥有权限（不弹窗申请）
            isBleGranted = XXPermissions.isGranted(this@MapMainActivity, needBle)
            // 无权限，可以发起申请
            if (!isBleGranted || !isEnable) {
                // 权限全部通过
                clBle.visible()
                return@apply
            }
            isLocationGranted = XXPermissions.isGranted(this@MapMainActivity, needLocation)
            // 无权限，可以发起申请
            if (!isLocationGranted || !isGpsOpen(this@MapMainActivity)) {
                // 权限全部通过
                clLocation.visible()
                return@apply
            }
            if (type == 1) {
                finish()
                return@apply
            }
            if (device == null) {
                clConnect.visible()
                return@apply
            }
            clSearch.visible()

        }


    }

    private fun openBluetoothSettings() {
        try {
            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
            startActivity(intent)
        } catch (e: Exception) {
            // 极少数情况系统找不到该页面（如定制ROM修改）
            // 可兜底跳转到设置主页
            startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    /**
     * 判断系统位置(GPS)开关是否开启
     */
    fun isGpsOpen(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 满足任意一种定位模式开启：高精度/省电/仅设备(GPS)
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

    /**
     * 跳转到系统定位开关设置页
     */
    fun jumpLocationSourceSetting(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            // 校验是否有Activity可以处理该Intent，防止ROM阉割页面崩溃
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                // 兜底：跳系统设置首页
                val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallback)
            }
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallback)
        }
    }
}