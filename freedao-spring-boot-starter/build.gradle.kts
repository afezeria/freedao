import org.springframework.boot.gradle.tasks.bundling.BootJar

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    libs.plugins.apply {
        alias(spring.boot)
        alias(spring.dependencyManagement)
        alias(lombok)
    }

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
