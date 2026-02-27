package com.MindStack.presentation.routing

import com.MindStack.presentation.di.DependenciesDeclaration
import com.MindStack.presentation.routing.routes.authRoutes
import com.MindStack.presentation.routing.routes.checkinRoutes
import com.MindStack.presentation.routing.routes.gameRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(deps: DependenciesDeclaration) {
    routing {
        get("/") {
            call.respondText("MindStack API — OK")
        }
        route("/api/v1") {
            authRoutes(deps.authService)
            checkinRoutes(deps.checkinService)
            gameRoutes(deps.gameService)
        }
    }
}