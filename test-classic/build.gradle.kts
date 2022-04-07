plugins {
    kotlin("jvm")
    kotlin("plugin.lombok")
    id("io.freefair.lombok")
    id("com.bnorm.power.kotlin-power-assert")
    jacoco
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
    implementation(projects.freedaoClassicRuntime)

    testImplementation(projects.freedaoCoreProcessor)
    testImplementation(projects.freedaoClassicProcessor)

    testImplementation(libs.logback)

    testImplementation(libs.kotlin.compile.test)
    testImplementation(libs.kotlin.test)
//    testImplementation(kotlin("test"))
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

tasks.test {
    useJUnit()
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report

    sourceSets(
        project(":freedao-core-processor").sourceSets.main.get(),
        project(":freedao-classic-processor").sourceSets.main.get()
    )
}