@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    libs.plugins.apply {
        alias(kotlin.jvm)
        alias(kotlin.spring)
        alias(spring.boot)
        alias(spring.dependencyManagement)
        alias(lombok)
    }
}

dependencies {
    testImplementation(projects.freedaoSpringBootStarter)
    testAnnotationProcessor(projects.freedaoSpringProcessor)

    testImplementation(libs.h2)
    testImplementation(libs.postgresql)
    testImplementation(libs.spring.boot.test)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.add("-parameters")
    options.compilerArgs.add("-Afreedao.debug=true")
}
tasks.withType<Test> {
    useJUnitPlatform()
}
