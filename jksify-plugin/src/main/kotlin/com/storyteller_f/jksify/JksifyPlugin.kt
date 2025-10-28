package com.storyteller_f.jksify

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register
import java.io.FileOutputStream
import java.util.Base64

abstract class DecodeBase64ToStoreFileTask : DefaultTask() {

    @get:Input
    abstract val signKey: Property<String>

    @get:OutputFile
    abstract val generatedJksFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val key = signKey.orNull
        if (!key.isNullOrBlank()) {
            val outputFile = generatedJksFile.get().asFile
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

class JksifyPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val decodeBase64ToStoreFileTask = project.tasks.register<DecodeBase64ToStoreFileTask>("decodeBase64ToStoreFile") {
            group = "signing"
            signKey.set(System.getenv("storyteller_f_sign_key") ?: "")
            generatedJksFile.set(project.layout.buildDirectory.file("signing/signing_key.jks"))
        }

        project.afterEvaluate {
            project.tasks.findByName("packageRelease")?.dependsOn(decodeBase64ToStoreFileTask)
        }
    }
}
