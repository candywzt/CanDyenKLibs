plugins {
    id("com.android.library")
}
version = 0.1

android {
    namespace = "candyenk.android"
    compileSdk = 37

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
        compose = true
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

    //Android X
    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.annotation:annotation:1.9.1")
    api("androidx.activity:activity:1.8.1")
    api("androidx.fragment:fragment:1.8.9")

    //JetPack Compose
    api("androidx.compose.runtime:runtime:1.11.0")
    api("androidx.compose.ui:ui:1.11.0")
    api("androidx.compose.ui:ui-tooling-preview:1.11.0")
    api("androidx.compose.foundation:foundation:1.11.0")
    debugImplementation("androidx.compose.ui:ui-tooling:1.11.0")

    //Google Material
    api("com.google.android.material:material:1.10.0")

    //OkHttp3
    api("com.squareup.okio:okio:3.17.0")
    api("com.squareup.okhttp3:okhttp:5.3.2")

    //Mail
    api("com.sun.mail:android-mail:1.6.8")
    api("com.sun.mail:android-activation:1.6.8")

    //Shizuku
    api("dev.rikka.shizuku:api:13.1.5")
    api("dev.rikka.shizuku:provider:13.1.5")

    //Xposed
    api("de.robv.android.xposed:api:82")

    //HideApi
    api("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")


}
/**
 * 打包Release Aar和源码Jar
 */
tasks.register<Jar>("PKG") {
    isZip64 = true
    group = "build"
    description = "打包主工程源码及依赖源码"
    archiveBaseName.set("CDKLibAndroid")
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
    destinationDirectory.set(layout.projectDirectory.dir("release"))
    dependsOn("assembleRelease")
    doLast {
        copy {
            from(layout.buildDirectory.file("outputs/aar/${project.name}-release.aar").get().asFile)
            into(destinationDirectory)
            rename { "${archiveBaseName.get()}-${archiveVersion.get()}.aar" }
        }
    }
}
