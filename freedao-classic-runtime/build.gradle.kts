@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.lombok)
}

dependencies {
    api(projects.freedaoCore)
    api(libs.slf4j)
    api(libs.jsqlparser)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
