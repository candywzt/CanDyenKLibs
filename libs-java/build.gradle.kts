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
    api("com.google.code.gson:gson:2.14.0")
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}
//打包源码Jar
tasks.register<Jar>("sourcesJar") {
    isZip64 = true

    // 添加 Java 源码
    from(sourceSets.main.get().allSource)

    // 插入依赖包源码
    configurations.runtimeClasspath.get().forEach { file ->
        val parentDir = file.parentFile.parentFile
        var sourceJarFound = false

        parentDir.walkTopDown().forEach { child ->
            if (child.isDirectory) {
                val sourceJar =
                    File(child, file.name.replace(".jar", "-sources.jar"))
                if (sourceJar.exists()) {
                    from(zipTree(sourceJar)) {
                        exclude("module-info.java")
                    }
                    sourceJarFound = true
                }
            }
        }

        if (!sourceJarFound) {
            if (file.isDirectory) {
                from(file)
            } else {
                from(zipTree(file))
            }
        }
    }

    // Manifest
    manifest {
        attributes["Implementation-Title"] = "CDKLibs-Java"
        attributes["Manifest-Version"] = version.toString()
    }

    // 文件名
    archiveBaseName.set("CDKLibJava")
    archiveAppendix.set("sources")
}
tasks.withType<Jar>().configureEach {
    // 支持大型压缩包
    isZip64 = true

    // 文件冲突策略：保留首个
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // 插入依赖包 class
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })

    // Manifest 写入
    manifest {
        attributes["Implementation-Title"] = "CDKLibs-Java"
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "candyenk.java.Test"
    }

    // 文件名
    archiveBaseName.set("CDKLib_Java")
}
