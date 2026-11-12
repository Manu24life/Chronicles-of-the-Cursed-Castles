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
val fxglVersion = "25.0.1"

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
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media")
}

dependencies {
    // Texture e animazioni FXGL utilizzabili nell'app JavaFX esistente.
    implementation("com.github.almasb:fxgl-core:${fxglVersion}")
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
    mergedModule {
        additive = true
        requires("java.naming")
        // Hibernate carica questi SPI attraverso un wrapper generico di ServiceLoader:
        // il plugin non può ricavarne automaticamente le clausole uses dal bytecode.
        listOf(
            "org.hibernate.boot.archive.scan.spi.ScannerFactory",
            "org.hibernate.boot.model.FunctionContributor",
            "org.hibernate.boot.model.TypeContributor",
            "org.hibernate.boot.registry.selector.StrategyRegistrationProvider",
            "org.hibernate.boot.registry.selector.spi.DialectSelector",
            "org.hibernate.boot.registry.selector.spi.NamedStrategyContributor",
            "org.hibernate.boot.spi.AdditionalMappingContributor",
            "org.hibernate.boot.spi.MetadataBuilderContributor",
            "org.hibernate.boot.spi.MetadataBuilderFactory",
            "org.hibernate.boot.spi.MetadataBuilderInitializer",
            "org.hibernate.boot.spi.MetadataSourcesContributor",
            "org.hibernate.boot.spi.SessionFactoryBuilderFactory",
            "org.hibernate.bytecode.spi.BytecodeProvider",
            "org.hibernate.engine.extension.spi.ExtensionIntegration",
            "org.hibernate.engine.jdbc.batch.spi.BatchBuilder",
            "org.hibernate.engine.jdbc.dialect.spi.DialectResolver",
            "org.hibernate.engine.jdbc.mutation.spi.MutationExecutorService",
            "org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformProvider",
            "org.hibernate.event.monitor.spi.EventMonitor",
            "org.hibernate.event.spi.EventEngineContributor",
            "org.hibernate.integrator.spi.Integrator",
            "org.hibernate.models.spi.ModelsContextProvider",
            "org.hibernate.resource.beans.container.spi.BeanContainer",
            "org.hibernate.service.spi.ServiceContributor",
            "org.hibernate.service.spi.SessionFactoryServiceContributor",
            "org.hibernate.stat.spi.StatisticsFactory",
            "org.hibernate.tool.schema.spi.SchemaManagementTool"
        ).forEach { uses(it) }
    }
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
        // Il codice Hibernate vive nel modulo unificato nell'immagine distribuita.
        jvmArgs = listOf(
            "--add-reads=it.unicam.cs.mpgc.rpg125657.merged.module=it.unicam.cs.mpgc.rpg125657.cursedcastles",
            "--add-opens=it.unicam.cs.mpgc.rpg125657.cursedcastles/it.unicam.cs.mpgc.rpg125657.cursedcastles.persistence.hibernate=it.unicam.cs.mpgc.rpg125657.merged.module"
        )
    }
}
