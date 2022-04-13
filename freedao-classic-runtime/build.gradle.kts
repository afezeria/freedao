plugins {
    `java-library`
    id("io.freefair.lombok")
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
