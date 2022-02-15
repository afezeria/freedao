plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.spring")
    kotlin("jvm")
    id("io.freefair.lombok") version "6.4.0"
}

dependencies {
    implementation(projects.freedaoSpringBootStarter)

    implementation(libs.kotlin.reflect)
    implementation(libs.kotlin.jdk8)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    annotationProcessor(projects.freedaoProcessorSpring)

    implementation(libs.postgresql)
    implementation(libs.h2)
    testImplementation("org.springframework.boot:spring-boot-starter-test")

}
//kotlin {
//    jvmToolchain {
//        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(8))
//    }
//}

//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(17))
//    }
//}