plugins {
    id("java")
}

group = "me.adamix.mercury.jda"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:26.0.2")
    compileOnly("net.dv8tion:JDA:5.6.1")

    testImplementation("net.dv8tion:JDA:5.6.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}