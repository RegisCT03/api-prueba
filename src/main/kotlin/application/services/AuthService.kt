package com.MindStack.application.services


import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.application.dtos.Response.AuthResponse
import com.MindStack.domain.interfaces.services.IAuthService
import com.MindStack.infraestructure.repositories.IUserRepository
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

class AuthService(
    private val userRepo: IUserRepository,
    private val jwtSecret: String   = System.getenv("JWT_SECRET")   ?: "mindstack_secret_dev",
    private val jwtIssuer: String   = System.getenv("JWT_ISSUER")   ?: "mindstack",
    private val jwtAudience: String = System.getenv("JWT_AUDIENCE") ?: "mindstack_users",
    private val jwtExpiration: Long = 86_400_000L
) : IAuthService {

    override suspend fun register(req: RegisterRequest): AuthResponse {
        userRepo.findByEmail(req.email)?.let {
            throw IllegalArgumentException("El email ya está registrado.")
        }
        val hashed = BCrypt.hashpw(req.password, BCrypt.gensalt())
        val user = userRepo.create(
            name            = req.name,
            lastName        = req.lastName,
            email           = req.email,
            hashedPassword  = hashed,
            dateOfBirth     = req.dateOfBirth,
            gender          = req.gender,
            idealSleepHours = req.idealSleepHours
        )
        return AuthResponse(
            token  = generateToken(user.id, user.email),
            userId = user.id,
            name   = user.name
        )
    }

    override suspend fun login(req: LoginRequest): AuthResponse {
        val (user, hashed) = userRepo.findByEmail(req.email)
            ?: throw IllegalArgumentException("Credenciales inválidas.")
        if (!BCrypt.checkpw(req.password, hashed)) {
            throw IllegalArgumentException("Credenciales inválidas.")
        }
        return AuthResponse(
            token  = generateToken(user.id, user.email),
            userId = user.id,
            name   = user.name
        )
    }

    private fun generateToken(userId: Int, email: String): String =
        JWT.create()
            .withIssuer(jwtIssuer)
            .withAudience(jwtAudience)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpiration))
            .sign(Algorithm.HMAC256(jwtSecret))
}