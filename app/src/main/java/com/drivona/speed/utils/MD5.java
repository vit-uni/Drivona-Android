package com.drivona.speed.utils;

import java.security.MessageDigest;

public class MD5 {
    private static final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55,
            56, 57, 97, 98, 99, 100, 101, 102 };
 
    public static String hexdigest(String paramString) {
        try {
            String str = hexdigest(paramString.getBytes());
            return str;
        } catch (Exception localException) {
        }
        return null;
    }
 
    public static String hexdigest(byte[] paramArrayOfByte) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar = new char[32];
            int i = 0;
            int j = 0;
            while (true) {
                if (i >= 16)
                    return new String(arrayOfChar);
                int k = arrayOfByte[i];
                int m = j + 1;
                arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
                j = m + 1;
                arrayOfChar[m] = hexDigits[(k & 0xF)];
                i++;
            }
        } catch (Exception localException) {
        }
        return null;
    }
}

/**
 * 使用
 * */
//
//val manager = packageManager
//    /** 通过包管理器获得指定包名包含签名的包信息 **/
//    var packageInfo: PackageInfo? = null
//        try {
//        packageInfo = manager.getPackageInfo("com.feijing.yypeiwan",
//        PackageManager.GET_SIGNATURES)
//        } catch (e: PackageManager.NameNotFoundException) {
//        e.printStackTrace()
//        }
//        /******* 通过返回的包信息获得签名数组 *******/
//        val signatures: Array<Signature> = packageInfo!!.signatures
//        val ss: String = MD5.hexdigest(signatures[0].toByteArray())
//        if (ss != null) {
//        Log.e("==","=====签名$ss",)
//        // Toast.makeText(this, "签名$ss", Toast.LENGTH_LONG).show()
//        } else {
//        Toast.makeText(this, "没获得签名", Toast.LENGTH_LONG).show()
//        }