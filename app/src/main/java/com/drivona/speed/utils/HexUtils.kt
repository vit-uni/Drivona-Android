package com.drivona.speed.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder

//import com.blankj.utilcode.util.JsonUtils
//import com.blankj.utilcode.util.LogUtils
//import com.feijing.yypeiwan.api.ShareUsernameSlice
//import com.feijing.yypeiwan.api.UserSkillPay
//import com.google.gson.Gson
//import com.tencent.imsdk.v2.V2TIMCustomElem
//import com.tencent.imsdk.v2.V2TIMFriendOperationResult
//import com.tencent.imsdk.v2.V2TIMManager
//import com.tencent.imsdk.v2.V2TIMMessage
//import com.tencent.imsdk.v2.V2TIMValueCallback
//
//
class HexUtils {
    companion object {
          fun getBitMap(resources: Resources, resourceId: Int): Bitmap {
            var bitmap = BitmapFactory.decodeResource(resources, resourceId)
            val width = bitmap.width
            val height = bitmap.height
            val newWidth = 80
            val newHeight = 80
            val widthScale = (newWidth.toFloat()) / width
            val heightScale = (newHeight.toFloat()) / height
            val matrix = Matrix()
            matrix.postScale(widthScale, heightScale)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            return bitmap
        }
        /**
         * 小端十六进制字符串转Long十进制
         * hex:不带0x,例 "3412"  -> 0x1234 = 4660
         */
        /**
         * 任意长度小端十六进制字符串 → 十进制Long / BigInteger
         * hex: 不带0x，例 "785634120000" (6字节小端)
         */
        fun littleEndianHexToDecimal(hex: String): BigInteger {
            // 每2个字符一个字节
            val srcBytes = hex.chunked(2)
                .map { it.toInt(16).toByte() }
                .toByteArray()

            // 小端 → 反转字节，转为大端，BigInteger默认解析大端
            val bigEndianBytes = srcBytes.reversedArray()
            return BigInteger(1, bigEndianBytes)
        }

        // 可选：如果结果数值≤Long最大值，可以转Long
        fun littleEndianHexToLong(hex: String): Long {
            val bi = littleEndianHexToDecimal(hex)
            if (bi > BigInteger.valueOf(Long.MAX_VALUE)) {
                throw ArithmeticException("数值超出Long范围，请使用BigInteger")
            }
            return bi.toLong()
        }

        fun decimalToLittleEndianHex(decimal: Int, targetLength: Int): String {
            // 1. 将十进制数转为字节数组（默认大端）
            val bigEndianBytes = byteArrayOf(
                (decimal shr 24).toByte(),
                (decimal shr 16).toByte(),
                (decimal shr 8).toByte(),
                decimal.toByte()
            )

            // 2. 转为小端模式（反转字节数组）
            val littleEndianBytes = bigEndianBytes.reversedArray()

            // 3. 按目标长度调整字节数组（补0 或 截断）
            val adjustedBytes = ByteArray(targetLength).apply {
                // 复制小端字节到新数组，超出长度则截断，不足则补0
                val copyLength = minOf(littleEndianBytes.size, targetLength)
                System.arraycopy(littleEndianBytes, 0, this, 0, copyLength)
            }

            // 4. 字节数组转十六进制字符串（大写，无分隔符）
            return adjustedBytes.joinToString("") { "%02X".format(it) }
        }

        fun parseManufacturerReverse(rawBytes: ByteArray): String {
            var data = ""
            var offset = 0
            while (offset < rawBytes.size) {
                val adLen = rawBytes[offset].toInt() and 0xFF
                if (adLen == 0) break
                if (offset + 1 + adLen > rawBytes.size) break

                val adType = rawBytes[offset + 1].toInt() and 0xFF
                if (adType == 0xFF) {
                    val dataStart = offset + 2
                    // 原始两个字节：小端存放 cidLow, cidHigh
                    val cidLowByte = rawBytes[dataStart]
                    val cidHighByte = rawBytes[dataStart + 1]


                    // ✅反转之后解析出来的值（大端解读原始小端字节）
                    var companyIdReversed =
                        (cidLowByte.toInt() and 0xFF) shl 8 or (cidHighByte.toInt() and 0xFF)
                    data = "%04X".format(companyIdReversed)
                    // payload
                    val payloadSize = adLen - 2
                    if (payloadSize > 0) {
                        rawBytes.copyOfRange(dataStart + 2, dataStart + 2 + payloadSize)
                    } else byteArrayOf()

                }
                offset += 1 + adLen
            }
            return data
        }

        fun swapTwoByteHex(hexStr: String): String {
            val s = hexStr.trim().uppercase()
            if (s.length != 4) return hexStr
            val low = s.substring(0, 2)
            val high = s.substring(2, 4)
            return high + ":"+low
        }

        /**
         * 获取mac地址后两位
         */
        fun swapMacFirstTwoBytesNoColon(mac: String): String {
            val arr = mac.split(":")
            if (arr.size < 2) return mac.replace(":", "")
            return arr[4] + arr[5]
        }
    }
}