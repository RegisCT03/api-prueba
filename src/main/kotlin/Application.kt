package com.MindStack

import com.MindStack.infraestructure.database.DatabaseConfig
import com.MindStack.infraestructure.database.DatabaseFactory
import com.MindStack.presentation.di.DependenciesDeclaration
import com.MindStack.presentation.plugins.configureSecurity
import com.MindStack.presentation.plugins.configureSerialization
import com.MindStack.presentation.routing.configureRouting
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    DatabaseFactory.init(DatabaseConfig())
    configureSerialization()
    configureSecurity()
    val deps = DependenciesDeclaration()
    configureRouting(deps)
}