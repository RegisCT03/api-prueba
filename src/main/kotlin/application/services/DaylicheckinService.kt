package com.MindStack.application.services

import com.MindStack.application.dtos.DailyCheckinRequest
import com.MindStack.application.dtos.DailyCheckinResponse
import com.MindStack.application.dtos.PersonalizedMessage
import com.MindStack.application.dtos.SemaphoreResponse
import com.MindStack.application.dtos.SleepEndRequest
import com.MindStack.application.dtos.SleepStartRequest
import com.MindStack.application.dtos.SleepStartResponse
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.domain.models.DailyCheckin
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class DailyCheckinService(
    private val checkinRepo: IDailyCheckinRepository,
    private val userRepo: IUserRepository,
    private val messageRepo: IMessageRepository
) : IDailyCheckinService {

    // ─── Flujo nuevo: Botón Zzz ──────────────────────────────────────────────

    override suspend fun startSleep(userId: Int, req: SleepStartRequest): SleepStartResponse {
        checkinRepo.findOpenTodayByUser(userId)?.let { existing ->
            return SleepStartResponse(
                checkinId  = existing.id,
                sleepStart = existing.sleepStart ?: req.sleepStart,
                message    = "Ya tienes un registro de sueño iniciado hoy."
            )
        }
        val checkin = checkinRepo.createOpen(idUser = userId, sleepStart = req.sleepStart)
        return SleepStartResponse(
            checkinId  = checkin.id,
            sleepStart = req.sleepStart,
            message    = "Buenas noches. Registro de sueño iniciado."
        )
    }

    // ─── Flujo nuevo: Botón Levantarse ───────────────────────────────────────

    override suspend fun endSleep(
        userId: Int, checkinId: Int, req: SleepEndRequest
    ): DailyCheckinResponse {
        val checkin = checkinRepo.findById(checkinId)
            ?: throw IllegalArgumentException("Check-in no encontrado.")
        if (checkin.idUser != userId)
            throw IllegalArgumentException("No autorizado.")
        if (checkin.sleepEnd != null)
            throw IllegalArgumentException("Este registro de sueño ya fue cerrado.")

        val sleepStart = checkin.sleepStart
            ?: throw IllegalArgumentException("El check-in no tiene sleep_start registrado.")

        val hoursSleep   = calculateHoursIso(sleepStart, req.sleepEnd)
        val idealHours   = userRepo.getIdealSleepHours(userId)
        val sleepPercent = (hoursSleep / idealHours) * 100.0
        val sleepDebt    = (idealHours - hoursSleep).coerceAtLeast(0.0)
        val semaphore    = SemaphoreEngine.evaluate(sleepPercent, req.moodScore)
        val battery      = when (semaphore.color.name) {
            "GREEN"  -> 70
            "YELLOW" -> 45
            else     -> 20
        }

        checkinRepo.closeCheckin(
            checkinId  = checkinId,
            sleepEnd   = req.sleepEnd,
            hoursSleep = hoursSleep,
            idMood     = req.moodScore.coerceIn(1, 5),
            idSSemaphore   = semaphore.semaphoreId,
            sleepDebt  = sleepDebt,
            battery    = battery
        )

        // Mensaje personalizado cruzando rol × semáforo
        val user      = userRepo.findById(userId)
        val msgResult = MessageEngine.getMessage(
            idRol          = user?.idRol,
            semaphoreColor = semaphore.color.name,
            batteryLevel   = battery
        )
        messageRepo.create(checkinId, null, msgResult.full)

        return buildResponse(
            checkinId    = checkinId,
            hoursSleep   = hoursSleep,
            sleepDebt    = sleepDebt,
            sleepPercent = sleepPercent,
            moodScore    = req.moodScore,
            semaphore    = semaphore,
            battery      = battery,
            msgResult    = msgResult
        )
    }

    // ─── Flujo legacy (compatibilidad) ───────────────────────────────────────

    override suspend fun submitCheckin(userId: Int, req: DailyCheckinRequest): DailyCheckinResponse {
        val start        = LocalTime.parse(req.sleepStart)
        val end          = LocalTime.parse(req.sleepEnd)
        val hoursSleep   = calculateHoursLegacy(start, end)
        val idealHours   = userRepo.getIdealSleepHours(userId)
        val sleepPercent = (hoursSleep / idealHours) * 100.0
        val sleepDebt    = (idealHours - hoursSleep).coerceAtLeast(0.0)
        val semaphore    = SemaphoreEngine.evaluate(sleepPercent, req.moodScore)
        val battery      = when (semaphore.color.name) {
            "GREEN"  -> 70; "YELLOW" -> 45; else -> 20
        }
        val checkin = checkinRepo.create(
            idUser = userId, sleepStart = req.sleepStart, sleepEnd = req.sleepEnd,
            hoursSleep = hoursSleep, idMood = req.moodScore.coerceIn(1, 5),
            idSemaphore = semaphore.semaphoreId, sleepDebt = sleepDebt, battery = battery
        )
        val user      = userRepo.findById(userId)
        val msgResult = MessageEngine.getMessage(user?.idRol, semaphore.color.name, battery)
        messageRepo.create(checkin.id, null, msgResult.full)

        return buildResponse(checkin.id, hoursSleep, sleepDebt, sleepPercent,
            req.moodScore, semaphore, battery, msgResult)
    }

    // ─── Consultas ────────────────────────────────────────────────────────────

    override suspend fun getHistory(userId: Int): List<DailyCheckinResponse> {
        val idealHours = userRepo.getIdealSleepHours(userId)
        val user       = userRepo.findById(userId)
        return checkinRepo.findByUser(userId)
            .filter { it.sleepEnd != null }
            .map { c -> toResponse(c, idealHours, user?.idRol) }
    }

    override suspend fun getTodayCheckin(userId: Int): DailyCheckinResponse? {
        val c = checkinRepo.findTodayByUser(userId) ?: return null
        if (c.sleepEnd == null) return null
        val idealHours = userRepo.getIdealSleepHours(userId)
        val user       = userRepo.findById(userId)
        return toResponse(c, idealHours, user?.idRol)
    }

    override suspend fun findOpenToday(userId: Int): DailyCheckin? =
        checkinRepo.findOpenTodayByUser(userId)

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun calculateHoursIso(start: String, end: String): Double {
        val s      = LocalDateTime.parse(start).toInstant(ZoneOffset.UTC)
        val e      = LocalDateTime.parse(end).toInstant(ZoneOffset.UTC)
        val diffMs = Duration.between(s, e).toMillis()
        return if (diffMs < 0) 0.0 else diffMs / 3_600_000.0
    }

    private fun calculateHoursLegacy(start: LocalTime, end: LocalTime): Double {
        val minutes = if (end.isAfter(start)) {
            Duration.between(start, end).toMinutes()
        } else {
            Duration.between(start, end).toMinutes() + (24 * 60)
        }
        return minutes / 60.0
    }

    private fun buildResponse(
        checkinId: Int, hoursSleep: Double, sleepDebt: Double,
        sleepPercent: Double, moodScore: Int,
        semaphore: SemaphoreEngine.SemaphoreResult,
        battery: Int, msgResult: MessageEngine.MessageResult
    ) = DailyCheckinResponse(
        checkinId    = checkinId,
        hoursSleep   = hoursSleep,
        sleepDebt    = sleepDebt,
        sleepPercent = sleepPercent,
        moodScore    = moodScore,
        semaphore    = SemaphoreResponse(semaphore.color.name, semaphore.label, semaphore.recommendation),
        batteryCog   = battery,
        fatiga       = (100 - battery).coerceAtLeast(0),
        message      = semaphore.recommendation,
        personalizedMessage = PersonalizedMessage(
            prefix       = msgResult.prefix,
            body         = msgResult.body,
            full         = msgResult.full,
            batteryRange = msgResult.batteryRange
        )
    )

    private fun toResponse(c: DailyCheckin, idealHours: Double, idRol: Int?): DailyCheckinResponse {
        val sleepPercent = ((c.hoursSleep ?: 0.0) / idealHours) * 100.0
        val mood         = c.idMood ?: 3
        val semaphore    = SemaphoreEngine.evaluate(sleepPercent, mood)
        val battery      = c.batteryCog ?: 0
        val msgResult    = MessageEngine.getMessage(idRol, semaphore.color.name, battery)
        return buildResponse(
            c.id, c.hoursSleep ?: 0.0, c.sleepDebt ?: 0.0,
            sleepPercent, mood, semaphore, battery, msgResult
        ).copy(fatiga = c.fatiga ?: (100 - battery).coerceAtLeast(0))
    }
}
