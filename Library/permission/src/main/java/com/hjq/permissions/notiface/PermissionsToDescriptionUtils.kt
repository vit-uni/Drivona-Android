package com.hjq.permissions.notiface

import android.content.Context
import com.hjq.permissions.Permission
import com.hjq.permissions.R

class PermissionsToDescriptionUtils {

    companion object {
        fun permissionsToDescription(permissionName: String,context : Context): String {
            return when(permissionName) {
                Permission.CAMERA -> context.getString(R.string.to_take_photos_and_record_videos)

                Permission.READ_MEDIA_IMAGES, Permission.READ_MEDIA_VIDEO -> context.getString(R.string.to_select_images_to_change_your_profile_picture)

                Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE -> "用于APP写入/下载/保存图片/读取图片、文件等信息"

                Permission.RECORD_AUDIO -> "用于录音上传介绍/上麦说话功能"

                Permission.READ_MEDIA_AUDIO -> "用于选择录音上传介绍/上麦说话功能"

                Permission.SYSTEM_ALERT_WINDOW -> "用于房间最小化，切换到后台后快速返回房间"
                else -> "用于 xxx 业务"
            }
        }
    }

}