plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "2.0.0"
}

group = "com.MindStack"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

kotlin {
    jvmToolchain(21)
}

val ktor_version = "2.3.7"
val exposed_version = "0.44.1"

dependencies {
    // ─── Ktor Server (todo en 2.3.7) ──────────────────────────────────────────
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")

    // ─── Serialización JSON ───────────────────────────────────────────────────
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // ─── Auth / JWT ───────────────────────────────────────────────────────────
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("com.auth0:java-jwt:4.4.0")

    // ─── Base de datos ────────────────────────────────────────────────────────
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed_version")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // ─── Seguridad ────────────────────────────────────────────────────────────
    implementation("org.mindrot:jbcrypt:0.4")

    // ─── Logging ──────────────────────────────────────────────────────────────
    implementation(libs.logback.classic)

    // ─── Test ─────────────────────────────────────────────────────────────────
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}