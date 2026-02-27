plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.MindStack"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    implementation("io.ktor:ktor-server-cors:3.3.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.3.2")
    implementation("io.ktor:ktor-server-core:3.3.2")
    implementation("io.ktor:ktor-server-core:3.3.2")
    implementation("io.ktor:ktor-serialization-jackson:3.3.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.3.2")
    implementation("io.ktor:ktor-server-core:3.3.2")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.jetbrains.exposed:exposed-core:0.44.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.1")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.1")

    implementation("io.ktor:ktor-server-auth:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")
    implementation("com.auth0:java-jwt:4.4.0")


    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.mindrot:jbcrypt:0.4")
}
