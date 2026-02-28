package com.MindStack

import com.MindStack.infraestructure.database.DatabaseConfig
import com.MindStack.infraestructure.database.DatabaseFactory
import com.MindStack.presentation.di.DependenciesDeclaration
import com.MindStack.presentation.plugins.configureSecurity
import com.MindStack.presentation.plugins.configureSerialization
import com.MindStack.presentation.routing.configureRouting
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(DatabaseConfig())
    configureSerialization()
    configureSecurity()
    configureCors()                         // NUEVO
    val deps = DependenciesDeclaration()
    configureRouting(deps)
}

// NUEVO ────────────────────────────────────────────────────────────────────────
fun Application.configureCors() {
    install(CORS) {
        // En producción reemplazar anyHost() por:
        // allowHost("tudominio.com", schemes = listOf("https"))
        anyHost()
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
}
