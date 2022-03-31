plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.freedaoProcessorCore)
    api(projects.freedaoRuntimeClassic)
}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
