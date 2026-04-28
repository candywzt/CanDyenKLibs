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

/**
 * 修改Jar任务
 * 插入依赖Class
 * 修改清单文件
 */
tasks.withType<Jar>().configureEach {
    isZip64 = true
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("CDKLib_Java")
    manifest {
        attributes["Implementation-Title"] = "CDKLibs-Java"
        attributes["Manifest-Version"] = "1.0"
        attributes["Main-Class"] = "candyenk.java.Test"
    }
    from({
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    })
}

/**
 * 打包源码Jar
 */
tasks.register<Jar>("sourcesJar") {
    isZip64 = true
    group = "build"
    description = "打包主工程源码及依赖源码"
    archiveBaseName.set("CDKLibJava")
    archiveAppendix.set("sources")
    manifest {
        attributes["Implementation-Title"] = "CDKLibs-Java"
        attributes["Manifest-Version"] = version.toString()
    }
    from(sourceSets.main.get().allSource)
    from(
        configurations.runtimeClasspath.map { config ->
            config.incoming.artifactView { ->
                withVariantReselection()
                attributes {
                    attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
                    attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
                    attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
                }
            }.files.map { file ->
                zipTree(file)
            }
        }
    )
    exclude("**/*.class")
//    configurations.runtimeClasspath.get().forEach { file ->
//        val parentDir = file.parentFile.parentFile
//        var sourceJarFound = false
//
//        parentDir.walkTopDown().forEach { child ->
//            if (child.isDirectory) {
//                val sourceJar = File(child, file.name.replace(".jar", "-sources.jar"))
//                if (sourceJar.exists()) {
//                    from(zipTree(sourceJar)) {
//                        exclude("module-info.java")
//                    }
//                    sourceJarFound = true
//                }
//            }
//        }
//
//        if (!sourceJarFound) {
//            if (file.isDirectory) {
//                from(file)
//            } else {
//                from(zipTree(file))
//            }
//        }
//    }
}
