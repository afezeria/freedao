plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    kotlin("jvm")
    id("io.freefair.lombok") version "6.4.0"
}

dependencies {
    testImplementation(projects.freedaoSpringBootStarter)
    testAnnotationProcessor(projects.freedaoProcessorSpring)

    testImplementation(libs.h2)
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
