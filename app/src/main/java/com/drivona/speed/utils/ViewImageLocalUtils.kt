package com.drivona.speed.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ViewImageLocalUtils {
    /**
     * 将View保存到本地相册
     * @param view 需要保存的View
     * @param context 上下文对象
     * @param fileName 文件名
     * @param quality 图片质量，0-100
     * @param format 图片格式，默认为PNG
     * @return 保存成功返回true，保存失败返回false
     */
    fun saveViewToGallery(
        view: View,
        context: Context,
        fileName: String,
        quality: Int = 100,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): Boolean {
        // 创建Bitmap对象
        val bitmap = createBitmapFromView(view)

        // 保存Bitmap到本地相册
        return saveBitmapToGallery(bitmap, context, fileName, quality, format)
    }

    /**
     * 将Bitmap保存到本地相册
     * @param bitmap 需要保存的Bitmap
     * @param context 上下文对象
     * @param fileName 文件名
     * @param quality 图片质量，0-100
     * @param format 图片格式，默认为PNG
     * @return 保存成功返回true，保存失败返回false
     */
    fun saveBitmapToGallery(
        bitmap: Bitmap,
        context: Context,
        fileName: String,
        quality: Int = 100,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    ): Boolean {
        // 获取保存图片的路径
        val savePath = getSavePath(context, fileName)

        // 保存图片
        val file = File(savePath)
        if (!file.exists()) {
            file.createNewFile()
        }
        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(format, quality, outputStream)
            outputStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            outputStream?.close()
        }

        // 通知相册更新
        notifyGalleryUpdate(context, savePath)

        return true
    }

    /**
     * 将Bitmap保存到本地相册
     * @param bitmap 需要保存的Bitmap
     * @param context 上下文对象
     * @param fileName 文件名
     * @param quality 图片质量，0-100
     * @param format 图片格式，默认为PNG
     * @return 保存成功返回true，保存失败返回false
     */
    fun saveBitmapToGallery(
        view: View,
        context: Context
    ): Boolean {
        val fileName = "${System.currentTimeMillis()}my_image"
        val path = "/sdcard/pictures"
        val width = view.width
        val height = view.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val file = File(path, "${fileName}.jpg")
        try {
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            canvas.setBitmap(null)
            bitmap.recycle()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            bitmap.recycle()
        }

        // 通知相册更新
        notifyGalleryUpdate(context, file.path)
        return true
    }

    /**
     * 创建Bitmap对象
     * @param view 需要创建Bitmap的View
     * @return Bitmap对象
     */
    private fun createBitmapFromView(view: View): Bitmap {
        // 创建Bitmap对象
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 绘制View
        view.draw(canvas)

        return bitmap
    }

    /**
     * 获取保存图片的路径
     * @param context 上下文对象
     * @param fileName 文件名
     * @return 保存图片的路径
     */
    private fun getSavePath(context: Context, fileName: String): String {
        val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Environment.DIRECTORY_PICTURES
        } else {
            "${Environment.getExternalStorageDirectory().path}/Pictures"
        }
        return "$dir/$fileName"
    }

    /**
     * 通知相册更新
     * @param context 上下文对象
     * @param path 图片路径
     */
    private fun notifyGalleryUpdate(context: Context, path: String) {
        // 通知相册更新
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DATA, path)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}