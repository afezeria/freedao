plugins {
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.freefair.lombok") version "6.4.0"
}

dependencies {
    api(projects.freedaoRuntimeClassic)

    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.zaxxer:HikariCP:5.0.1")


}

//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(8))
//    }
//}
