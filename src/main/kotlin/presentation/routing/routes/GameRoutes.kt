package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.MemoryGameRequest
import com.MindStack.application.dtos.NeuroReflexRequest
import com.MindStack.domain.interfaces.services.IGameService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.gameRoutes(gameService: IGameService) {
    authenticate("auth-jwt") {
        route("/games") {

            // POST /api/v1/games/neuro-reflex  (Taptap)
            post("/neuro-reflex") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<NeuroReflexRequest>()
                call.respond(
                    HttpStatusCode.Created,
                    gameService.submitNeuroReflex(userId, req)
                )
            }

            // POST /api/v1/games/memory  (Memorama)
            post("/memory") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<MemoryGameRequest>()
                call.respond(
                    HttpStatusCode.Created,
                    gameService.submitMemoryGame(userId, req)
                )
            }

            // GET /api/v1/games/battery/{checkinId}
            // CORRECCIÓN: se pasa userId para verificar ownership del check-in
            get("/battery/{checkinId}") {
                val userId    = call.principal<JWTPrincipal>()!!.userId()
                val checkinId = call.parameters["checkinId"]?.toIntOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "checkinId inválido")
                    )
                call.respond(
                    HttpStatusCode.OK,
                    gameService.getCombinedBattery(userId, checkinId)
                )
            }
        }
    }
}
