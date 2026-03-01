package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.domain.interfaces.services.IAuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

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