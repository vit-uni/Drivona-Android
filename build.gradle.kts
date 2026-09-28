import org.jetbrains.kotlin.gradle.utils.addExtendsFromRelation

// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.resolverConvention) apply false
}


buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.12.0")

        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
//        classpath("com.mob.sdk:MobSDK:2018.0319.1724")
//        classpath("com.google.gms:google-services:4.3.8")
        classpath("com.huawei.agconnect:agcp:1.6.0.300")
        classpath("com.google.guava:guava:30.1.1-jre")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7")
        classpath("org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.15")
        classpath("org.codehaus.groovy:groovy-all:2.4.1")

        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}


ext {
    set("compileSdk", 36)
    set("minSdk", 24)
    set("targetSdk", 36)
    //动态配置
    set("versionCode", 102)
    set("versionName", "1.0.2")
    // "第三方库"
    set("okhttp", "4.9.3")
    set("gson", "2.8.6")
    set("arouter", "1.5.2")
    set("easypermissions", "3.0.0")
    set("glide", "4.13.0")
    set("glideTrans", "4.3.0")
    set("indicatorView", "2.1.3")
    set("rxjava", "3.0.2")
    set("rxandroid", "3.0.0")
    set("rxbinding", "4.0.0")
    set("roundimageview", "2.3.0")
    set("baseAdapter", "3.0.7")
    set("emojiIos", "0.8.0")
    set("emojiJava", "5.1.1")
    set("smartRefresh", "2.0.5")
    set("floatWindow", "1.0.9")
    set("pictureselector", "v3.0.9")
    set("lottieVersion", "5.2.0")

    // 友盟 AppKey
    set("UMENG_APP_KEY", "646c850fba6a5259c45753ed")
    // QQ AppId
    set("QQ_APP_ID", "102040217")
    // QQ Secret-
    set("QQ_APP_SECRET", "vjU2OVO2VjiSIuHO")
    // 微信 AppId
    set("WX_APP_ID", "wxb62795e498c847d1")
    // 微信 Secret
    set("WX_APP_SECRET", "bcea00c921d27948e385a18e54ab397f")

    set("mode", 0)
    set("android_ui", buildMap {
        set("appcompat", "androidx.appcompat:appcompat:1.3.1")
        set("material", "com.google.android.material:material:1.4.0")
    })
}