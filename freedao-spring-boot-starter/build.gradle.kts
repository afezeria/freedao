import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.freefair.lombok")

}

dependencies {
    api(projects.freedaoClassicRuntime)
    api(libs.spring.boot.jdbc)
    api(libs.spring.boot.aop)
    annotationProcessor(libs.spring.boot.configuration.processor)
}

tasks.withType<BootJar> {
    enabled = false
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
