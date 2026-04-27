plugins {
    id("com.android.application")
}
android {
    namespace = "com.candyenk.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = namespace
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        viewBinding = true;
        buildConfig = true;
        aidl = true;
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
    api(project(":libs-android"))
    implementation("org.litepal.guolindev:core:3.2.3")//LitePal
    implementation("org.luaj:luaj-jse:3.0.1")//LuaJ
}
