import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.6.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.liquibase.gradle") version "2.0.3"
    id("org.sonarqube") version "3.3"
    id("io.gitlab.arturbosch.detekt").version("1.19.0")
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.spring") version "1.6.10"
    kotlin("plugin.jpa") version "1.6.10"
    kotlin("plugin.allopen") version "1.4.32"
    jacoco
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

liquibase {
    activities.register("main") {
        this.arguments =
            mapOf(
                "logLevel" to "info",
                "changeLogFile" to "src/main/resources/db/changelog/liquibase.xml",
                "url" to "jdbc:postgresql://localhost:5432/postgres",
                "username" to "postgres",
                "password" to "postgres",
            )
    }
    runList = "main"
}

group = "com.ambrosia"

version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_17

configurations { compileOnly { extendsFrom(configurations.annotationProcessor.get()) } }

repositories {
    maven { url = uri("https://repo.spring.io/release") }
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql")
    implementation("org.keycloak:keycloak-spring-boot-starter:16.1.0")
    implementation("org.keycloak:keycloak-admin-client:16.1.0")
    implementation("org.zalando:problem-spring-web:0.27.0")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.github.springtestdbunit:spring-test-dbunit:1.3.0")
    implementation("org.dbunit:dbunit:2.7.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.liquibase:liquibase-core:4.4.3")
    liquibaseRuntime("org.yaml:snakeyaml:1.29")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
        exclude(module = "mockito-core")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2:2.1.210")
    testImplementation("com.ninja-squad:springmockk:3.1.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    runtimeOnly("com.h2database:h2")
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.sonarqube {
    dependsOn(tasks.detekt)
    dependsOn(tasks.jacocoTestReport)
}


jacoco {
    toolVersion = "0.8.7"
}

detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
}
tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
    jvmTarget = "17"
}

sonarqube {
    properties {
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.projectName", "Nymph")
        property("sonar.projectKey", "Nymph")
        property("sonar.login", "0cdc89972e6c60137d9a1729c86e2dda963aa4ea")
        property("sonar.language", "kotlin")
        property("sonar.kotlin.detekt.reportPaths", "build/reports/detekt/detekt.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.jacoco.reportPath", "build/jacoco/test.exec")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            isEnabled = true
        }

    }
}

tasks.withType<Test> { useJUnitPlatform() }

