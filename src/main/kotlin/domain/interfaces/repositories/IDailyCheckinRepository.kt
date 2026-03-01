package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.DailyCheckin

interface IDailyCheckinRepository {
    suspend fun create(
        idUser: Int, sleepStart: String, sleepEnd: String,
        hoursSleep: Double, idMood: Int, idSemaphore: Int,
        sleepDebt: Double, battery: Int
    ): DailyCheckin
    suspend fun findById(id: Int): DailyCheckin?
    suspend fun findByUser(userId: Int): List<DailyCheckin>
    suspend fun findTodayByUser(userId: Int): DailyCheckin?
    suspend fun updateBattery(checkinId: Int, battery: Int)

    suspend fun createOpen(idUser: Int, sleepStart: String): DailyCheckin

    suspend fun findOpenTodayByUser(userId: Int): DailyCheckin?

    suspend fun closeCheckin(
        checkinId: Int,
        sleepEnd: String,
        hoursSleep: Double,
        idMood: Int,
        idSemaphore: Int,
        sleepDebt: Double,
        battery: Int
    ): DailyCheckin
}