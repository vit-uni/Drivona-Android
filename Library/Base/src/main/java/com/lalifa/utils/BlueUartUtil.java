package com.lalifa.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import com.youth.banner.util.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

//public class BlueUartUtil extends Thread {//
//    private final String TAG = "McuSerial";
//    private static String path = "/dev/ttyS4";  // 可根据设备不同修改
//    private static int baudrate = 9600; //波率
//
//    //蓝牙地址码
//    public String msMAC="";
//    //蓝牙连接状态
//    private boolean mbConnectOk = false;
//    //获取默认适配器
//    private BluetoothAdapter mBT = BluetoothAdapter.getDefaultAdapter();
//    public final static String UUID_SPP = "00001101-0000-1000-8000-00805F9B34FB";
//    //蓝牙串口连接对象
//    private BluetoothSocket serialPort = null;
//    private InputStream inputStream = null;
//    private OutputStream outputStream = null;
//    private Object lock = new Object();
//    public int cmdState = 0;
//    private int revlength, sendlength;
//    private byte[] sendbuff = new byte[65535];
//    private byte[] revbuff = new byte[65535];
//
//    public interface StringListener {
//        void addString(String string);
//    }
//
//    private StringListener stringListener;
//
//    public void setStringListener(StringListener stringListener) {
//        this.stringListener = stringListener;
//    }
//
//    private void BlueUartUtil() {
//    }
//
//    /**
//     * 单例
//     */
//    private static volatile BlueUartUtil instance = null;
//
//    public static BlueUartUtil getInstance() {
//        if (instance == null) {
//            synchronized (BlueUartUtil.class) {
//                if (instance == null) {
//                    instance = new BlueUartUtil();
//                    instance.BlueUartUtil();
//                }
//            }
//        }
//        return instance;
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        while ( true ) {
//            try {
//                sleep(10000);
//            } catch (InterruptedException e) {
//                LogUtils.d(e);
//            }
//        }
//    }
//
//    //断开蓝牙连接设备
//    public void closeConnet(){
////        synchronized (lock) {
//            if (this.mbConnectOk) {
//                try {
//                    if (null != inputStream)
//                        inputStream.close();
//                    if (null != outputStream)
//                        outputStream.close();
//                    if (null != serialPort)
//                        serialPort.close();
//                    mbConnectOk = false;//标记连接已被关闭
//                } catch (IOException e) {
//                    //任何一部分报错，都将强制关闭socket连接
//                    inputStream = null;
//                    outputStream = null;
//                    serialPort = null;
//                    mbConnectOk = false;//标记连接已被关闭
//                    LogUtils.d(e);
//                    LogUtils.d("closeSerialPort: 关闭串口异常：" + e.toString());
//                }
//            }
//            LogUtils.d("closeSerialPort: 关闭串口成功");
////        }
//    }
//
//    public boolean isConnectOk(){
//        try {
//            LogUtils.d("BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msMAC)=="+(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msMAC)!=null));
//            return this.mbConnectOk&&serialPort.isConnected()&&(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msMAC)!=null);
//        }catch (Exception e){
//            LogUtils.d(e);
//            return false;
//        }
//    }
//
//    /**
//     * 建立蓝牙设备串口连接
//     */
//    public boolean createConn(String sMAC) {
//        msMAC = sMAC;
//        if (!mBT.isEnabled())
//            return false;
//        //如果连接存在则断开连接
//        if (isConnectOk())
//            closeConnet();
//
//        //开始连接蓝牙设备
//        final BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(msMAC);
//
//        //UUID连接
//        UUID uuidSPP = UUID.fromString(UUID_SPP);
//
//        try {
//            serialPort = device.createRfcommSocketToServiceRecord(uuidSPP);
//            serialPort.connect();
//            LogUtils.d("createConn: 打开串口成功");
//            outputStream = serialPort.getOutputStream();
//            inputStream = serialPort.getInputStream();
//            mbConnectOk = true;
//        } catch (IOException e) {
//            LogUtils.e(e);
//            closeConnet(); //断开连接
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 判断当前设备是否连接
//     * */
//
//    public boolean isConnect() {
//        return mbConnectOk;
//    }
//
//    public void destory() {
//        closeConnet();
//    }
//
//    public interface CallBack{
//        void onSucess();
//        void onFaild();
//    }
//
//    public boolean sendFileToPc(String mac, String filepath, int timeout,int times,CallBack callBack)
//    {
//        for(int i=0;i<times;i++){
//            if(sendFileToPc(mac,filepath,timeout)){
//                if(callBack!=null)
//                    callBack.onSucess();
//                return true;
//            }
//            else{
//                if(callBack!=null)
//                    callBack.onFaild();
//            }
//        }
//        if(callBack!=null)
//            callBack.onFaild();
//        return false;
//    }
//
//    public boolean sendFileToPc(String mac, String filepath, int timeout){
//        String name = Thread.currentThread().getStackTrace()[2].getMethodName();
//        int targetlength = 0;
//        byte[] tempRevBuff = new byte[65535];
//        byte[] tempSendBuff = new byte[sendlength];
//        int i=0;
//
//        try {
//            LogUtils.d("msMAC:" + msMAC + ",mac:" + mac);
//            if ((!isConnectOk()) || (msMAC == null) || (!msMAC.equals(mac))) {
//                LogUtils.d("重新建立通信");
//                if (!createConn(mac)) {
//                    LogUtils.d("建立通信失败");
//                    return false;
//                }
//            }
//
//            String fileheadstr = "0x5E|0000|";//0x5E|0059|2023-12-18-14-16-36_1|102400|.zip|aomsv123456|111111|end
//            File file = FileCommonUtil.getFileByPath(filepath);
//            fileheadstr += file.getName() + "|";
//            long fileSize = file.length();
//            fileheadstr += String.valueOf(fileSize) + "|";
//            fileheadstr += getFileExtension(filepath) + "|";
//            fileheadstr += "AOMS" + String.valueOf(deviceno) + "|";
//
//            int crc = 0;
//            int bytesRead;
//            byte[] buffer = new byte[1024];
//            InputStream fileinputStream = null;
//            try {
//                fileinputStream = new FileInputStream(filepath);
//                if (fileinputStream == null) {
//                    LogUtils.d("fileinputStream==null");
//                    return false;
//                }
//                while ( (bytesRead = fileinputStream.read(buffer)) != -1 ) {
//                    crc = CRCUtil.CRC_16_XMODEM(crc, buffer, 0, bytesRead);
//                }
//            } catch (IOException e) {
//                LogUtils.d(e);
//            } finally {
//                if (fileinputStream != null) {
//                    try {
//                        fileinputStream.close();
//                    } catch (IOException e) {
//                        LogUtils.d(e);
//                    }
//                    fileinputStream = null;
//                }
//                LogUtils.d("fileinputStream=null;");
//            }
//
//            fileheadstr += String.valueOf(crc) + "|";
//            fileheadstr += "end";
//            LogUtils.d("fileheadstr.length():" + fileheadstr.getBytes().length);
//            fileheadstr = replaceSubstring(fileheadstr, 5, 9, String.format("%04d", fileheadstr.getBytes().length));
//            LogUtils.d(fileheadstr);
//            try {
//                int count = inputStream.available();
//                if (count > 0) {
//                    inputStream.read(tempRevBuff);
//                }
//            } catch (IOException e) {
//                LogUtils.d(e);
//            }
//            revlength = 0;
//            byte[] firstbuff = fileheadstr.getBytes();
//            if (!mcuSerialSend(firstbuff, firstbuff.length)) {
//                return false;
//            }
//            System.arraycopy(sendbuff, 0, tempSendBuff, 0, sendlength);
//            do {
//                SystemClock.sleep(5);
//                if (!mbConnectOk) {
//                    break;
//                }
//                try {
//                    int templength = inputStream.available();
//                    if (templength > 0)
//                        templength = inputStream.read(tempRevBuff);
//                    if (templength > 0) {
//                        System.arraycopy(tempRevBuff, 0, revbuff, revlength, templength);
//                        revlength += templength;
//                        continue;
//                    }
//                } catch (IOException e) {
//                    LogUtils.d(e);
//                }
//                i++;
//            } while ( i * 5 < timeout);
//            if (revlength > 0) {
//                String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
//                LogUtils.d("name" + "  " + revmsg);
//            }
//            //返回"0x5E|0000000"
//            String revstr = new String(revbuff, 0, revlength);
//            LogUtils.d(revstr);
//            if (!revstr.contains("0x5E|00000000")) {
//                LogUtils.d("头没有回");
//                return false;
//            }
//            revlength = 0;
//            try {
//                fileinputStream = new FileInputStream(filepath);
//                if (fileinputStream == null) {
//                    LogUtils.d("fileinputStream==null");
//                    return false;
//                }
//                while ( (bytesRead = fileinputStream.read(buffer)) != -1 ) {
//                    if (!mcuSerialSend(buffer, bytesRead)) {
//                        return false;
//                    }
//                }
//            } catch (IOException e) {
//                LogUtils.d(e);
//                return false;
//            } finally {
//                if (fileinputStream != null) {
//                    try {
//                        fileinputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    fileinputStream = null;
//                }
//            }
//            i = 0;
//            System.arraycopy(sendbuff, 0, tempSendBuff, 0, sendlength);
//            do {
//                SystemClock.sleep(5);
//                if (!mbConnectOk) {
//                    break;
//                }
//                try {
//                    int templength = inputStream.available();
//                    if (templength > 0)
//                        templength = inputStream.read(tempRevBuff);
//                    if (templength > 0) {
//                        System.arraycopy(tempRevBuff, 0, revbuff, revlength, templength);
//                        revlength += templength;
//                        continue;
//                    }
//                } catch (IOException e) {
//                    LogUtils.d(e);
//                }
//                i++;
//            } while ( i * 5 < timeout );
//            if (revlength > 0) {
//                String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
//                LogUtils.d("name" + "  " + revmsg);
//            }
//            //返回"0x5E|0000000"
//            revstr = new String(revbuff, 0, revlength);
//            LogUtils.d(revstr);
//            if (!revstr.contains("0x5E|00000001")) {
//                LogUtils.d("传输失败");
//                return false;
//            } else {
//                LogUtils.d("传输完成");
//                return true;
//            }
//        }catch (Exception e){
//            LogUtils.d("传输失败");
//            return false;
//        }
//    }
//
//
////    byte[]allbuffer = new byte[1024*1024*8]; // 缓冲区大小可以根据需要调整
////    public boolean sendFileToPc(String mac, String filepath, int timeout,CallBack callBack){
////        String name = Thread.currentThread().getStackTrace()[2].getMethodName();
////        int targetlength = 0;
////        byte[] tempRevBuff = new byte[65535];
////        byte[] tempSendBuff = new byte[sendlength];
////        int i=0;
////
////        LogUtils.d("msMAC:"+msMAC+",mac:"+mac);
////        if((!isConnectOk())||(msMAC==null)||(!msMAC.equals(mac))){
////            LogUtils.d("重新建立通信");
////            if(!createConn(mac)){
////                LogUtils.d("建立通信失败");
////                return false;
////            }
////        }
////
////        String fileheadstr = "0x5E|0000|";//0x5E|0059|2023-12-18-14-16-36_1|102400|.zip|aomsv123456|111111|end
////        File file = FileCommonUtil.getFileByPath(filepath);
////        fileheadstr += file.getName()+"|";
////        long fileSize = file.length();
////        fileheadstr += String.valueOf(fileSize)+"|";
////        fileheadstr +=getFileExtension(filepath)+"|";
////        fileheadstr +="AOMS"+String.valueOf(deviceno)+"|";
////
////        int bytelen=0;
////        int bytesRead;
////        byte[] buffer = new byte[1024];
////        if(fileSize<1024*1024*8){
////            try (InputStream inputStream = new FileInputStream(filepath)) {
////                while ( (bytesRead = inputStream.read(buffer)) != -1 ) {
////                    System.arraycopy(buffer, 0, allbuffer, bytelen, bytesRead);
////                    bytelen+=bytesRead;
////                }
////
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////        int crc = CRCUtil.CRC_16_XMODEM(allbuffer,0,bytelen);
////        fileheadstr +=String.valueOf(crc)+"|";
////        fileheadstr +="end";
////        LogUtils.d("fileheadstr.length():"+fileheadstr.length());
////        fileheadstr = replaceSubstring(fileheadstr,5,9,String.format("%04d",fileheadstr.length()));
////        LogUtils.d(fileheadstr);
////
////        try {
////            int count = inputStream.available();
////            if(count>0) {
////                inputStream.read(tempRevBuff);
////            }
////        } catch (IOException e) {
////            LogUtils.d(e);
////        }
////
////        byte[] firstbuff = fileheadstr.getBytes();
////        mcuSerialSend(firstbuff,firstbuff.length);
////        System.arraycopy(sendbuff, 0, tempSendBuff, 0, sendlength);
////        do {
////            SystemClock.sleep(5);
////            if(!mbConnectOk){
////                break;
////            }
////            try {
////                int templength = inputStream.available();
////                if(templength>0)
////                    templength = inputStream.read(tempRevBuff);
////                if (templength > 0) {
////                    System.arraycopy(tempRevBuff, 0, revbuff, revlength, templength);
////                    revlength += templength;
////                    continue;
////                }
////            } catch (IOException e) {
////                LogUtils.d(e);
////            }
////            i++;
////        } while ( i * 5 < 3000 );
////        if (revlength > 0) {
////            String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
////            LogUtils.d("name" + "  " + revmsg);
////        }
////        //返回"0x5E|0000000"
////        String revstr = new String(revbuff,0,revlength);
////        LogUtils.d(revstr);
////        if(!revstr.contains("0x5E|0000000")){
////            LogUtils.d("头没有回");
////            return false;
////        }
////        revlength = 0;
////        mcuSerialSend(allbuffer,bytelen);
////        i=0;
////        System.arraycopy(sendbuff, 0, tempSendBuff, 0, sendlength);
////        do {
////            SystemClock.sleep(5);
////            if(!mbConnectOk){
////                break;
////            }
////            try {
////                int templength = inputStream.available();
////                if(templength>0)
////                    templength = inputStream.read(tempRevBuff);
////                if (templength > 0) {
////                    System.arraycopy(tempRevBuff, 0, revbuff, revlength, templength);
////                    revlength += templength;
////                    continue;
////                }
////            } catch (IOException e) {
////                LogUtils.d(e);
////            }
////            i++;
////        } while ( i * 5 < timeout );
////        if (revlength > 0) {
////            String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
////            LogUtils.d("name" + "  " + revmsg);
////        }
////        //返回"0x5E|0000000"
////        revstr = new String(revbuff,0,revlength);
////        LogUtils.d(revstr);
////        if(revstr.contains("0x5E|00000000")){
////            LogUtils.d("传输完成");
////            return false;
////        }else{
////            LogUtils.d("传输失败");
////            return true;
////        }
////    }
//
//    private static String replaceSubstring(String original, int startIndex, int endIndex, String replacement) {
//        // 使用 substring 获取替换前后的两部分字符串，然后拼接 replacement
//        return original.substring(0, startIndex) + replacement + original.substring(endIndex);
//    }
//
//    private static String getFileExtension(String fileName) {
//        int lastDotIndex = fileName.lastIndexOf('.');
//        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
//            // 文件名中没有点，或者点是文件名的最后一个字符
//            return "";
//        } else {
//            // 返回点后面的字符串作为文件扩展名
//            return fileName.substring(lastDotIndex + 1);
//        }
//    }
//    /**
//     * 发送串口数据
//     *
//     * @param sendData byte[]
//     */
//    public boolean mcuSerialSend(byte[] sendData, int size) {
//        try {
//            outputStream.write(sendData, 0, size);
//            outputStream.flush();
//            return true;
//            //Log.d(TAG, "sendSerialPort: 串口数据发送成功 "+ bytesToHexString(sendData));
//        } catch (IOException e) {
//            closeConnet();
//            LogUtils.d(e);
//            LogUtils.d( "sendSerialPort: 串口数据发送失败：" + e.toString());
//        }
//        return false;
//    }
//
//    int sendAndRev(byte[] sendbuff, int sendlength, int targetlength, int timeout, boolean... textable) {
//        boolean textable1 = ((textable != null) && textable.length > 0) && textable[0];
//        String name = Thread.currentThread().getStackTrace()[2].getMethodName();
//        int i = 0;
//        byte[] tempRevBuff = new byte[65535];
//        byte[] tempSendBuff = new byte[sendlength];
//        System.arraycopy(sendbuff, 0, tempSendBuff, 0, sendlength);
//        revlength = 0;
//        if (DEVICE) {
//            try {
//                if(!mbConnectOk){
//                    return revlength;
//                }
//                int count = inputStream.available();
//                if(count>0)
//                    inputStream.read(tempRevBuff);
//            } catch (IOException e) {
//                LogUtils.d(e);
//            }
//            mcuSerialSend(tempSendBuff, sendlength);
//            String sendmsg = "SEND:" + bytesToHexString(tempSendBuff);
//            LogUtils.d(name + "  " + sendmsg);
//            if (textable1) {
//                if (stringListener != null) {
//                    sendmsg = "<p><font color='black' size='20'>" + sendmsg + "</font></p>";
//                    stringListener.addString(sendmsg);
//                }
//            }
//            do {
//                SystemClock.sleep(5);
//                if(!mbConnectOk){
//                    return revlength;
//                }
//                try {
//                    int templength = inputStream.available();
//                    if(templength>0)
//                        templength = inputStream.read(tempRevBuff);
//                    if (templength > 0) {
//                        if ((revlength) + templength > targetlength)
//                            templength = targetlength - (revlength);
//                        System.arraycopy(tempRevBuff, 0, revbuff, revlength, templength);
//                        revlength += templength;
//                        if ((revlength > 4) && (targetlength == 65535)) {
//                            targetlength = (int) (revbuff[3] & 0xFF);
//                        }
//                        if ((revlength) >= targetlength)
//                            break;
//                        continue;
//                    }
//                } catch (IOException e) {
//                    LogUtils.d(e);
//                }
//                i++;
//            } while ( i * 5 < timeout );
//            if (revlength > 0) {
//                String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
//                LogUtils.d(name + "  " + revmsg);
//                if (textable1) {
//                    if (stringListener != null) {
//                        revmsg = "<p><font color='black' size='20'>" + revmsg + "</font></p>";
//                        stringListener.addString(revmsg);
//                    }
//                }
//            }
//        } else {
//            String sendmsg = "SEND:" + bytesToHexString(tempSendBuff);
//            LogUtils.d(name + "  " + sendmsg);
//            if (textable1) {
//                if (stringListener != null) {
//                    sendmsg = "<p><font color='black' size='20'>" + sendmsg + "</font></p>";
//                    stringListener.addString(sendmsg);
//                }
//            }
//            if (revlength > 0) {
//                String revmsg = "REV:" + bytesToHexString(revbuff, revlength);
//                LogUtils.d(name + "  " + revmsg);
//                if (textable1) {
//                    if (stringListener != null) {
//                        revmsg = "<p><font color='black' size='20'>" + revmsg + "</font></p>";
//                        stringListener.addString(revmsg);
//                    }
//                }
//            }
//        }
//        return revlength;
//    }
//
//    int cmdPrev(byte[] revbuff, int revlength) {
//        int size, len, ret = -1;
//        int index = 0;
//        while ( (revbuff[index] != FRAMEHEAD) && (index < revlength) ) {
//            index++;
//        }
//        revlength = revlength - index;
//        System.arraycopy(revbuff, index, revbuff, 0, revlength);
//
//        if (revlength < 5) {
//            LogUtils.d("cmd rev size < 5:" + String.valueOf(revlength));
//            //emit sendStateCode(COMMU_LENLT);
//            //sendStateCodeEvent(COMMU_LENLT);
//            return ret;
//        }
//
//        len = (int) (revbuff[3] & 0xFF);
//        size = len;
//
//        if (len < 5) {
//            LogUtils.d("cmd len <5:" + String.valueOf(len));
//            //emit sendStateCode(COMMU_LENLT);
//            //sendStateCodeEvent(COMMU_LENLT);
//            return ret;
//        }
//
//        if (len > revlength) {
//            LogUtils.d("cmd len:" + String.valueOf(len) + " no match rev length" + String.valueOf(revlength));
//            //emit sendStateCode(COMMU_LENNOMATCH);
//            //sendStateCodeEvent(COMMU_LENNOMATCH);
//            return ret;
//        }
//
//        if (revbuff[size - 1] != sumrc(revbuff, size - 1)) {
//            LogUtils.d("BCC ERR:" + String.valueOf(sumrc(revbuff, size - 1)));
//            //emit sendStateCode(COMMU_BCCERR);
//            //sendStateCodeEvent(COMMU_BCCERR);
//            return ret;
//        }
//        return revlength;
//    }
//
//    private byte sumrc(byte[] data, int size) {
//        byte sum = 0;
//        for (int i = 0; i < size; i++) {
//            sum += data[i];
//        }
//        return sum;
//    }
//
//    private static byte[] hexStringToBytes(String hexString) {
//        if (hexString == null || hexString.equals("")) {
//            return null;
//        }
//        hexString = hexString.toUpperCase();
//        int length = hexString.length() / 2;
//        char[] hexChars = hexString.toCharArray();
//        byte[] d = new byte[length];
//        for (int i = 0; i < length; i++) {
//            int pos = i * 2;
//            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
//        }
//        return d;
//    }
//
//    /**
//     * Convert byte[] to hex string
//     *
//     * @param src byte[] data
//     * @return hex string
//     */
//    public static String bytesToHexString(byte[] src) {
//        StringBuilder stringBuilder = new StringBuilder("");
//        if (src == null || src.length <= 0) {
//            return null;
//        }
//        for (int i = 0; i < src.length; i++) {
//            int v = src[i] & 0xFF;
//            String hv = Integer.toHexString(v);
//            if (hv.length() < 2) {
//                stringBuilder.append(0);
//            }
//            stringBuilder.append(hv);
//        }
//        return stringBuilder.toString();
//    }
//
//    /**
//     * Convert byte[] to hex string
//     *
//     * @param src byte[] data
//     * @return hex string
//     */
//    public static String bytesToHexString(byte[] src, int size,String split) {
//        StringBuilder stringBuilder = new StringBuilder("");
//        if (src == null || src.length <= 0 || src.length < size) {
//            return null;
//        }
//        for (int i = 0; i < size; i++) {
//            int v = src[i] & 0xFF;
//            String hv = Integer.toHexString(v);
//            if (hv.length() < 2) {
//                stringBuilder.append(0);
//            }
//            stringBuilder.append(hv);
//            stringBuilder.append(split);
//        }
//        return stringBuilder.toString();
//    }
//
//    /**
//     * Convert byte[] to hex string
//     *
//     * @param src byte[] data
//     * @return hex string
//     */
//    public static String bytesToHexString(byte[] src, int size) {
//        StringBuilder stringBuilder = new StringBuilder("");
//        if (src == null || src.length <= 0 || src.length <= size) {
//            return null;
//        }
//        for (int i = 0; i < size; i++) {
//            int v = src[i] & 0xFF;
//            String hv = Integer.toHexString(v);
//            if (hv.length() < 2) {
//                stringBuilder.append(0);
//            }
//            stringBuilder.append(hv);
//        }
//        return stringBuilder.toString();
//    }
//
//    /**
//     * 字符转换为字节
//     */
//    private static byte charToByte(char c) {
//        return (byte) "0123456789ABCDEF".indexOf(c);
//    }
//
////    static int virtualCnt = 0;
////    Object[] innerHandleBytes(String name, byte[] sendbuf, int sendlen, boolean... textable) {
////        String resultmsg = "";
////        boolean textable1 = ((textable != null) && textable.length > 0) && textable[0];
////        int ret = -1, j = 0;
////        Object[] tempObjects = new Object[5];
////        CmdType cmdType = CmdType.valueOf(sendbuf[1] & 0xFF);
////        byte[] buff = null;
////        if (DEVICE) {
////            cmdState = -1;
////            do {
////                if(!mbConnectOk){
//////                    sendStateCodeEvent(0xFFFFFFFF,"蓝牙已断开");
////                    ret = cmdType.cmdErr|STATE_COMMU_BLUETHOOTH_ERR;
////                    tempObjects[0] = ret;
////                    tempObjects[1] = cmdState;
////                    tempObjects[2] = buff;
////                    tempObjects[3] = revlength;
////                    return tempObjects;
////                }
////                revlength = 65535;
////                revlength = sendAndRev(sendbuf, sendlen, revlength, cmdType.cmdTimeout, textable1);
////                revlength = cmdPrev(revbuff, revlength);
////                if (revlength > 0) {
////                    if (revbuff[1] == (byte) cmdType.cmdRes) {
////                        resultmsg = cmdType.name + " sucess";
////                        LogUtils.d(resultmsg);
////                        if (textable1) {
////                            if (stringListener != null) {
////                                resultmsg = "<p><font color='green' size='20'>" + resultmsg + "</font></p>";
////                                stringListener.addString(resultmsg);
////                            }
////                        }
////                        buff = new byte[revlength];
////                        System.arraycopy(revbuff, 0, buff, 0, revlength);
////                        cmdState = revbuff[revlength - 2];
////                        tempObjects[1] = cmdState;
////                        ret = STATE_COMMU_SUCESS;
////                        tempObjects[0] = ret;
////                        tempObjects[2] = buff;
////                        tempObjects[3] = revlength;
////                        return tempObjects;
////                    } else {
////                        resultmsg = "cmd ins wrong:" + String.valueOf(revbuff[1]);
////                        LogUtils.d(resultmsg);
////                        if (textable1) {
////                            if (stringListener != null) {
////                                resultmsg = "<p><font color='red' size='20'>" + resultmsg + "</font></p>";
////                                stringListener.addString(resultmsg);
////                            }
////                        }
////                        ret = STATE_COMMU_INSERR;
////                    }
////                }
////                try {
////                    Thread.sleep(2000);
////                    resultmsg = cmdType.name + "  " + String.valueOf(j + 1) + "  times err!!!";
////                    LogUtils.d(resultmsg);
////                    if (textable1) {
////                        if (stringListener != null) {
////                            resultmsg = "<p><font color='red' size='20'>" + resultmsg + "</font></p>";
////                            stringListener.addString(resultmsg);
////                        }
////                    }
////                } catch (InterruptedException e) {
////                    LogUtils.d(e);
////                }
////            } while ( (++j) < cmdType.cmdRetryTime );
////            if(!mbConnectOk){
////                ret = cmdType.cmdErr|STATE_COMMU_BLUETHOOTH_ERR;
////            }
////            else {
////                ret = cmdType.cmdErr;
////            }
////            tempObjects[0] = ret;
////            tempObjects[1] = cmdState;
////            tempObjects[2] = buff;
////            tempObjects[3] = revlength;
////            return tempObjects;
////        } else {
////            revlength = 0;
////            revlength = sendAndRev(sendbuf, sendlen, revlength, cmdType.cmdTimeout, textable1);
////            resultmsg = cmdType.name + " sucess";
////            LogUtils.d(resultmsg);
////            if (textable1) {
////                if (stringListener != null) {
////                    resultmsg = "<p><font color='green' size='20'>" + resultmsg + "</font></p>";
////                    stringListener.addString(resultmsg);
////                }
////            }
////            buff = new byte[255];
////            if (virtualCnt > 0) {
////                if (virtualCnt++ > 100) {
////                    virtualCnt = 1;
////                    tempObjects[1] = 1;
////                    ret = STATE_COMMU_ERR;
////                }
////            } else {
////                tempObjects[1] = 0;
////                ret = STATE_COMMU_SUCESS;
////            }
////            tempObjects[0] = ret;
////            tempObjects[2] = buff;
////            tempObjects[3] = revlength;
////            return tempObjects;
////        }
////    }
//}
