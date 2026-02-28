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

// ─── Sleep Flow (Zzz / Levantarse) ───────────────────────────────────────────

@Serializable
data class SleepStartRequest(
    val sleepStart: String   // ISO-8601 del dispositivo: "2025-02-27T23:30:00"
)

@Serializable
data class SleepStartResponse(
    val checkinId: Int,
    val sleepStart: String,
    val message: String
)

@Serializable
data class SleepEndRequest(
    val sleepEnd: String,    // ISO-8601 del dispositivo al despertar
    val moodScore: Int       // 1-5 — se pregunta al usuario al presionar Levantarse
)

// ─── Daily Check-in (legacy — mantener para compatibilidad) ──────────────────

@Serializable
data class DailyCheckinRequest(
    val sleepStart: String,
    val sleepEnd: String,
    val moodScore: Int
)

@Serializable
data class SemaphoreResponse(
    val color: String,
    val label: String,
    val recommendation: String
)

@Serializable
data class PersonalizedMessage(
    val prefix: String,        // "Óptimo" | "Precaución" | "Crítico"
    val body: String,          // texto principal de la matriz
    val full: String,          // "$prefix: $body" — listo para mostrar en el front
    val batteryRange: String   // "71% - 100%" | "31% - 70%" | "0% - 30%"
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
    val message: String,                         // genérico (mantener compatibilidad)
    val personalizedMessage: PersonalizedMessage  // NUEVO — cruzado con rol del usuario
)

// ─── Juego A: Taptap (Neuro-Reflejo) ─────────────────────────────────────────

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

// ─── Juego B: Memorama ────────────────────────────────────────────────────────

@Serializable
data class MemoryGameRequest(
    val idDailyCheckin: Int,
    val correctHits: Int,
    val totalRequired: Int
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
    val globalRecommendation: String,
    val personalizedMessage: PersonalizedMessage  // NUEVO — cruzado con rol del usuario
)

// ─── Racha ────────────────────────────────────────────────────────────────────

@Serializable
data class StreakResponse(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDays: Int,
    val goalDays: Int = 20,
    val progressPercent: Double,
    val isGoalAchieved: Boolean
)

// ─── Dashboard ────────────────────────────────────────────────────────────────

@Serializable
data class DashboardResponse(
    val todayCheckin: DailyCheckinResponse?,
    val streak: StreakResponse,
    val weekSleepAvgHours: Double,
    val weekBatteryAvg: Double,
    val hasPendingSleepStart: Boolean,  // true si hay un Zzz sin Levantarse
    val pendingCheckinId: Int?          // id del check-in abierto
)
