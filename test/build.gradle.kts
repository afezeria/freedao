@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    libs.plugins.apply {
        alias(kotlin.jvm)
        alias(kotlin.lombok)
        alias(lombok)
        alias(kotlinPowerAssert)
    }
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

    libs.apply {
        testImplementation(logback)

        testImplementation(kotlin.compile.test)
        testImplementation(kotlin.test)
//    testImplementation(kotlin("test"))
        testImplementation(junit4)

        testImplementation(compileTesting)
        testImplementation(hikaricp)

        testImplementation(bundles.dbDrivers)
        testImplementation(bundles.testcontainers)
    }

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