package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.DailyCheckin
import com.MindStack.domain.models.GameSession
import com.MindStack.domain.models.Message
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
    suspend fun create(
        idUser: Int, sleepStart: String, sleepEnd: String,
        hoursSleep: Double, idMood: Int, idStatus: Int,
        sleepDebt: Double, battery: Int
    ): DailyCheckin
    suspend fun findById(id: Int): DailyCheckin?
    suspend fun findByUser(userId: Int): List<DailyCheckin>
    suspend fun findTodayByUser(userId: Int): DailyCheckin?
    suspend fun updateBattery(checkinId: Int, battery: Int)
}

interface IGameSessionRepository {
    suspend fun create(
        idDailyCheckin: Int, idJuego: Int,
        scoreValue: Double, battery: Int, metadata: String
    ): GameSession
    suspend fun findByCheckin(checkinId: Int): List<GameSession>
}

interface IMessageRepository {
    suspend fun create(idDailyCheckin: Int?, idGameSession: Int?, message: String): Message
    suspend fun findByCheckin(checkinId: Int): List<Message>
}