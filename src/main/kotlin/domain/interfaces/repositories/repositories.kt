package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.DailyCheckin
import com.MindStack.domain.models.GameSession
import com.MindStack.domain.models.Message
import com.MindStack.domain.models.StreakInfo
import com.MindStack.domain.models.User

interface IUserRepository {
    suspend fun findByEmail(email: String): Pair<User, String>?
    suspend fun create(
        name: String, lastName: String, email: String, hashedPassword: String,
        dateOfBirth: String?, gender: String?, idealSleepHours: Double
    ): User
    suspend fun findById(id: Int): User?
    suspend fun getIdealSleepHours(userId: Int): Double
}

interface IDailyCheckinRepository {
    // ─── Existentes ───────────────────────────────────────────────────────────
    suspend fun create(
        idUser: Int, sleepStart: String, sleepEnd: String,
        hoursSleep: Double, idMood: Int, idStatus: Int,
        sleepDebt: Double, battery: Int
    ): DailyCheckin
    suspend fun findById(id: Int): DailyCheckin?
    suspend fun findByUser(userId: Int): List<DailyCheckin>
    suspend fun findTodayByUser(userId: Int): DailyCheckin?
    suspend fun updateBattery(checkinId: Int, battery: Int)

    // ─── NUEVOS: flujo Zzz / Levantarse ──────────────────────────────────────
    /** INSERT con solo sleepStart — todos los demás campos quedan null */
    suspend fun createOpen(idUser: Int, sleepStart: String): DailyCheckin

    /** Check-in de hoy donde sleepEnd IS NULL (Zzz presionado, sin Levantarse) */
    suspend fun findOpenTodayByUser(userId: Int): DailyCheckin?

    /** UPDATE: cierra el check-in con todos los campos calculados */
    suspend fun closeCheckin(
        checkinId: Int,
        sleepEnd: String,
        hoursSleep: Double,
        idMood: Int,
        idStatus: Int,
        sleepDebt: Double,
        battery: Int
    ): DailyCheckin
}

interface IGameSessionRepository {
    suspend fun create(
        idDailyCheckin: Int, idGame: Int,
        scoreValue: Double, battery: Int, metadata: String
    ): GameSession
    suspend fun findByCheckin(checkinId: Int): List<GameSession>
}

interface IMessageRepository {
    suspend fun create(idDailyCheckin: Int?, idGameSession: Int?, message: String): Message
    suspend fun findByCheckin(checkinId: Int): List<Message>
}

// ─── NUEVO ────────────────────────────────────────────────────────────────────
interface IStreakRepository {
    /** Racha activa (endDate IS NULL). Null si no existe. */
    suspend fun findActive(userId: Int): StreakInfo?

    /** Crea racha nueva con daysCount = 1 y startDate = hoy */
    suspend fun createNew(userId: Int): StreakInfo

    /** Suma 1 al daysCount de la racha activa */
    suspend fun increment(streakId: Int): StreakInfo

    /** Cierra la racha (endDate = hoy) */
    suspend fun close(streakId: Int)

    /** Racha más larga histórica del usuario */
    suspend fun longestStreak(userId: Int): Int

    /** Total de días con check-in en toda la historia */
    suspend fun totalDays(userId: Int): Int
}
