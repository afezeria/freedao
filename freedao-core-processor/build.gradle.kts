@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(projects.freedaoCore)

    libs.apply {
        api(bundles.kotlin)

        api(kotlinx.metadata)
        api(javapoet)
        api(antlr)

        testImplementation(bundles.kotest)
    }

}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
tasks.compileKotlin.configure {
//    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
//    kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
}
