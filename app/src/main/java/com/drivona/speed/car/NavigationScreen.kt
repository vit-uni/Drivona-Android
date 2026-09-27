package com.drivona.speed.car

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Looper
import android.util.Log
import android.view.Surface
import androidx.car.app.AppManager
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.Template
import androidx.car.app.navigation.NavigationManager
import androidx.car.app.navigation.NavigationManagerCallback
import androidx.car.app.navigation.model.NavigationTemplate

// NavigationScreen.kt
class NavigationScreen(
    carContext: CarContext,
    private val destinationName: String
) : Screen(carContext) {

    private var navigationStarted = false
    private var currentStep = "准备导航"
    private var remainingDistance = "0.0 km"
    private var estimatedTime = "0 min"

    // 模拟导航步骤
    private val navigationSteps = listOf(
        "前方直行 500 米",
        "前方路口右转",
        "继续直行 1.2 公里",
        "前方左转进入主路",
        "到达目的地"
    )
    private var stepIndex = 0

    init {
        // 注册 SurfaceCallback 接收地图绘制 Surface
        val appManager = carContext.getCarService(AppManager::class.java)
        appManager.setSurfaceCallback(object : SurfaceCallback {
            override fun onSurfaceAvailable(surfaceContainer: SurfaceContainer) {
                // 开始绘制地图
                drawMap(surfaceContainer.surface!!)
            }

            override fun onSurfaceDestroyed(surfaceContainer: SurfaceContainer) {
                // 清理资源
            }

            override fun onVisibleAreaChanged(visibleArea: Rect) {
                // 可见区域变化，调整地图显示
            }
        })

        // 注册 NavigationManager 回调
        setupNavigationManager()
    }

    private fun setupNavigationManager() {
        try {
            val navigationManager = carContext.getCarService(CarContext.NAVIGATION_SERVICE) as NavigationManager

            // 设置回调（必须在主线程调用）
            if (Looper.myLooper() == Looper.getMainLooper()) {
                navigationManager.setNavigationManagerCallback(
                    object : NavigationManagerCallback {
                        override fun onStopNavigation() {
                            // 处理停止导航指令
                            stopNavigation()
                        }
                    }
                )
            }
        } catch (e: Exception) {
            Log.e("NavigationScreen", "设置 NavigationManager 失败: ${e.message}")
        }
    }

    private fun drawMap(surface: Surface) {
        // 简单的地图绘制示例，用 Canvas 画一些元素
        // 在实际项目中，这里应该集成地图 SDK
        val canvas = surface.lockHardwareCanvas() ?: return
        try {
            // 背景色
            canvas.drawColor(Color.parseColor("#2C3E50"))

            // 绘制道路（直线模拟）
            val paint = Paint().apply {
                color = Color.WHITE
                strokeWidth = 6f
                style = Paint.Style.STROKE
            }

            // 画一条曲折的路线
            val path = Path().apply {
                moveTo(100f, 800f)
                lineTo(300f, 600f)
                lineTo(500f, 700f)
                lineTo(700f, 400f)
                lineTo(900f, 500f)
            }
            canvas.drawPath(path, paint)

            // 画起点标记
            drawMarker(canvas, 100f, 800f, "起点", Color.GREEN)

            // 画终点标记
            drawMarker(canvas, 900f, 500f, destinationName, Color.RED)

            // 画当前位置标记
            drawMarker(canvas, 300f, 600f, "📍", Color.BLUE)

            // 绘制导航信息（路径上的箭头指示）
            drawNavigationArrow(canvas)

        } finally {
            surface.unlockCanvasAndPost(canvas)
        }
    }

    private fun drawMarker(canvas: Canvas, x: Float, y: Float, label: String, color: Int) {
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
        }

        // 画圆点
        canvas.drawCircle(x, y, 20f, paint)

        // 画标签文字
        val textPaint = Paint().apply {
            this.color = Color.WHITE
            textSize = 40f
            isAntiAlias = true
        }
        canvas.drawText(label, x + 30f, y + 10f, textPaint)
    }

    private fun drawNavigationArrow(canvas: Canvas) {
        // 在路径上画一个箭头表示当前导航方向
        val paint = Paint().apply {
            color = Color.YELLOW
            style = Paint.Style.FILL
        }

        // 简单的三角形箭头
        val arrowPath = Path().apply {
            moveTo(500f, 500f)
            lineTo(480f, 530f)
            lineTo(520f, 530f)
            close()
        }
        canvas.drawPath(arrowPath, paint)
    }

    override fun onGetTemplate(): Template {
        return NavigationTemplate.Builder()
            .setActionStrip(
                ActionStrip.Builder()
                    .addAction(
                        Action.Builder()
                            .setTitle("结束导航")
                            .setOnClickListener { stopNavigation() }
                            .build()
                    )
                    .addAction(
                        Action.Builder()
                            .setTitle("下一指引")
                            .setOnClickListener { nextNavigationStep() }
                            .build()
                    )
                    .build()
            )
            .setMapActionStrip(
                ActionStrip.Builder()
                    .addAction(Action.PAN) // 支持平移


                    .build()
            )


            .build()
    }

    private fun nextNavigationStep() {
        if (stepIndex < navigationSteps.size - 1) {
            stepIndex++
            currentStep = navigationSteps[stepIndex]
            remainingDistance = "${(10 - stepIndex * 2).coerceAtLeast(0)}.0"
            estimatedTime = "${(20 - stepIndex * 4).coerceAtLeast(0)}"
            invalidate() // 刷新界面
        } else {
            // 到达目的地
            showArrivalAlert()
        }
    }

    private fun showArrivalAlert() {

    }

    private fun stopNavigation() {
        try {
            val navigationManager = carContext.getCarService(CarContext.NAVIGATION_SERVICE) as NavigationManager
            navigationManager.navigationEnded()
            navigationManager.clearNavigationManagerCallback()
        } catch (e: Exception) {
            Log.e("NavigationScreen", "停止导航失败: ${e.message}")
        }
        screenManager.pop()
    }

      fun onStart() {

        // 导航开始
        try {
            val navigationManager = carContext.getCarService(CarContext.NAVIGATION_SERVICE) as NavigationManager
            navigationManager.navigationStarted()
            navigationStarted = true
        } catch (e: Exception) {
            Log.e("NavigationScreen", "开始导航失败: ${e.message}")
        }
    }

      fun onStop() {

        if (navigationStarted) {
            stopNavigation()
        }
    }

    companion object {
        private const val TAG = "NavigationScreen"
    }
}