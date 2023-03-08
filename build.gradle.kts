plugins {
    application
    // https://plugins.gradle.org/plugin/org.springframework.boot
    id("org.springframework.boot") version "3.0.4"
}

group = "pro.zavodnikov.kalah"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.4")
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2")

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.0.4")

    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
