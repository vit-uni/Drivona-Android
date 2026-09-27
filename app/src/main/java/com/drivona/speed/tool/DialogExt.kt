package com.drivona.speed.tool

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import com.drivona.speed.R
import com.lalifa.extension.appendSpan
import com.lalifa.extension.onClick
import per.goweii.layer.core.anim.NullAnimatorCreator
import per.goweii.layer.core.ktx.onInitialize
import per.goweii.layer.core.widget.SwipeLayout
import per.goweii.layer.dialog.DialogLayer
import per.goweii.layer.dialog.ktx.backgroundDimDefault
import per.goweii.layer.dialog.ktx.cancelableOnTouchOutside
import per.goweii.layer.dialog.ktx.contentAnimator
import per.goweii.layer.dialog.ktx.contentView
import per.goweii.layer.dialog.ktx.gravity
import per.goweii.layer.dialog.ktx.swipeDismiss

/**
 * 网络协议弹框
 * @receiver Activity
 * @param callback Function1<[@kotlin.ParameterName] String, Unit>
 */
fun Activity.showAgreeDialog(
    callback: (isAgree: Boolean) -> Unit
) {
    DialogLayer(this)
        .contentView(R.layout.pop_agree)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.CENTER)
        .contentAnimator(NullAnimatorCreator())
        .backgroundDimDefault()
        .addInputMethodCompat(true)
        .onInitialize {
            requireViewById<TextView>(R.id.cancel).onClick {
                callback.invoke(false)
                dismiss()
            }
            requireViewById<TextView>(R.id.agree).onClick {
                callback.invoke(true)
                dismiss()
            }
        }.show()
}

fun Activity.secondaryConfirmationAgreement(vararg agreement: String, callback: (String) -> Unit) {
    DialogLayer(this)
        .contentView(R.layout.pop_secondary_confirmation_agreement)
        .cancelableOnTouchOutside(true)
        .gravity(Gravity.BOTTOM)
        .swipeDismiss(SwipeLayout.Direction.BOTTOM)
        .backgroundDimDefault()
        .addInputMethodCompat(true)
        .onInitialize {

            val agreeBtn = requireViewById<TextView>(R.id.agree_btn)
            agreeBtn.apply {
                for (s in agreement) {
                    appendSpan(s, color = Color.parseColor("#6196FF")) {
                        //用户协议
                        callback.invoke(s)
                    }
                }
            }
            requireViewById<TextView>(R.id.agree).onClick {
                callback.invoke("")
                dismiss()
            }
        }.show()
}