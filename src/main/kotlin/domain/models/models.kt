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

@Serializable
data class DailyCheckin(
    val id: Int = 0,
    val idUser: Int,
    val sleepStart: String? = null,
    val sleepEnd: String? = null,
    val hoursSleep: Double? = null,
    val idMood: Int? = null,
    val idSemaphore: Int? = null,
    val dateTime: String,
    val sleepDebt: Double? = null,
    val batteryCog: Int? = null,
    val fatiga: Int? = null
)

@Serializable
data class GameSession(
    val id: Int = 0,
    val idDailyCheckin: Int,
    val idGame: Int,
    val startTime: String? = null,
    val endTime: String? = null,
    val scoreValue: Double? = null,
    val battery: Int? = null,
    val metadata: String? = null
)

@Serializable
data class Message(
    val id: Int = 0,
    val idDailyCheckin: Int? = null,
    val idGameSession: Int? = null,
    val message: String
)

// ─── NUEVO ────────────────────────────────────────────────────────────────────
@Serializable
data class StreakInfo(
    val id: Int = 0,
    val userId: Int,
    val startDate: String,        // formato "2025-02-01"
    val endDate: String? = null,  // null = racha activa
    val daysCount: Int = 1
)

@Serializable
enum class TrafficLight { GREEN, YELLOW, RED }
