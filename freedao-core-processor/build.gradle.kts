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
tasks.compileKotlin.configure {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
//    kotlinOptions.freeCompilerArgs += "-opt-in=org.mylibrary.OptInAnnotation"
//}
