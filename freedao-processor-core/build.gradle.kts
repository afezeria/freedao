plugins {
    java
    kotlin("jvm")
}

dependencies {
    api(projects.freedaoCore)

    api(libs.bundles.kotlin)

    api(libs.kotlinx.metadata)
    api(libs.javapoet)
    api(libs.antlr)

}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
