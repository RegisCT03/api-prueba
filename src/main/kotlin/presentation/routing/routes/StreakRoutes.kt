package com.MindStack.presentation.routing.routes

import com.MindStack.application.services.StreakService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.streakRoutes(streakService: StreakService) {
    authenticate("auth-jwt") {
        route("/streak") {

            // GET /api/v1/streak
            // Retorna: racha actual, racha más larga, total de días, progreso hacia meta 20 días
            get {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(HttpStatusCode.OK, streakService.getStreakInfo(userId))
            }
        }
    }
}
