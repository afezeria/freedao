plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.freedaoCoreProcessor)
    api(projects.freedaoClassicRuntime)
}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
