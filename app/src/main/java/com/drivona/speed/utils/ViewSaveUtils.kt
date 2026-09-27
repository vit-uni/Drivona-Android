package com.drivona.speed.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream

class ViewSaveUtils {

    /**
     * 保存View到本地相册
     * @param context 上下文
     * @param view 要保存的View
     * @param fileName 文件名
     * @param format 图片格式
     * @param quality 图片质量
     * @param listener 保存成功回调
     */
    fun saveViewToGallery(
        context: Context,
        view: Bitmap,
        fileName: String,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 100,
        listener: ((Uri?) -> Unit)? = null
    ) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/${format.name.lowercase()}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }
        val contentResolver: ContentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            saveBitmapToUri(context, uri, view, format, quality)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(uri, contentValues, null, null)
            }
            listener?.invoke(uri)
            // 通知相册更新
            MediaScannerConnection.scanFile(context, arrayOf(File(uri.path!!).absolutePath), null, null)
        }
    }

    /**
     * 将Bitmap保存到Uri
     * @param context 上下文
     * @param uri 图片Uri
     * @param bitmap 要保存的Bitmap
     * @param format 图片格式
     * @param quality 图片质量
     */
    private fun saveBitmapToUri(
        context: Context,
        uri: Uri,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ) {
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(uri)
            outputStream?.let { bitmap.compress(format, quality, it) }
            outputStream?.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream?.close()
        }
    }

    /**
     * 将View转换为Bitmap
     * @param view 要转换的View
     * @param width View的宽度
     * @param height View的高度
     * @param backgroundColor 背景色
     * @return 转换后的Bitmap
     */
    fun viewToBitmap(view: Drawable, width: Int, height: Int, backgroundColor: Int = Color.WHITE): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)
        view.setBounds(0, 0, width, height)
        view.draw(canvas)
        return bitmap
    }
}