package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.LoginRequest
import com.MindStack.application.dtos.RegisterRequest
import com.MindStack.domain.interfaces.services.IAuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: IAuthService) {
    route("/auth") {

        // POST /api/v1/auth/register
        post("/register") {
            val req = call.receive<RegisterRequest>()
            val res = authService.register(req)
            call.respond(HttpStatusCode.Created, res)
        }

        // POST /api/v1/auth/login
        post("/login") {
            val req = call.receive<LoginRequest>()
            val res = authService.login(req)
            call.respond(HttpStatusCode.OK, res)
        }
    }
}