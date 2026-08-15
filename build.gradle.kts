import groovy.json.JsonOutput
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

plugins {
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
}

val modVersion: String by project
val minecraftVersion: String by project
val loaderVersion: String by project
val modName: String by project

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    implementation("net.fabricmc:fabric-loader:$loaderVersion")
    implementation(files("ncore.jar")) // Put your path to NCore here
}

tasks {
    val generatedResourcesDir = layout.buildDirectory.file("generated/resources").get().asFile
    val mixinFileName = "s.json"

    processResources {
        val mixinFiles = mutableListOf<String>()
        file("src/main/java/neionew/mixins").walkTopDown().filter { it.isFile && it.extension == "java" }.forEach { file ->
            val className = file.path.substringAfterLast("/").removeSuffix(".java")
            mixinFiles.add(className)
        }

        val mixinJson = mapOf("package" to "neionew.mixins", "client" to mixinFiles)
        val fabricJson = mapOf(
            "schemaVersion" to 1,
            "id" to modName,
            "version" to modVersion,
            "name" to modName,
            "authors" to listOf("Neion"),
            "environment" to "client",
            "entrypoints" to mapOf("client" to listOf("neionew.Neionew")),
            "mixins" to listOf(mixinFileName),
            "depends" to mapOf(
                "fabricloader" to ">=${loaderVersion}",
                "minecraft" to minecraftVersion,
                "ncore" to "1.0.4"
            )
        )

        val outputFile = File(generatedResourcesDir, mixinFileName)
        val fabricFile = File(generatedResourcesDir, "fabric.mod.json")
        outputFile.parentFile.mkdirs()
        fabricFile.parentFile.mkdirs()
        outputFile.writeText(JsonOutput.toJson(mixinJson))
        fabricFile.writeText(JsonOutput.toJson(fabricJson))
        from(generatedResourcesDir)
        filteringCharset = "UTF-8"
    }

    withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }

    jar {
        archiveFileName.set("$modName-$modVersion.jar")
        doLast {
            val jarFile = outputs.files.singleFile
            val tmpFile = jarFile.resolveSibling("${jarFile.name}.tmp")

            ZipFile(jarFile).use { zipIn ->
                ZipOutputStream(tmpFile.outputStream()).use { zipOut ->
                    zipIn.entries().toList().filterNot { entry -> entry.name.startsWith("META-INF/") }
                        .forEach { entry ->
                            zipOut.putNextEntry(entry)
                            zipIn.getInputStream(entry).copyTo(zipOut, 8192)
                        }
                }
            }
            jarFile.delete()
            tmpFile.renameTo(jarFile)
        }
    }
}