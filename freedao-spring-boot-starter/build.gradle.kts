plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    api(projects.freedaoRuntimeClassic)

    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
