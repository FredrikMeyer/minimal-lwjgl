plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

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
    // The BOM aligns the versions of every LWJGL module below.
    implementation(platform(libs.lwjgl.bom))
    // To add another LWJGL module, declare it in gradle/libs.versions.toml
    // and add it to the `lwjgl` bundle plus the natives list below.
    implementation(libs.bundles.lwjgl)
    implementation(libs.jspecify)
    implementation(libs.joml)

    runtimeOnly(variantOf(libs.lwjgl.core) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjgl.glfw) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjgl.opengl) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjgl.stb) { classifier(lwjglNatives) })
}

tasks.withType<JavaCompile>().configureEach {
    // Treat compiler warnings as errors so lint regressions fail the build.
    options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror"))
}

tasks.clean {
    delete += setOf("bin")
}

testing {
    suites {
        // Configure the built-in test suite
        getByName<JvmTestSuite>("test") {
            // Use JUnit Jupiter test framework
            useJUnitJupiter(libs.versions.junit.jupiter)
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
    mainClass = "net.fredrikmeyer.opengl.algsurface.App"
    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
}
