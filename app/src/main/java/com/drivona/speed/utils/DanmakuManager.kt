package com.drivona.speed.utils

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.lalifa.extension.sp
import com.lalifa.utils.UiUtils
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.controller.IDanmakuView
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import java.util.concurrent.ExecutionException


class DanmakuManager(private val context: Activity, private val danmakuView: IDanmakuView) {
    private val danmakuContext: DanmakuContext = DanmakuContext.create()

    init {
        val parser = object : BaseDanmakuParser() {
            override fun parse(): Danmakus {
                return Danmakus()
            }
        }
        danmakuView.setCallback(object : master.flame.danmaku.controller.DrawHandler.Callback {
            override fun updateTimer(timer: DanmakuTimer?) {
            }

            override fun drawingFinished() {
            }

            override fun danmakuShown(danmaku: BaseDanmaku?) {
            }

            override fun prepared() {
                danmakuView.start()
            }
        })

        /**
         * description : 缓存相关的内容
         */
        val mBackgroundCacheStuffer: BaseCacheStuffer.Proxy = object : BaseCacheStuffer.Proxy() {
            override fun prepareDrawing(danmaku: BaseDanmaku, fromWorkerThread: Boolean) {
                // 根据你的条件检查是否需要需要更新弹幕
            }

            override fun releaseResource(danmaku: BaseDanmaku) {
                //清理相应的数据
                danmaku.tag = null
            }
        }

        //设置最大显示行数
        val maxLInesPair = HashMap<Int, Int>(16)
        maxLInesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5
        //设置是否禁止重叠
        val overlappingEnablePair = HashMap<Int, Boolean>(16)
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true
        //设置一些相关的配置
        danmakuContext.setDuplicateMergingEnabled(false)
            //是否重复合并
            .setScrollSpeedFactor(1.2f)
            //设置文字的比例
            .setScaleTextSize(1.2f)
            //图文混排的时候使用！这里可以不用
            .setCacheStuffer(MyCacheStuffer(context), mBackgroundCacheStuffer)
            //设置显示最大行数
            .setMaximumLines(maxLInesPair)
            //设置防，null代表可以重叠
            .preventOverlapping(overlappingEnablePair);
        //设置解析器
//        val defaultDanmakuParser = getDefaultDanmakuParser()

        danmakuView.prepare(parser, danmakuContext)
        danmakuView.enableDanmakuDrawingCache(true)
    }

    fun addDanmaku(content: String, color: String, speed: Float, userHeader: String) {
        Glide.with(context)
            .asBitmap() //必须
            .load(userHeader)
            .centerCrop()
            .into(object : Target<Bitmap> {
                override fun onStart() {

                }

                override fun onStop() {

                }

                override fun onDestroy() {

                }

                override fun onLoadStarted(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun getSize(cb: SizeReadyCallback) {
                    cb.onSizeReady(UiUtils.dp2px(23f), UiUtils.dp2px(23f))
                }

                override fun removeCallback(cb: SizeReadyCallback) {

                }

                override fun setRequest(request: Request?) {

                }

                override fun getRequest(): Request? {
                    return null
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL)
                    danmaku?.let {
                        val map: MutableMap<String, Any> = HashMap(16)
                        map["content"] = content
                        map["bitmap"] = BitmapUtils.getShowPicture(resource)

                        map["color"] = "#66000000"
                        map["text_color"] = color
                        it.tag = map
                        it.padding = 10 // 设置头像和弹幕之间的间距
                        it.text = ""
//                        it.text = createSpannable(content, BitmapUtils.getShowPicture(resource))
                        it.textSize = 8.sp * (context.resources.displayMetrics.density - 0.6f)
                        it.setTime(danmakuView.currentTime)
//                        it.duration = Duration((danmakuView.width / speed * 10).toLong())
                        danmaku.priority = 1
                        danmaku.isLive = false
                        // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
//                        it.textShadowColor = 0
                        //设置阴影的颜色
                        it.textShadowColor = Color.WHITE
                        // danmaku.underlineColor = Color.GREEN;
                        //设置背景颜色
                        // danmaku.underlineColor = Color.GREEN;
                        //设置背景颜色
                        it.borderColor = Color.GREEN
                        danmakuView.addDanmaku(it)
                    }
                }
            })
    }

    /**
     * 创建图文混排模式
     *
     * @param drawable
     * @return
     */
//    fun createSpannable(content: String, drawable: Bitmap): SpannableStringBuilder {
//        var text: String = content
//        var spannableStringBuilder = SpannableStringBuilder(content)
//        var span = CenteredImageSpan(context, drawable)//ImageSpan.ALIGN_BOTTOM);
//        spannableStringBuilder.setSpan(span, 0, text.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        spannableStringBuilder.append(text);
//        spannableStringBuilder.setSpan(BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        return spannableStringBuilder;
//    }

    fun getImage(url: String?, width: Int, height: Int): Bitmap? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        try {
            return Glide.with(context)
                .asBitmap()
                .load(url)
                .into(width, height)
                .get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }

    fun setMaxDanmakuCount(count: Int) {
        danmakuContext.setMaximumVisibleSizeInScreen(count)
    }

    fun setMaxLines(lines: Int) {
//        danmakuContext.setMaximumLines(lines)
    }

    fun getDefaultDanmakuParser(): BaseDanmakuParser? {
        return object : BaseDanmakuParser() {
            override fun parse(): IDanmakus {
                return Danmakus()
            }
        }
    }
}