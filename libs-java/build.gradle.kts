plugins {
    id("java")
    id("java-library")
}

version = 0.1

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    api("com.google.code.gson:gson:2.14.0")//Gson
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

/**
 * 修改Jar任务
 * 插入依赖Class
 * 修改清单文件
 * 添加启动类
 */
tasks.withType<Jar>().configureEach {
    isZip64 = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("CDKLibJava")
    destinationDirectory.set(layout.projectDirectory.dir("release"))
    manifest {
        attributes["Implementation-Title"] = archiveBaseName.get()
        attributes["Manifest-Version"] = version
        attributes["Main-Class"] = "candyenk.java.Test"
    }
//    插入依赖Class
//    from(configurations.runtimeClasspath.get().map {
//        if (it.isDirectory) it else zipTree(it)
//    })
}

/**
 * 打包源码Jar
 */
tasks.register<Jar>("PKG") {
    isZip64 = true
    group = "build"
    description = "打包源码"
    archiveBaseName.set("CDKLibJava")
    archiveClassifier.set("sources")
    dependsOn("jar")
    destinationDirectory.set(layout.projectDirectory.dir("release"))
    from(sourceSets.main.get().allSource)
}
