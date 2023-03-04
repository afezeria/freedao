@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.freedaoCoreProcessor)
    api(projects.freedaoClassicRuntime)
}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
