package com.drivona.speed.utils;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.TextView;

import com.drivona.speed.api.DeviceData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BleUtil {
    /**
     * 是否有设备连接蓝牙
     *
     * @return
     */
    @SuppressLint("MissingPermission")
    public static BluetoothDevice isBTConnected(BluetoothAdapter bluetoothAdapter, List<DeviceData> connectAddress) {
        try {
            Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            method.setAccessible(true);
            int state = (int) method.invoke(bluetoothAdapter, (Object[]) null);
            Log.e("Song_Ble", "state===" + state); //注意：已连接蓝牙后但state仍然为0的情况下需要检查是否有权限，Android10以上设备需要动态申请权限
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Log.e("Ble", "BluetoothAdapter.STATE_CONNECTED");
                Set<BluetoothDevice> devices = bluetoothAdapter.getBondedDevices();//注意要在AndroidManifest中配置权限
                Log.e("Song_Ble", "devices:" + devices.size());

                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    Log.e("Song_Ble", "设备地址：" +device.getAddress()+"-设备名字：--"+device.getName()+"___是否连接：__"+isConnected);
                    if (isConnected) {
                        for (int i = 0; i < connectAddress.size(); i++) {
                            Log.e("Song_Ble", "硬件设备地址：" +connectAddress.get(i).getUuid());

                            if (device.getAddress() == connectAddress.get(i).getUuid()) {
                                Log.e("Song_Ble", "地址一致" +connectAddress.get(i).getUuid());
                                return device;
                            }
                        }


                    }
                }


            }

        } catch (NoSuchMethodException e) {
            Log.e("Song_Ble", "NoSuchMethodExceptione===" + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("Song_Ble", "IllegalAccessException===" + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("Song_Ble", "InvocationTargetException===" + e);
            e.printStackTrace();
        }

        return null;
    }


}
