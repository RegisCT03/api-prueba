package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.*
import com.MindStack.domain.models.DailyCheckin

interface IAuthService {
    suspend fun register(req: RegisterRequest): AuthResponse
    suspend fun login(req: LoginRequest): AuthResponse
}

interface IDailyCheckinService {
    // ─── Legacy (mantener para compatibilidad) ────────────────────────────────
    suspend fun submitCheckin(userId: Int, req: DailyCheckinRequest): DailyCheckinResponse

    // ─── NUEVOS: flujo Zzz / Levantarse ──────────────────────────────────────
    /** Botón Zzz: INSERT con solo sleep_start, retorna checkinId */
    suspend fun startSleep(userId: Int, req: SleepStartRequest): SleepStartResponse

    /** Botón Levantarse: UPDATE sleep_end + calcular horas + semáforo */
    suspend fun endSleep(userId: Int, checkinId: Int, req: SleepEndRequest): DailyCheckinResponse

    // ─── Consultas ────────────────────────────────────────────────────────────
    suspend fun getHistory(userId: Int): List<DailyCheckinResponse>
    suspend fun getTodayCheckin(userId: Int): DailyCheckinResponse?

    /** Retorna el check-in de hoy que aún no tiene sleep_end (para Dashboard) */
    suspend fun findOpenToday(userId: Int): DailyCheckin?
}

interface IGameService {
    suspend fun submitNeuroReflex(userId: Int, req: NeuroReflexRequest): NeuroReflexResponse
    suspend fun submitMemoryGame(userId: Int, req: MemoryGameRequest): MemoryGameResponse
    suspend fun getCombinedBattery(userId: Int, checkinId: Int): CombinedBatteryResponse
}
