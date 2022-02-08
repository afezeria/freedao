plugins {
    `java-library`
}

dependencies {
    api(projects.freedaoCore)
    api(libs.slf4j)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
