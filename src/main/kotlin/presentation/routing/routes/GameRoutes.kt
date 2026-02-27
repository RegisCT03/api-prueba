package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.MemoryGameRequest
import com.MindStack.application.dtos.NeuroReflexRequest
import com.MindStack.domain.interfaces.services.IGameService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.gameRoutes(gameService: IGameService) {
    authenticate("auth-jwt") {
        route("/games") {

            // POST /api/v1/games/neuro-reflex
            post("/neuro-reflex") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<NeuroReflexRequest>()
                call.respond(HttpStatusCode.Created, gameService.submitNeuroReflex(userId, req))
            }

            // POST /api/v1/games/memory
            post("/memory") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<MemoryGameRequest>()
                call.respond(HttpStatusCode.Created, gameService.submitMemoryGame(userId, req))
            }

            // GET /api/v1/games/battery/{checkinId}
            get("/battery/{checkinId}") {
                val checkinId = call.parameters["checkinId"]?.toIntOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "checkinId inválido"))
                call.respond(HttpStatusCode.OK, gameService.getCombinedBattery(checkinId))
            }
        }
    }
}