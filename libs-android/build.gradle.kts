plugins {
    id("com.android.library")
}
version = 0.1

android {
    namespace = "candyenk.android"
    compileSdk = 36

    defaultConfig {
        minSdk = 30
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        buildConfig = true
        aidl = true
    }
    packaging {
        resources {
            excludes += "/META-INF/**"
        }
    }

}
dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar"))))
    api(project(":libs-java"))
    api("androidx.appcompat:appcompat:1.6.1")//AndroidX
    api("androidx.annotation:annotation:1.9.1")//AndroidX
    api("androidx.activity:activity:1.8.1")//AndroidX
    api("androidx.fragment:fragment:1.8.9")//AndroidX
    api("com.google.android.material:material:1.10.0")//Google MD
    api("com.squareup.okio:okio:3.17.0")//OkHttp3
    api("com.squareup.okhttp3:okhttp:5.3.2")//OkHttp3
    api("com.sun.mail:android-mail:1.6.8")//Mail
    api("com.sun.mail:android-activation:1.6.8")//Mail
    api("dev.rikka.shizuku:api:13.1.5")//Sui
    api("dev.rikka.shizuku:provider:13.1.5")//Shizuku
    api("de.robv.android.xposed:api:82")//Xposed
    api("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")//隐藏API调用
}


