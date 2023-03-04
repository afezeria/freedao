rootProject.name = "freedao"

include("freedao-core")
include("freedao-core-processor")
include("freedao-classic-processor")
include("freedao-classic-runtime")
include("freedao-spring-processor")
include("freedao-spring-boot-starter")
include("test")
include("test-spring-boot")


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val kotlin = version("kotlin", "1.8.10")
            val springBoot = version("springBoot", "2.6.2")
            val kotest = version("kotest", "5.5.5")

            //==============================================
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-kapt", "org.jetbrains.kotlin.kapt").versionRef("kotlin")
            plugin("kotlin-lombok", "org.jetbrains.kotlin.plugin.lombok").versionRef("kotlin")
            plugin("kotlin-allopen", "org.jetbrains.kotlin.plugin.allopen").versionRef("kotlin")
            plugin("kotlin-spring", "org.jetbrains.kotlin.plugin.spring").versionRef("kotlin")

            plugin("spring-boot", "org.springframework.boot").versionRef("springBoot")
            plugin("spring-dependencyManagement", "io.spring.dependency-management").version("1.0.11.RELEASE")

            plugin("lombok", "io.freefair.lombok").version("6.4.2")
            plugin("serialTask", "io.github.afezeria.serial-task").version("1.0")
            plugin("kotlinPowerAssert", "com.bnorm.power.kotlin-power-assert").version("0.12.0")


            //==============================================
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef(kotlin)
            library("kotlin-jdk8", "org.jetbrains.kotlin", "kotlin-stdlib-jdk8").versionRef(kotlin)
            library("kotlin-test", "org.jetbrains.kotlin", "kotlin-test-junit").versionRef(kotlin)

            library("kotlinx-metadata", "org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.5.0")

            library("annotations", "org.jetbrains:annotations:13.0")

            library("autoservice", "com.google.auto.service:auto-service:1.0")
            library("javapoet", "com.squareup:javapoet:1.13.0")
            library("antlr", "org.antlr:antlr4-runtime:4.9.3")

            library("spring-boot-jdbc", "org.springframework.boot", "spring-boot-starter-jdbc").versionRef(springBoot)
            library("spring-boot-aop", "org.springframework.boot", "spring-boot-starter-aop").versionRef(springBoot)
            library("spring-boot-web", "org.springframework.boot", "spring-boot-starter-web").versionRef(springBoot)
            library("spring-boot-test", "org.springframework.boot", "spring-boot-starter-test").versionRef(springBoot)
            library(
                "spring-boot-configuration-processor",
                "org.springframework.boot", "spring-boot-configuration-processor",
            ).versionRef(springBoot)
            library("spring-context", "org.springframework:spring-context:5.3.14")


            library("slf4j", "org.slf4j:slf4j-api:1.7.32")
            library("logback", "ch.qos.logback:logback-classic:1.2.10")

            library("jsqlparser", "com.github.jsqlparser:jsqlparser:4.3")
            library("hikaricp", "com.zaxxer:HikariCP:5.0.0")
            library("postgresql", "org.postgresql:postgresql:42.3.1")
            library("mysql", "mysql:mysql-connector-java:8.0.27")
            library("h2", "com.h2database:h2:2.1.210")

            library("junit4", "junit:junit:4.13")

            library("kotlin-compile-test", "com.github.tschuchortdev:kotlin-compile-testing-ksp:1.4.9")
            library("compileTesting", "com.google.testing.compile:compile-testing:0.19")

            library("kotest-assertions", "io.kotest", "kotest-assertions-core").versionRef(kotest)
            library("kotest-property", "io.kotest", "kotest-property").versionRef(kotest)
            library("kotest-runner", "io.kotest", "kotest-runner-junit4-jvm").versionRef(kotest)

            library("mockk", "io.mockk:mockk:1.12.3")


            library("testcontainers-bom", "org.testcontainers:testcontainers-bom:1.15.1")
            library("testcontainers-mysql", "org.testcontainers:mysql:1.16.3")
            library("testcontainers-postgresql", "org.testcontainers:postgresql:1.16.2")


            //============================================
            bundle("kotest", listOf("kotest-runner", "kotest-assertions", "kotest-property"))
            bundle("kotlin", listOf("kotlin-reflect", "kotlin-jdk8"))
            bundle("testcontainers", listOf("testcontainers-bom", "testcontainers-mysql", "testcontainers-postgresql"))
            bundle("dbDrivers", listOf("postgresql", "mysql", "h2"))


        }
    }
}
