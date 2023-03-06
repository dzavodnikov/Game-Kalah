plugins {
    application
}

group = "pro.zavodnikov.kalah"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}
