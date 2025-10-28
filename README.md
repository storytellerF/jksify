# Jksify Plugin

[![JitPack version badge](https://jitpack.io/v/storytellerF/jksify.svg)](https://jitpack.io/#storytellerF/jksify)

A Gradle plugin that simplifies Android app signing by decoding base64-encoded keystore files during the build process.

## Overview

Jksify helps automate the process of handling signing keys for Android applications. Instead of storing sensitive `.jks` files directly in your repository, you can store them as base64-encoded environment variables and let this plugin decode them during the build process.

## Features

- Decodes base64-encoded keystore files during build
- Securely handles signing keys through environment variables
- Automatically integrates with Android release builds
- Customizable output location for generated keystore files

## Installation

### Using the plugins DSL (Recommended)

In your app-level `build.gradle.kts` file:

```kotlin
plugins {
    id("com.storyteller_f.jksify") version "0.0.1"
}
```

### Using legacy plugin application

Add the following to your app-level `build.gradle.kts` file:

```kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.storyteller_f.jksify:jksify-plugin:0.0.1")
    }
}

apply(plugin = "com.storyteller_f.jksify")
```

## Configuration

### Environment Setup

Set the environment variable with your base64-encoded keystore:

```bash
export storyteller_f_sign_key="base64_encoded_keystore_here"
```

Alternatively, you can configure it directly in your `build.gradle.kts`:

```kotlin
jksify {
    signKey.set("base64_encoded_keystore_here")
}
```

### Extension Properties

You can customize the plugin behavior using the following properties:

```kotlin
jksify {
    // The base64-encoded keystore (defaults to environment variable storyteller_f_sign_key)
    signKey.set("base64_encoded_keystore_here")
    
    // Output path for the generated keystore file (defaults to build/signing/signing_key.jks)
    generatedJksFile.set(file("custom/path/to/keystore.jks"))
}
```

## How It Works

1. During the build process, the plugin looks for either:
   - An environment variable named `storyteller_f_sign_key`
   - A configured `signKey` property in the extension

2. If found, it decodes the base64 value into a `.jks` file at the specified location

3. The generated keystore file can then be used in your signing configuration:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("build/signing/signing_key.jks") // Default location
            storePassword = "your_store_password"
            keyAlias = "your_key_alias"
            keyPassword = "your_key_password"
        }
    }
    
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## Building from Source

To build the plugin from source:

1. Clone the repository
2. Run `./gradlew publishToMavenLocal` to publish to your local Maven repository
3. Add `mavenLocal()` to your project's repositories
4. Apply the plugin as described above

## Security Considerations

- Never commit base64-encoded keystores directly to your repository
- Use CI/CD environment variables to store your base64-encoded keystores
- Ensure proper access controls on your CI/CD environment

## Publishing

To publish a new version of the plugin:

1. Update the version in `gradle.properties`
2. Run `./publish.sh`

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
