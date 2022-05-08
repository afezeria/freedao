plugins {
    `java-library`
}

dependencies {

    // https://mvnrepository.com/artifact/org.jetbrains/annotations
//    implementation(libs)
    implementation(libs.annotations)

}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}