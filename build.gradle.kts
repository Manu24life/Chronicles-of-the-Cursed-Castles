plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "4.0.2"
}

group = "it.unicam.cs.mpgc.rpg125657"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"
val lombokVersion = "1.18.48"
val hibernateVersion = "7.4.12.Final"
val h2Version = "2.5.250"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainClass.set("it.unicam.cs.mpgc.rpg125657.cursedcastles.CursedCastlesApplication")
}

javafx {
    version = "25.0.3"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    testCompileOnly("org.projectlombok:lombok:${lombokVersion}")
    testAnnotationProcessor("org.projectlombok:lombok:${lombokVersion}")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(platform("org.hibernate.orm:hibernate-platform:${hibernateVersion}"))
    implementation("org.hibernate.orm:hibernate-core")
    implementation("jakarta.enterprise:jakarta.enterprise.cdi-api:4.1.0")
    implementation("jakarta.transaction:jakarta.transaction-api")
    runtimeOnly("com.h2database:h2:${h2Version}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    addExtraDependencies(
            "jakarta.enterprise.cdi-api",
            "jakarta.enterprise.lang-model",
            "jakarta.annotation-api",
            "jakarta.interceptor-api"
    )
    imageZip.set(layout.buildDirectory.file("distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "zip-6", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
