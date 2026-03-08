plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
}

val env: MutableMap<String, String> = System.getenv()
group = group.takeIf { it.toString().contains(".") } ?: env["GROUP"]
        ?: "com.storyteller_f.jksify"
version = version.takeIf { it != "unspecified" } ?: env["VERSION"] ?: "0.0.1-local"

gradlePlugin {
    plugins {
        create("jksify") {
            id = "com.storyteller_f.jksify"
            implementationClass = "com.storyteller_f.jksify.JksifyPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/storytellerF/jksify")
            // 最好通过命令行传递
            credentials {
                username = project.findProperty("gpr.user") as String
                password = project.findProperty("gpr.key") as String
            }
        }
    }
}
