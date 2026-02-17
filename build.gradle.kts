import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.2.21"
    id("com.gradleup.shadow") version "9.3.1"
}

group = "com.degoos.hytale"
version = "0.1.1-SNAPSHOT"

val hytaleInstallationPath = properties["hytale.path"].toString()
val hytaleServerExecutablePath = "${hytaleInstallationPath}/Server/HytaleServer.jar"
val serverOutputPath = "${properties["hytale.server"]}/mods"


repositories {
    mavenCentral()
    maven("https://dev.degoos.xyz/maven/repo")
}

dependencies {
    compileOnly(files(hytaleServerExecutablePath))

    compileOnly("com.degoos:kayle:0.0.8")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(23)
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("")

    dependencies {
        exclude(dependency("org.jetbrains.kotlin:.*"))
    }

    minimize()
}


tasks.register<Copy>("deployPlugin") {
    group = "deployment"
    description = "Builds the ShadowJar and copies it to the external server plugins folder."

    dependsOn("shadowJar")

    val shadowTask = tasks.named<ShadowJar>("shadowJar")

    from(shadowTask.flatMap { it.archiveFile })

    into(serverOutputPath)

    doLast {
        println(">> Successfully deployed plugin to: $serverOutputPath")
    }
}