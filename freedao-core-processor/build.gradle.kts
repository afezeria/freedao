@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    libs.plugins.apply {
        alias(kotlin.jvm)
        alias(kotlinPowerAssert)
    }
}
configure<com.bnorm.power.PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertTrue",
        "kotlin.test.assertEquals",
        "kotlin.test.assertContentEquals",
        "kotlin.test.assertContains",
        "io.kotest.matchers.shouldBe"
    )
}

dependencies {
    api(projects.freedaoCore)

    libs.apply {
        api(bundles.kotlin)

        api(kotlinx.metadata)
        api(javapoet)
        api(antlr)

        testImplementation(bundles.kotest)
        testImplementation(kotlin.test)
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
