package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.Request.LoginRequest
import com.MindStack.application.dtos.Request.RegisterRequest
import com.MindStack.application.dtos.Response.AuthResponse

interface IAuthService {
    suspend fun register(req: RegisterRequest): AuthResponse
    suspend fun login(req: LoginRequest): AuthResponse
}