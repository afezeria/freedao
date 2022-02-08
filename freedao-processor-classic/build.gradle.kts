plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.freedaoProcessorCore)
    api(projects.freedaoRuntimeClassic)
    api(libs.springContext)
}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
