package com.lalifa.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.view.drawToBitmap
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.OnPermissionInterceptor
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.notiface.PermissionInterceptor
import java.io.File
import java.io.FileOutputStream

/**
 * View转图片保存本地
 */
class ViewToImageUtils {
    companion object {
        fun viewToImage(context: Context, view: View, callback: (File?) -> Unit){
            XXPermissions.with(context)
                // 不适配分区存储应该这样写
//                .permission(Permission.WRITE_EXTERNAL_STORAGE)//android 13 废弃AndroidManifest.xml也要去掉
                .permission(Permission.READ_MEDIA_IMAGES)//android 13 废弃AndroidManifest.xml也要去掉
                .interceptor(PermissionInterceptor())
                .request(object : OnPermissionCallback {

                    @Override
                    override fun onGranted(permissions:List<String>, allGranted: Boolean) {
                        if (allGranted) {
                            callback.invoke(toFile(context, view))
                        }
                    }

                    @Override
                    override fun onDenied(permissions: List<String>, doNotAskAgain: Boolean) {
                        if (doNotAskAgain) {
                            ToastUtils.showShort("权限被拒绝！！！");
                        }
                    }
                })
        }

        fun toFile(context: Context, view: View) : File?{
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S) {
                // Android 13及更高版本，使用MediaStore API
                return saveBitmapToGalleryAndroid13(context, view)
            } else {
                // Android 12及更低版本，使用标准的文件I/O方式
                return saveBitmapToGalleryLegacy(context, view)
            }
        }

        private fun saveBitmapToGalleryAndroid13(context: Context, view: View) : File?{
            val bitmap = view.drawToBitmap()
            val fileName = "image_${System.currentTimeMillis()}.jpg" // 自动生成唯一文件名
            val relativePath = Environment.DIRECTORY_PICTURES

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            }

            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val imageUri = context.contentResolver.insert(collection, contentValues)

            try {
                context.contentResolver.openOutputStream(imageUri!!)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
//                MediaScannerConnection.scanFile(context, arrayOf(relativePath), null, null)
//                notifyGalleryUpdate(context, relativePath)
//                MediaScannerConnection.scanFile(context, arrayOf(relativePath), null) { _, uri ->
//                    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
//                }
                return File(relativePath)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        private fun saveBitmapToGalleryLegacy(context: Context, view: View): File? {
            // 创建一个空的Bitmap对象
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            // 创建一个Canvas对象，并将其绑定到Bitmap上
            val canvas = Canvas(bitmap)
            // 将View绘制到Canvas上
            view.draw(canvas)
            //获取可扫描目录的根目录
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            // 创建文件名
            val fileName = "my_image.png"
            // 在可扫描目录下创建一个新文件
            val file = File(storageDir, fileName)
            try {
                // 创建文件输出流
                val fos = FileOutputStream(file)
                val compressImage = BitmapUtil.compressImage(bitmap, 100)
                // 将Bitmap对象压缩为PNG格式，并写入文件输出流
                compressImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                // 关闭文件输出流
                fos.close()
                // 发送媒体扫描广播，通知系统更新相册
                MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), null, null)
                notifyGalleryUpdate(context, file.absolutePath)
                return file
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

         fun outPutBitMap(bitmap: Bitmap, path: String, fileName: String): File {
            // 在可扫描目录下创建一个新文件
            val file = File(path, fileName)
            try {
                // 创建文件输出流
                val fos = FileOutputStream(file)
                val compressImage = BitmapUtil.compressImage(bitmap, 100)
                // 将Bitmap对象压缩为PNG格式，并写入文件输出流
                compressImage.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                // 关闭文件输出流
                fos.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file
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

}