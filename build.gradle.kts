plugins {
    id("java")
}

group = "de.chojo"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://eldonexus.de/repository/maven-public")
    maven("https://eldonexus.de/repository/maven-proxies")
}

dependencies {
    implementation("de.chojo", "cjda-util", "2.8.5+beta.5") {
        exclude(group = "club.minnced", module = "opus-java")
    }

    // Logging
    implementation("org.slf4j", "slf4j-api", "2.0.6")
    implementation("org.apache.logging.log4j", "log4j-core", "2.20.0")
    implementation("org.apache.logging.log4j", "log4j-slf4j2-impl", "2.20.0")
    implementation("de.chojo", "log-util", "1.0.1") {
        exclude("org.apache.logging.log4j")
    }
    implementation("club.minnced:discord-webhooks:0.8.4")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
