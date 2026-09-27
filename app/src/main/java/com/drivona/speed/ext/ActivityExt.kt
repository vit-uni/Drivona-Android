package com.drivona.speed.ext

import android.app.Activity
import android.content.Context
import android.content.Intent


inline fun <reified T: Activity> Context.start(bunfle: Intent.() -> Unit = {}) {
    val starter = Intent(this, T::class.java)
    bunfle.invoke(starter)
    startActivity(starter)
}