package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.DailyCheckinRequest
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.checkinRoutes(checkinService: IDailyCheckinService) {
    authenticate("auth-jwt") {
        route("/checkin") {

            // POST /api/v1/checkin  → registra sueño + ánimo, devuelve semáforo
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
                else call.respond(HttpStatusCode.NotFound, mapOf("message" to "Sin check-in para hoy."))
            }

            // GET /api/v1/checkin/history
            get("/history") {
                val userId = call.principal<JWTPrincipal>()!!.userId()
                call.respond(HttpStatusCode.OK, checkinService.getHistory(userId))
            }
        }
    }
}