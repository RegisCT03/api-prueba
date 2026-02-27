package com.MindStack.application.services

import com.MindStack.application.dtos.DailyCheckinRequest
import com.MindStack.application.dtos.DailyCheckinResponse
import com.MindStack.application.dtos.SemaphoreResponse
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import java.time.Duration
import java.time.LocalTime

class DailyCheckinService(
    private val checkinRepo: IDailyCheckinRepository,
    private val userRepo: IUserRepository,
    private val messageRepo: IMessageRepository
) : IDailyCheckinService {

    override suspend fun submitCheckin(userId: Int, req: DailyCheckinRequest): DailyCheckinResponse {
        // 1. Calcular horas dormidas (soporta cruzar medianoche)
        val start = LocalTime.parse(req.sleepStart)
        val end   = LocalTime.parse(req.sleepEnd)
        val hoursSleep = calculateHoursSleep(start, end)

        // 2. Deuda y porcentaje de sueño
        val idealHours   = userRepo.getIdealSleepHours(userId)
        val sleepPercent = (hoursSleep / idealHours) * 100.0
        val sleepDebt    = (idealHours - hoursSleep).coerceAtLeast(0.0)

        // 3. Evaluar semáforo
        val semaphore = SemaphoreEngine.evaluate(sleepPercent, req.moodScore)

        // 4. Batería inicial (antes de los juegos)
        val initialBattery = when (semaphore.color.name) {
            "GREEN"  -> 70
            "YELLOW" -> 45
            else     -> 20
        }

        // 5. Guardar check-in
        val checkin = checkinRepo.create(
            idUser     = userId,
            sleepStart = req.sleepStart,
            sleepEnd   = req.sleepEnd,
            hoursSleep = hoursSleep,
            idMood     = req.moodScore.coerceIn(1, 5),
            idStatus   = semaphore.statusId,
            sleepDebt  = sleepDebt,
            battery    = initialBattery
        )

        // 6. Guardar mensaje de sugerencia
        messageRepo.create(checkin.id, null, semaphore.recommendation)

        return DailyCheckinResponse(
            checkinId    = checkin.id,
            hoursSleep   = hoursSleep,
            sleepDebt    = sleepDebt,
            sleepPercent = sleepPercent,
            moodScore    = req.moodScore,
            semaphore    = SemaphoreResponse(semaphore.color.name, semaphore.label, semaphore.recommendation),
            batteryCog   = initialBattery,
            fatiga       = (100 - initialBattery).coerceAtLeast(0),
            message      = semaphore.recommendation
        )
    }

    override suspend fun getHistory(userId: Int): List<DailyCheckinResponse> {
        val idealHours = userRepo.getIdealSleepHours(userId)
        return checkinRepo.findByUser(userId).map { c ->
            val sleepPercent = ((c.hoursSleep ?: 0.0) / idealHours) * 100.0
            val mood         = c.idMood ?: 3
            val semaphore    = SemaphoreEngine.evaluate(sleepPercent, mood)
            val battery      = c.batteryCog ?: 0
            DailyCheckinResponse(
                checkinId    = c.id,
                hoursSleep   = c.hoursSleep ?: 0.0,
                sleepDebt    = c.sleepDebt ?: 0.0,
                sleepPercent = sleepPercent,
                moodScore    = mood,
                semaphore    = SemaphoreResponse(semaphore.color.name, semaphore.label, semaphore.recommendation),
                batteryCog   = battery,
                fatiga       = c.fatiga ?: (100 - battery).coerceAtLeast(0),
                message      = semaphore.recommendation
            )
        }
    }

    override suspend fun getTodayCheckin(userId: Int): DailyCheckinResponse? {
        val idealHours = userRepo.getIdealSleepHours(userId)
        val c = checkinRepo.findTodayByUser(userId) ?: return null
        val sleepPercent = ((c.hoursSleep ?: 0.0) / idealHours) * 100.0
        val mood = c.idMood ?: 3
        val semaphore = SemaphoreEngine.evaluate(sleepPercent, mood)
        val battery = c.batteryCog ?: 0
        return DailyCheckinResponse(
            checkinId    = c.id,
            hoursSleep   = c.hoursSleep ?: 0.0,
            sleepDebt    = c.sleepDebt ?: 0.0,
            sleepPercent = sleepPercent,
            moodScore    = mood,
            semaphore    = SemaphoreResponse(semaphore.color.name, semaphore.label, semaphore.recommendation),
            batteryCog   = battery,
            fatiga       = c.fatiga ?: (100 - battery).coerceAtLeast(0),
            message      = semaphore.recommendation
        )
    }

    private fun calculateHoursSleep(start: LocalTime, end: LocalTime): Double {
        val minutes = if (end.isAfter(start)) {
            Duration.between(start, end).toMinutes()
        } else {
            Duration.between(start, end).toMinutes() + (24 * 60)
        }
        return minutes / 60.0
    }
}