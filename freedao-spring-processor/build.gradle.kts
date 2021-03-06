plugins {
    kotlin("jvm")
}

dependencies {
    api(projects.freedaoClassicProcessor)
    implementation(libs.spring.context)

}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
