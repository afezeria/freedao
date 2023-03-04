@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.freedaoClassicProcessor)
    implementation(libs.spring.context)

}
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
