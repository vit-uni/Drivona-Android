package com.drivona.speed.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.blankj.utilcode.util.LogUtils
import com.lalifa.base.BaseApplication
import com.lalifa.extension.pk
import com.ypx.imagepicker.bean.ImageItem

class TakePhotoPathUtils {
    companion object {
        fun getPath(path: ImageItem, isCrop: Boolean): String {
            return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                // Android 13及更高版本，使用MediaStore API
                return getPathFromUri(path.uri).pk()
            } else {
                // Android 12及更低版本，使用标准的文件I/O方式
                return if (isCrop) path.cropUrl else path.path
            }
        }

        private fun getPathFromUri(uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? =
                BaseApplication.get().contentResolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val imagePath = cursor.getString(columnIndex)
                cursor.close()
                return imagePath
            }
            return uri.path // 如果无法通过 cursor 获取，直接返回 Uri 的路径
        }
    }

}