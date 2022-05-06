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
    implementation("io.mockk:mockk:1.12.3")

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
    jvmArgs(
        "--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.jvm=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
    )
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report

    sourceSets(
        project(":" + projects.freedaoCoreProcessor.name).sourceSets.main.get(),
        project(":" + projects.freedaoClassicProcessor.name).sourceSets.main.get(),
        project(":" + projects.freedaoClassicRuntime.name).sourceSets.main.get(),
    )
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("test/**")
            }
        })
    )
}