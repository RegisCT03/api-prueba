package com.MindStack.application.services

import com.MindStack.application.dtos.StreakResponse
import com.MindStack.domain.interfaces.repositories.IStreakRepository
import com.MindStack.domain.models.StreakInfo
import java.time.LocalDate

class StreakService(private val streakRepo: IStreakRepository) {

    /**
     * Llamar desde DailyCheckinService.endSleep() DESPUÉS de cerrar el check-in.
     *
     * Lógica:
     *  - Sin racha activa → crear nueva (daysCount=1)
     *  - lastDay == hoy   → ya se contó hoy, no incrementar
     *  - lastDay == ayer  → día consecutivo, incrementar
     *  - lastDay < ayer   → se saltó un día, cerrar racha vieja y crear nueva
     */
    suspend fun refresh(userId: Int): StreakResponse {
        val today  = LocalDate.now()
        val active = streakRepo.findActive(userId)

        if (active == null) {
            val new = streakRepo.createNew(userId)
            return buildResponse(new)
        }

        // Último día registrado en la racha activa
        val lastDay = LocalDate.parse(active.startDate)
            .plusDays((active.daysCount - 1).toLong())

        return when {
            lastDay == today -> {
                // Ya se registró hoy, no hacer nada
                buildResponse(active)
            }
            lastDay.plusDays(1) == today -> {
                // Día consecutivo: incrementar
                val updated = streakRepo.increment(active.id)
                buildResponse(updated)
            }
            else -> {
                // Se saltó al menos un día: reiniciar racha
                streakRepo.close(active.id)
                val new = streakRepo.createNew(userId)
                buildResponse(new)
            }
        }
    }

    suspend fun getStreakInfo(userId: Int): StreakResponse {
        val active  = streakRepo.findActive(userId)
        val current = active?.daysCount ?: 0
        val longest = streakRepo.longestStreak(userId)
        val total   = streakRepo.totalDays(userId)
        return StreakResponse(
            currentStreak   = current,
            longestStreak   = longest,
            totalDays       = total,
            goalDays        = 20,
            progressPercent = (current / 20.0 * 100.0).coerceAtMost(100.0),
            isGoalAchieved  = total >= 20
        )
    }

    private suspend fun buildResponse(streak: StreakInfo): StreakResponse {
        val longest = streakRepo.longestStreak(streak.userId)
        val total   = streakRepo.totalDays(streak.userId)
        return StreakResponse(
            currentStreak   = streak.daysCount,
            longestStreak   = longest,
            totalDays       = total,
            goalDays        = 20,
            progressPercent = (streak.daysCount / 20.0 * 100.0).coerceAtMost(100.0),
            isGoalAchieved  = total >= 20
        )
    }
}
