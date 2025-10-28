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
