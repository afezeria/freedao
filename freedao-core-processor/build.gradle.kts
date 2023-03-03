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

    implementation("io.kotest:kotest-assertions-core:5.5.5")
    implementation("io.kotest:kotest-property:5.5.5")

}
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
    }
}
tasks.compileKotlin.configure {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    kotlinOptions.freeCompilerArgs += "-opt-in=org.mylibrary.OptInAnnotation"
//}
