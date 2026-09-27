package com.drivona.speed

//import com.lalifa.ext.Tools.Companion.parser
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.net.http.HttpResponseCache
import android.os.Build
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.multidex.MultiDex
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.Ble.InitCallback
import cn.com.heaton.blelibrary.ble.BleLog
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.model.BleFactory
import cn.com.heaton.blelibrary.ble.utils.UuidUtils
import com.drake.statelayout.StateConfig
import com.drake.tooltip.ToastConfig
import com.drake.tooltip.interfaces.ToastFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.lalifa.api.InitNet
import com.lalifa.base.BaseApplication
import com.lalifa.ext.ActivityManager
import com.lalifa.ext.Tools
import com.lalifa.ext.Tools.Companion.BLE_Read_ID
import com.lalifa.ext.Tools.Companion.BLE_SERVICE_ID
import com.lalifa.ext.Tools.Companion.BLE_Write_ID
import com.lalifa.extension.pk
import com.drivona.speed.api.BleRssiDevice
import com.google.android.libraries.mapsplatform.turnbyturn.model.NavInfo
import com.google.android.libraries.navigation.NavigationApi
import com.google.android.libraries.navigation.Navigator
import com.lalifa.utils.SPUtil
import com.tencent.bugly.crashreport.CrashReport
import java.io.File
import java.util.UUID


class MApplication : BaseApplication() {
    //导航信息，你可以把路线、转向、剩余距离、目的地放这里
    val navInfoMutableLiveData: MutableLiveData<NavInfo> = MutableLiveData()
    lateinit var navigator: Navigator
        private set
    companion object {
        private lateinit var INSTANCE: MApplication
        lateinit var placesClient: PlacesClient
        fun get() = INSTANCE
    }
    /** 对外推送导航数据 */
    fun updateNavInfo(info: NavInfo) {
        navInfoMutableLiveData.postValue(info)
    }
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        ActivityManager.getInstance().init(this)
        initConfig()
        // Initialize the SDK
        Places.initialize(applicationContext, Tools.GoogleKey)

        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)

        NavigationApi.getNavigator(
            this,
            object : NavigationApi.NavigatorListener {
                override fun onError(p0: Int) {

                }

                override fun onNavigatorReady(
                    navigator: Navigator
                ) {

                    this@MApplication.navigator =
                        navigator
                }



            }
        )
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * 初始化通用吐司View
     */
    private fun initToast() {
        ToastConfig.initialize(get(), object : ToastFactory {
            override fun onCreate(
                context: Application,
                message: CharSequence,
                duration: Int,
                tag: Any?,
            ): Toast? {
                val toast = Toast.makeText(context, message, duration)
                val view = View.inflate(context, R.layout.view_toast, null)
                view.findViewById<TextView>(android.R.id.message).text = message
                toast.view = view
                toast.setGravity(Gravity.BOTTOM, 0, 0)
                return toast
            }
        })
    }

    /**
     * 初始化日志打印工具 列表空界面
     */
    public fun initConfig() {

        Places.initialize(applicationContext, Tools.GoogleKey)
        //初始化SVGA动画
//        parser.init(this)
        val cacheDir = File(applicationContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)


        //LogCat.setDebug(BuildConfig.DEBUG, get().getString(R.string.app_name))
        initToast()
        StateConfig.apply {
            emptyLayout = R.layout.layout_common_empty
            loadingLayout = R.layout.layout_common_loding
        }
        CrashReport.initCrashReport(applicationContext, "937c75dd4f", false)

        val token = SPUtil.get(Tools.Token)

        InitNet.initNetHttp(this, token.pk())
        initBle()
        initSoundPool()
    }

    //初始化蓝牙
    private fun initBle() {
        var serviceUuidDevice = UUID.fromString(BLE_SERVICE_ID)
        var writeUuidDevice = UUID.fromString(BLE_Write_ID)
        var readUuidDevice = UUID.fromString(BLE_Read_ID)
        Ble.options()
            .setLogBleEnable(true) //设置是否输出打印蓝牙日志
            .setThrowBleException(true) //设置是否抛出蓝牙异常
            .setLogTAG("MyAndroidBLE") //设置全局蓝牙操作日志TAG
            .setAutoConnect(false) //设置是否自动连接
            .setIgnoreRepeat(false) //设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
            .setConnectFailedRetryCount(3) //连接异常时（如蓝牙协议栈错误）,重新连接次数
            .setConnectTimeout((10 * 1000).toLong()) //设置连接超时时长
            .setScanPeriod((12 * 1000).toLong()) //设置扫描时长
            .setMaxConnectNum(7) //最大连接数量
            .setUuidService(serviceUuidDevice) //设置主服务的uuid
            .setUuidWriteCha(writeUuidDevice) //设置可写特征的uuid
            .setUuidReadCha(readUuidDevice) //设置可读特征的uuid （选填）
//            .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("fd03"))) //设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
            .setFactory(object : BleFactory<BleRssiDevice?>() {
                //实现自定义BleDevice时必须设置
                override fun create(address: String?, name: String?): BleRssiDevice? {
                    return BleRssiDevice(address, name) //自定义BleDevice的子类
                }
            })
//            .setBleWrapperCallback(MyBleWrapperCallback())
            .create<BleDevice?>(
                this,
                object : InitCallback {
                    override fun success() {
                        BleLog.e("MainApplication", "初始化成功")
                    }

                    override fun failed(failedCode: Int) {
                        BleLog.e("MainApplication", "初始化失败：" + failedCode)
                    }
                })
    }
    var pool: SoundPool? = null
    private var weixian = 0
    private var chaosu = 0
    private var shexiangtou = 0
    private fun initSoundPool() {
        if (Build.VERSION.SDK_INT >= 21) {
            val builder = SoundPool.Builder()
            builder.setMaxStreams(2)
            var audio = AudioAttributes.Builder()
            audio.setLegacyStreamType(AudioManager.STREAM_MUSIC)
            builder.setAudioAttributes(audio.build())
            pool = builder.build()
        } else {
            pool = SoundPool(2, AudioManager.STREAM_MUSIC, 0)
        }
        weixian = pool?.load(this, R.raw.weixian, 1)!!
        chaosu = pool?.load(this, R.raw.chaosu, 1)!!
        shexiangtou = pool?.load(this, R.raw.shexiangtou, 1)!!


    }
    fun playWeixian() {
         pool?.play(weixian, 1f, 1f, 0, 0, 1f)!!
    }
    fun playChaosu() {
         pool?.play(chaosu, 1f, 1f, 0, 0, 1f)!!
    }
    fun playShexiangtou() {
         pool?.play(shexiangtou, 1f, 1f, 0, 0, 1f)!!
    }
}