package com.MindStack.application.dtos

import kotlinx.serialization.Serializable

// ─── Auth ─────────────────────────────────────────────────────────────────────

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

@Serializable
data class AuthResponse(
    val token: String,
    val userId: Int,
    val name: String
)

// ─── Daily Check-in ───────────────────────────────────────────────────────────

@Serializable
data class DailyCheckinRequest(
    val sleepStart: String,     // "HH:mm"
    val sleepEnd: String,       // "HH:mm"
    val moodScore: Int          // 1-5
)

@Serializable
data class SemaphoreResponse(
    val color: String,
    val label: String,
    val recommendation: String
)

@Serializable
data class DailyCheckinResponse(
    val checkinId: Int,
    val hoursSleep: Double,
    val sleepDebt: Double,
    val sleepPercent: Double,
    val moodScore: Int,
    val semaphore: SemaphoreResponse,
    val batteryCog: Int,
    val fatiga: Int,
    val message: String
)

// ─── Juego A: Neuro-Reflejo ───────────────────────────────────────────────────

@Serializable
data class NeuroReflexRequest(
    val idDailyCheckin: Int,
    val reactionTime1Ms: Double,
    val reactionTime2Ms: Double,
    val reactionTime3Ms: Double
)

@Serializable
data class NeuroReflexResponse(
    val sessionId: Int,
    val averageMs: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)

// ─── Juego B: Memoria de Trabajo ──────────────────────────────────────────────

@Serializable
data class MemoryGameRequest(
    val idDailyCheckin: Int,
    val correctHits: Int,
    val totalRequired: Int      // siempre 5
)

@Serializable
data class MemoryGameResponse(
    val sessionId: Int,
    val accuracyPercent: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)

// ─── Batería combinada ────────────────────────────────────────────────────────

@Serializable
data class CombinedBatteryResponse(
    val finalBattery: Int,
    val fatiga: Int,
    val semaphoreColor: String,
    val cognitiveStatus: String,
    val globalRecommendation: String
)