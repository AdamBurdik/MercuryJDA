plugins {
    id("java")
}

group = "me.adamix.mercury.jda"
version = "0.1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://www.jitpack.io") }
}

dependencies {
    implementation("org.jetbrains:annotations:26.0.2")
    compileOnly("net.dv8tion:JDA:5.6.1")

    implementation("com.github.AdamBurdik.MercuryConfiguration:api:31378f0218")
    implementation("com.github.AdamBurdik.MercuryConfiguration:toml:31378f0218")

    testImplementation("net.dv8tion:JDA:6.1.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}