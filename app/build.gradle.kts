@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.drivona.speed"
    compileSdk = rootProject.ext.get("compileSdk") as Int

    defaultConfig {
        applicationId = "com.drivona.speed"
        minSdk = rootProject.ext.get("minSdk") as Int
        targetSdk = rootProject.ext.get("targetSdk") as Int
        versionCode = rootProject.ext.get("versionCode") as Int
        versionName = rootProject.ext.get("versionName") as String
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters.add("armeabi")
            abiFilters.add("armeabi-v7a")
            abiFilters.add("arm64-v8a")
        }

        manifestPlaceholders.apply {
            put("JPUSH_PKGNAME", "com.drivona.speed")
            put("JPUSH_APPKEY", "1869c5d19644afdf8fb18860")//JPush 上注册的包名对应的 Appkey.
            put("JPUSH_CHANNEL", "developer-default")
        }

        aaptOptions.cruncherEnabled = false
        aaptOptions.useNewCruncher = false

        buildConfigField("String", "WX_APPID", "\"${rootProject.ext.get("WX_APP_ID") as String}\"")

        addManifestPlaceholders(mutableMapOf<String, Any>().apply {
            put("WX_APPID", rootProject.ext.get("WX_APP_ID") as String)
        })

        buildConfigField("boolean", "NeedLogger", "true")
        buildConfigField("String", "AUTH_SECRET", "\"rYsiiJCY+zdebr9DXIu4iKcbbochuGFKfXEcJ+XZxhX358s8xzpRmb1tAB8UrANvO3XJ9KbRTIeW5Zm/fhIG9sbgtQQunA9Jfft+JDOYO020LGpDoSYlYSTofPI9QDs2Tw5eHtNANfYa6NqrEGCSEGN0opDiAMFoeCDs+0CyLFk0GGFUePCJnn5fiTQa7AAfjiiCZG71VtChhnj6cQaVDWd2vLvPf8Qq2B4m/kMope5GzLdygo7DuV8Ca+mjocfySoiGRrmqKOslQJCOT7MihLD5MuZsBCCSk20F87LnAAA9HfAW7+Q3Lw==\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    signingConfigs {
        //812932c0a266c4ff034199a1cdd47207
        getByName("debug").apply {
            storeFile = file("speed.jks")
            storePassword = "lalifa"
            keyAlias = "key0"
            keyPassword = "lalifa"
            enableV1Signing = true
            enableV2Signing = true
        }

        create("release") {
            //812932c0a266c4ff034199a1cdd47207
            storeFile = file("speed.jks")
            storePassword = "lalifa"
            keyAlias = "key0"
            keyPassword = "lalifa"
            enableV1Signing = true
            enableV2Signing = true
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    //逃避图片审查
    aaptOptions.cruncherEnabled = false
    aaptOptions.useNewCruncher = false
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = "18"
    }
    lintOptions.apply {
        disable.apply {
            add("checkReleaseBuilds")
            add("abortOnError")
        }
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
//    kotlin {
//        jvmToolchain(17)
//    }
}

dependencies {
//    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(project(":Library:Base"))
    implementation(project(":Library:widget"))
    implementation(project(":Library:bugly"))
    implementation(project(":blecore"))
    implementation(project(":indicatorseekbar"))
    implementation(project(":blurview"))
    implementation(libs.floatWindow)
//    implementation(libs.banner)
    implementation(libs.voicerecorder)
    implementation(libs.androidx.car.app)
    // 如果需要地图功能
    implementation("androidx.car.app:app-projected:1.6.0")
//    implementation(libs.rxpay)
//    kapt(libs.rxpayCompiler)

//    implementation("com.google.android.gms:play-services-maps:19.2.0")

//    implementation("com.google.android.maps:google_turnbyturn:1.0.0")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")
    api("com.google.android.libraries.navigation:navigation:6.3.3")
    api("com.google.android.libraries.places:places:4.0.0"){
        exclude(group = "com.google.android.gms", module = "play-services-maps")
    }

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("com.google.guava:guava:32.1.3-jre")
    implementation("com.github.zhpanvip:viewpagerindicator:1.2.3")
//    implementation("com.zhpan.library:viewpagerindicator:3.3.0")
//    implementation("com.inuker.bluetooth:library:1.4.0")
}