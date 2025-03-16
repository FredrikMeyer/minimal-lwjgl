plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val lwjglVersion = "3.3.6"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else if (arch.startsWith("ppc"))
                "natives-linux-ppc64le"
            else if (arch.startsWith("riscv"))
                "natives-linux-riscv64"
            else
                "natives-linux"

        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos-arm64"

        else ->
            throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}


dependencies {
    // This dependency is used by the application.
    implementation(libs.guava)

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl", "lwjgl")
//    implementation("org.lwjgl", "lwjgl-assimp")
//    implementation("org.lwjgl", "lwjgl-bgfx")
    implementation("org.lwjgl", "lwjgl-glfw")
//    implementation("org.lwjgl", "lwjgl-nuklear")
//    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
//    implementation("org.lwjgl", "lwjgl-par")
    // For textures
    implementation("org.lwjgl", "lwjgl-stb")
//    implementation("org.lwjgl", "lwjgl-vulkan")
    implementation("org.jspecify:jspecify:1.0.0")

    implementation("org.joml:joml:1.10.5")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-bgfx", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-nuklear", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
//    runtimeOnly("org.lwjgl", "lwjgl-par", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
//    if (lwjglNatives == "natives-macos-arm64") runtimeOnly(
////        "org.lwjgl",
////        "lwjgl-vulkan",
////        classifier = lwjglNatives
////    )
}

testing {
    suites {
        // Configure the built-in test suite
        @Suppress("UnstableApiUsage") val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.11.1")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Define the main class for the application.
    mainClass = "net.fredrikmeyer.opengl.minimal.App"
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}
