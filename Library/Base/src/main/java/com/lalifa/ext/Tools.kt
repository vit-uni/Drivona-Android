package com.lalifa.ext

//import com.opensource.svgaplayer.SVGAParser

class Tools {
    companion object{

        const val BASE_URL = "https://online.vit-uni.com"
            // 正式环境
            // 测试环境
//        const val BASE_URL = "https://app.vit-uni.com"
        const val HOST = "${BASE_URL}/"
        const val FILE_PATH = "${BASE_URL}/"
        const val GoogleKey = "AIzaSyCM7U1dQw4hMHK0iRJr2caNATHdMKboFrU"
//        val parser = SVGAParser.shareParser()

        const val IS_AGREE = "IS_AGREE"
        const val IS_LOGIN = "IS_LOGIN"
        const val Token = "Token"
        const val SWITCH_SEACH_HISTORY = "SWITCH_SEACH_HISTORY"
        const val SETTING = "setting"
        const val isHaveOpen = "isHaveOpen"
        const val CurrentDeviceType = "CurrentDeviceType"
        //请求成功
        const val RESULT_CODE = 1
        //登录失效
        const val LOGIN_INVALID = 401
        //错误码
        const val ERROR_CODE = "1002"
        //分页每页数据数量
        const val PAGE_SIZE = 20
        const val DeviceID ="DeviceID"
        const val DeviceNAME ="DeviceNAME"
        const val BLE_SERVICE_ID ="00005500-0000-1000-8000-00805f9b34fb"
        const val BLE_Write_ID ="00005501-0000-1000-8000-00805f9b34fb"
        const val BLE_Read_ID ="00005502-0000-1000-8000-00805f9b34fb"
        const val BLE_LianJie   ="55AA03000001FF"
        const val BLE_DianLiang ="55AA02000101"
        const val BLE_DengLiang ="55AA0400050102"


        const val Device_History = "Device_History"
        const val Place_History = "Place_History"
        const val Avatar_Path = "Avatar_Path"
        const val isMPH = "isMPH"
        const val Stop_Navigation = "Stop_Navigation"
        const val Start_Navigation = "Start_Navigation"
        const val Car_Exit = "Car_Exit"
        const val Car_Center = "Car_Center"
        const val Car_APP_CHECK = "Car_APP_CHECK"
        const val Car_APP_CHECK_RESULT = "Car_APP_CHECK_RESULT"
    }
}