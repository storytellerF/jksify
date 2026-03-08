package com.storyteller_f.jksify

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register
import java.io.FileOutputStream
import java.util.*

abstract class DecodeBase64ToStoreFileTask : DefaultTask() {

    @get:Input
    abstract val base64Encoded: Property<String>

    @get:OutputFile
    abstract val outputJKSFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val key = base64Encoded.orNull
        if (!key.isNullOrBlank()) {
            val outputFile = outputJKSFile.get().asFile
            outputFile.parentFile?.let {
                if (!it.exists() && !it.mkdirs()) {
                    throw Exception("mkdirs failed: $it")
                }
            }
            if (!outputFile.exists() && !outputFile.createNewFile()) {
                throw Exception("create failed: $outputFile")
            }
            val decodedBytes = Base64.getDecoder().decode(key)
            FileOutputStream(outputFile).use { it.write(decodedBytes) }
            println("Base64 decoded and written to: $outputFile")
        } else {
            println("skip decodeBase64ToStoreFile")
        }
    }
}

interface JksifyExtension {
    val signKey: Property<String>
    val generatedJksFile: RegularFileProperty
}

class JksifyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create<JksifyExtension>("jksify")
        extension.signKey.convention(getenv("storyteller_f_sign_key") ?: "")
        extension.generatedJksFile.convention(project.layout.buildDirectory.file("signing/signing_key.jks"))

        val decodeBase64ToStoreFileTask = project.tasks.register<DecodeBase64ToStoreFileTask>("decodeBase64ToStoreFile") {
            group = "signing"
            base64Encoded.set(extension.signKey)
            outputJKSFile.set(extension.generatedJksFile)
        }

        project.afterEvaluate {
            tasks.findByName("packageRelease")?.dependsOn(decodeBase64ToStoreFileTask)
        }
    }
}

fun getenv(key: String): String? {
    return System.getenv(key.lowercase()) ?: System.getenv(key.uppercase())
}
