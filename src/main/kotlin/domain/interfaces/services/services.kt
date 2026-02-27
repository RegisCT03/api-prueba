package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.*

interface IAuthService {
    suspend fun register(req: RegisterRequest): AuthResponse
    suspend fun login(req: LoginRequest): AuthResponse
}

interface IDailyCheckinService {
    suspend fun submitCheckin(userId: Int, req: DailyCheckinRequest): DailyCheckinResponse
    suspend fun getHistory(userId: Int): List<DailyCheckinResponse>
    suspend fun getTodayCheckin(userId: Int): DailyCheckinResponse?
}

interface IGameService {
    suspend fun submitNeuroReflex(userId: Int, req: NeuroReflexRequest): NeuroReflexResponse
    suspend fun submitMemoryGame(userId: Int, req: MemoryGameRequest): MemoryGameResponse
    suspend fun getCombinedBattery(checkinId: Int): CombinedBatteryResponse
}