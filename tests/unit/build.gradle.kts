plugins {
    kotlin("jvm")
    id("io.freefair.lombok") version "6.3.0"
    id("com.bnorm.power.kotlin-power-assert")
}

configure<com.bnorm.power.PowerAssertGradleExtension> {
    functions = listOf(
        "kotlin.assert",
        "kotlin.test.assertTrue",
        "kotlin.test.assertEquals",
        "kotlin.test.assertContentEquals",
        "kotlin.test.assertContains",
    )
}


dependencies {
    implementation(projects.freedaoCore)
    implementation(projects.freedaoRuntimeClassic)
    implementation(libs.springContext)

    testImplementation(projects.freedaoProcessorCore)
    testImplementation(projects.freedaoProcessorClassic)

    testImplementation(libs.logback)

    testImplementation(libs.kotlin.compile.test)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.junit4)

    testImplementation(libs.compileTesting)
    testImplementation(libs.hikaricp)

    testImplementation(libs.bundles.dbDrivers)
    testImplementation(libs.bundles.testcontainers)
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }

}

tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.add("-parameters")
}
