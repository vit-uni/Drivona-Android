package com.drivona.speed.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

object LocationPermissionUtil {

    /**
     * 判断定位权限是否【始终允许】
     * @return true=始终允许；false=仅使用时允许 / 拒绝
     */
    fun isLocationAlwaysAllow(context: Context): Boolean {
        // 是否拥有前台定位权限（精确/粗略二选一）
        val hasForeground = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasForeground) {
            return false
        }

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android10+：必须后台定位权限也授予，才是始终允许
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android9及以下：拿到前台权限=始终允许
            true
        }
    }

    /**
     * 是否有前台定位权限（仅使用期间允许也算true）
     */
    fun isLocationForegroundGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
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