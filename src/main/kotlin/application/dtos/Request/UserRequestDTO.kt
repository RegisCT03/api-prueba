package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val lastName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val idealSleepHours: Double = 8.0
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)