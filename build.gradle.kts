plugins {
    id("org.springframework.boot") version "2.6.2" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("plugin.spring") version "1.6.10" apply false
    kotlin("jvm") version "1.6.20" apply false
    kotlin("kapt") version "1.6.20" apply false
    id("com.bnorm.power.kotlin-power-assert") version "0.11.0" apply false
}

subprojects {

    repositories {
        mavenCentral()
        google()
    }

    group = "com.github.afezeria.freedao"
    version = "0.0.1"

}
