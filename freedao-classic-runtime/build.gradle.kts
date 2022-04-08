plugins {
    `java-library`
    id("io.freefair.lombok")
}

dependencies {
    api(projects.freedaoCore)
    api(libs.slf4j)
// https://mvnrepository.com/artifact/com.github.jsqlparser/jsqlparser
    api("com.github.jsqlparser:jsqlparser:4.3")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
