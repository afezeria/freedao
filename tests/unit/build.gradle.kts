plugins {
    kotlin("jvm")
    kotlin("plugin.lombok") version "1.6.20"

    id("io.freefair.lombok") version "6.4.2"
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
    implementation(projects.freedaoRuntimeClassic)
//    implementation(libs.springContext)

    testImplementation(projects.freedaoProcessorCore)
    testImplementation(projects.freedaoProcessorClassic)

    testImplementation(libs.logback)

    testCompileOnly("org.projectlombok:lombok:1.18.22")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.22")

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

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report

    sourceSets(
        project(":freedao-processor-core").sourceSets.main.get(),
        project(":freedao-processor-classic").sourceSets.main.get()
    )
}