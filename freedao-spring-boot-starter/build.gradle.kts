plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api(projects.freedaoClassicRuntime)
    api(libs.spring.boot.jdbc)
    annotationProcessor(libs.spring.boot.configuration.processor)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
