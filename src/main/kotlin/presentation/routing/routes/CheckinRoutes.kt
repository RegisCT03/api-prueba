package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.Request.DailyCheckinRequest
import com.MindStack.domain.interfaces.services.IDailyCheckinService
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

fun Route.checkinRoutes(checkinService: IDailyCheckinService) {
    authenticate("auth-jwt") {
        route("/checkin") {

            // POST /api/v1/checkin
            post {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val req    = call.receive<DailyCheckinRequest>()
                val res    = checkinService.submitCheckin(userId, req)
                call.respond(HttpStatusCode.Created, res)
            }

            // GET /api/v1/checkin/today
            get("/today") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                val res    = checkinService.getTodayCheckin(userId)
                if (res != null) call.respond(HttpStatusCode.OK, res)
                else call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("message" to "Sin check-in completado para hoy.")
                )
            }

            // GET /api/v1/checkin/history
            get("/history") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(HttpStatusCode.OK, checkinService.getHistory(userId))
            }
        }
    }
}
