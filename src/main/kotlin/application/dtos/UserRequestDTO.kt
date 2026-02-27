package com.MindStack.application.dtos

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val dateOfBirth: String,
    val gender: String,
)

@Serializable
data class UserLoginRequestDto(
    val email: String,
    val password: String
)