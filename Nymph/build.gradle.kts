import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.liquibase.gradle") version "2.0.3"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
	kotlin("plugin.allopen") version "1.4.32"
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

liquibase {
	activities.register("main") {
		this.arguments = mapOf(
			"logLevel" to "info",
			"changeLogFile" to "src/main/resources/db/changelog/liquibase-changeLog.xml",
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

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

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
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	liquibaseRuntime("org.postgresql:postgresql")
	liquibaseRuntime("org.liquibase:liquibase-core:4.4.3")
	liquibaseRuntime("org.yaml:snakeyaml:1.29")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(module = "junit")
		exclude(module = "mockito-core")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("com.h2database:h2:2.0.206")
	testImplementation("com.ninja-squad:springmockk:3.1.0")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
	runtimeOnly("com.h2database:h2")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
