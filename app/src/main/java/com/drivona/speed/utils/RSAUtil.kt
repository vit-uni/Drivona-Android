package com.drivona.speed.utils

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 * Android RSA 加解密工具类
 * 支持：密钥对生成、加密、解密、密钥格式转换
 */
object RSAUtil {
    // RSA算法标识
    const val RSA_KEY =
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhaimJSCmtlLP5009Ga/Z4OXq5yue8wF85C+41lssRffEm9FuXrUWY3mfBo66Y2qL6bvDd7mmaM7evZP20Ij0C7hXlnvUf59eFjjWNwP7by+VNKJddopYpaWWhpkehQnE1XU1AW1t5YKIGC3Ak0KRAvGeVovZFsr7LrljDEJQ6mm/+36vLpdsjI1yyv3j5V3GzIjFJt4bxQZ8zfh37iBt7R7L526X7Y6bgtNrw/PRAy6sWKPHY1E7mxXAyV7Uph1BRWskhFnPMK0HfyEF6+/WZzRokDU+7YtFabvGZs2e7DJEMGAldzdcHWivc0YHSV/bnjlBp+1dCkZMaPdlqsJSDQIDAQAB"
    private const val RSA_ALGORITHM = "RSA"

    // 填充方式（Android推荐使用，解决数据长度限制问题）
    private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"

    // 密钥长度（2048位安全性更高，1024位已不推荐）
    private const val KEY_SIZE = 2048

    /**
     * 生成RSA密钥对
     * @return KeyPair 包含公钥和私钥的密钥对
     * @throws Exception 生成密钥对失败时抛出
     */
    @Throws(Exception::class)
    fun generateRSAKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM)
        // 初始化密钥生成器
        keyPairGenerator.initialize(KEY_SIZE, SecureRandom())
        return keyPairGenerator.generateKeyPair()
    }

    /**
     * 获取Base64编码的公钥字符串
     * @param publicKey 公钥对象
     * @return Base64编码的公钥字符串
     */
    fun getPublicKeyString(publicKey: PublicKey): String {
        return Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
    }

    /**
     * 获取Base64编码的私钥字符串
     * @param privateKey 私钥对象
     * @return Base64编码的私钥字符串
     */
    fun getPrivateKeyString(privateKey: PrivateKey): String {
        return Base64.encodeToString(privateKey.encoded, Base64.NO_WRAP)
    }

    /**
     * 将Base64编码的公钥字符串转换为PublicKey对象
     * @param publicKeyStr Base64编码的公钥字符串
     * @return PublicKey 公钥对象
     * @throws Exception 转换失败时抛出
     */
    @Throws(Exception::class)
    fun getPublicKey(publicKeyStr: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyStr, Base64.NO_WRAP)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePublic(keySpec)
    }

    /**
     * 将Base64编码的私钥字符串转换为PrivateKey对象
     * @param privateKeyStr Base64编码的私钥字符串
     * @return PrivateKey 私钥对象
     * @throws Exception 转换失败时抛出
     */
    @Throws(Exception::class)
    fun getPrivateKey(privateKeyStr: String): PrivateKey {
        val keyBytes = Base64.decode(privateKeyStr, Base64.NO_WRAP)
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(RSA_ALGORITHM)
        return keyFactory.generatePrivate(keySpec)
    }

    /**
     * 使用公钥加密数据
     * @param data 待加密的明文数据
     * @param publicKey 公钥对象
     * @return Base64编码的加密结果
     * @throws Exception 加密失败时抛出
     */
    @Throws(Exception::class)
    fun encryptWithPublicKey(data: String, publicKey: PublicKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        var tempData = ""
        if (data != null) {
            tempData = data
        }
        if (tempData.length > 30) {
            return tempData
        }
        // 处理长数据分段加密（RSA加密有长度限制）
        val dataBytes = tempData.toByteArray(Charsets.UTF_8)
        val maxEncryptLength = KEY_SIZE / 8 - 11 // PKCS1Padding 需预留11字节
        val outputStream = ByteArrayOutputStream()

        var offset = 0
        while (offset < dataBytes.size) {
            val length = if (dataBytes.size - offset > maxEncryptLength) {
                maxEncryptLength
            } else {
                dataBytes.size - offset
            }
            val encryptedBlock = cipher.doFinal(dataBytes, offset, length)
            outputStream.write(encryptedBlock)
            offset += length
        }

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    /**
     * 使用私钥解密数据
     * @param encryptedData Base64编码的加密数据
     * @param privateKey 私钥对象
     * @return 解密后的明文
     * @throws Exception 解密失败时抛出
     */
    @Throws(Exception::class)
    fun decryptWithPrivateKey(encryptedData: String, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        // 处理分段解密
        val encryptedBytes = Base64.decode(encryptedData, Base64.NO_WRAP)
        val maxDecryptLength = KEY_SIZE / 8
        val outputStream = ByteArrayOutputStream()

        var offset = 0
        while (offset < encryptedBytes.size) {
            val length = if (encryptedBytes.size - offset > maxDecryptLength) {
                maxDecryptLength
            } else {
                encryptedBytes.size - offset
            }
            val decryptedBlock = cipher.doFinal(encryptedBytes, offset, length)
            outputStream.write(decryptedBlock)
            offset += length
        }

        return String(outputStream.toByteArray(), Charsets.UTF_8)
    }

    /**
     * 简化版加密：直接传入公钥字符串加密
     */
    @Throws(Exception::class)
    fun encrypt(data: String, publicKeyStr: String = RSA_KEY): String {
        val publicKey = getPublicKey(publicKeyStr)
        return encryptWithPublicKey(data, publicKey)
    }

    /**
     * 简化版解密：直接传入私钥字符串解密
     */
    @Throws(Exception::class)
    fun decrypt(encryptedData: String, privateKeyStr: String): String {
        val privateKey = getPrivateKey(privateKeyStr)
        return decryptWithPrivateKey(encryptedData, privateKey)
    }
}