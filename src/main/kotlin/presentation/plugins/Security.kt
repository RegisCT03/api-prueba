package com.MindStack.presentation.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity(
    jwtSecret: String   = System.getenv("JWT_SECRET")   ?: "mindstack_secret_dev",
    jwtIssuer: String   = System.getenv("JWT_ISSUER")   ?: "mindstack",
    jwtAudience: String = System.getenv("JWT_AUDIENCE") ?: "mindstack_users"
) {
    install(Authentication) {
        jwt("auth-jwt") {
            realm = "MindStack API"
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null)
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }
}

// Extension para extraer userId del token JWT
fun JWTPrincipal.userId(): Int =
    payload.getClaim("userId").asInt()
        ?: throw IllegalStateException("Token inválido: userId no encontrado")