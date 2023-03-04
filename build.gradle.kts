@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    libs.plugins.apply {
        alias(kotlin.jvm) apply false
        alias(serialTask)
    }
}

val ossrhUsername: String by project
val ossrhPassword: String by project

subprojects {

    apply {
        plugin("java")
        plugin("maven-publish")
        plugin("signing")
    }

    repositories {
        mavenCentral()
        google()
    }

    group = "io.github.afezeria"
    version = "0.2.2"
//    version = "${version}SNAPSHOT"

    if (project.name.startsWith("test")) {
        tasks.withType<Jar> {
            enabled = false
        }
    } else {
        tasks.withType<Jar> {
            enabled = true
            archiveClassifier.set("")
        }

        val sourcesJar by tasks.creating(Jar::class) {
            archiveClassifier.set("sources")
            from(project.the<SourceSetContainer>()["main"].allSource)
        }

        val javadocJar by tasks.creating(Jar::class) {
            archiveClassifier.set("javadoc")
        }

        artifacts {
            add("archives", sourcesJar)
            add("archives", javadocJar)
        }

        configure<PublishingExtension> {
            repositories {
                maven {
                    if (project.version.toString().endsWith("-SNAPSHOT")) {
                        setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    } else {
                        setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    }
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }

            publications {
                register<MavenPublication>("mavenJava") {
                    from(components["java"])
                    artifact(sourcesJar)
                    artifact(javadocJar)

                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()

                    pom {
                        name.set("${project.group}:${project.name}")
                        description.set("A dao library based on apt.")
                        url.set("https://github.com/afezeria/freedao")
                        licenses {
                            license {
                                name.set("The Apache Software License, Version 2.0")
                                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                            }
                        }
                        scm {
                            url.set("https://github.com/afezeria/freedao")
                            connection.set("scm:git:https://github.com/afezeria/freedao.git")
                            developerConnection.set("scm:git:ssh://git@github.com/afezeria/freedao.git")
                        }
                        developers {
                            developer {
                                id.set("afezeria")
                                name.set("afezeria")
                                email.set("zodal@outlook.com")
                            }
                        }
                    }
                }
            }
        }
        configure<SigningExtension>() {
            sign(the<PublishingExtension>().publications["mavenJava"])
        }
    }
}

serialTask {
    set("publishMavenJavaPublicationToMavenRepository")
}