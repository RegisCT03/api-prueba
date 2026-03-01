package com.MindStack.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0,
    val name: String,
    val lastName: String,
    val email: String,
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val idRol: Int? = null,
    val idealSleepHours: Double = 8.0
)