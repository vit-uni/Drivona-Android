pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.rongcloud.cn/repository/maven-releases/") }
        maven { url = uri("https://jitpack.io") }
        //极光
        // 配置HMS Core SDK的Maven仓地址。
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }



        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://maven.aliyun.com/repository/public/")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://mvn.cloud.alipay.com/nexus/content/repositories/open/")}

//        maven { url = uri("http://maven.aliyun.com/nexus/content/groups/public")}
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
        maven { url = uri("https://maven.rongcloud.cn/repository/maven-releases/") }
        maven { url = uri("https://jitpack.io") }
        //极光
        // 配置HMS Core SDK的Maven仓地址。
        maven { url = uri("https://developer.huawei.com/repo/") }
        maven { url = uri("https://dl.google.com/dl/android/maven2/") }

        // 1.添加MobSDK Maven地址


        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/jcenter") }
        maven { url = uri("https://maven.aliyun.com/repository/public/")}
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin")}
        maven { url = uri("https://mvn.cloud.alipay.com/nexus/content/repositories/open/")}

//        maven { url = uri("http://maven.aliyun.com/nexus/content/groups/public")}
    }
}

rootProject.name = "SpeedProject"
include(":app", ":Library:Base", /*":Library:alipay", */":Library:bugly", /*":Library:umeng",*/ ":Library:widget", ":Library:permission", ":Library:imagepicker", ":Library:banner", ":indicatorseekbar",":blurview")
include(":blecore")
