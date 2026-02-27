package com.MindStack.application.services

import com.MindStack.application.dtos.CombinedBatteryResponse
import com.MindStack.application.dtos.MemoryGameRequest
import com.MindStack.application.dtos.MemoryGameResponse
import com.MindStack.application.dtos.NeuroReflexRequest
import com.MindStack.application.dtos.NeuroReflexResponse
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.domain.interfaces.repositories.IGameSessionRepository
import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.interfaces.services.IGameService

private const val JUEGO_NEURO_REFLEX = 1
private const val JUEGO_MEMORY       = 2

class GameService(
    private val gameSessionRepo: IGameSessionRepository,
    private val checkinRepo: IDailyCheckinRepository,
    private val messageRepo: IMessageRepository
) : IGameService {

    override suspend fun submitNeuroReflex(userId: Int, req: NeuroReflexRequest): NeuroReflexResponse {
        val averageMs = (req.reactionTime1Ms + req.reactionTime2Ms + req.reactionTime3Ms) / 3.0
        val battery   = CognitiveBatteryEngine.neuroReflexBattery(averageMs)
        val result    = CognitiveBatteryEngine.evaluate(battery)

        val metadata = """{"game":"neuro-reflex","time1Ms":${req.reactionTime1Ms},"time2Ms":${req.reactionTime2Ms},"time3Ms":${req.reactionTime3Ms},"averageMs":$averageMs}"""

        val session = gameSessionRepo.create(
            idDailyCheckin = req.idDailyCheckin,
            idJuego        = JUEGO_NEURO_REFLEX,
            scoreValue     = averageMs,
            battery        = battery,
            metadata       = metadata
        )

        refreshCombinedBattery(req.idDailyCheckin)
        messageRepo.create(req.idDailyCheckin, session.id, result.recommendation)

        return NeuroReflexResponse(
            sessionId      = session.id,
            averageMs      = averageMs,
            battery        = battery,
            label          = result.label,
            recommendation = result.recommendation
        )
    }

    override suspend fun submitMemoryGame(userId: Int, req: MemoryGameRequest): MemoryGameResponse {
        val accuracy = (req.correctHits.toDouble() / req.totalRequired.toDouble()) * 100.0
        val battery  = CognitiveBatteryEngine.memoryBattery(accuracy)
        val result   = CognitiveBatteryEngine.evaluate(battery)

        val metadata = """{"game":"memory-work","correctHits":${req.correctHits},"totalRequired":${req.totalRequired},"accuracyPercent":$accuracy}"""

        val session = gameSessionRepo.create(
            idDailyCheckin = req.idDailyCheckin,
            idJuego        = JUEGO_MEMORY,
            scoreValue     = accuracy,
            battery        = battery,
            metadata       = metadata
        )

        refreshCombinedBattery(req.idDailyCheckin)
        messageRepo.create(req.idDailyCheckin, session.id, result.recommendation)

        return MemoryGameResponse(
            sessionId       = session.id,
            accuracyPercent = accuracy,
            battery         = battery,
            label           = result.label,
            recommendation  = result.recommendation
        )
    }

    override suspend fun getCombinedBattery(checkinId: Int): CombinedBatteryResponse {
        val checkin  = checkinRepo.findById(checkinId)
            ?: throw IllegalArgumentException("Check-in no encontrado.")
        val sessions = gameSessionRepo.findByCheckin(checkinId)

        val batteryA = sessions.firstOrNull { it.idJuego == JUEGO_NEURO_REFLEX }?.battery
        val batteryB = sessions.firstOrNull { it.idJuego == JUEGO_MEMORY }?.battery
        val combined = CognitiveBatteryEngine.combinedBattery(batteryA, batteryB)
        val cognitive = CognitiveBatteryEngine.evaluate(combined)

        val semaphoreColor = when (checkin.idStatus) {
            1 -> "GREEN"; 2 -> "YELLOW"; else -> "RED"
        }

        return CombinedBatteryResponse(
            finalBattery         = combined,
            fatiga               = (100 - combined).coerceAtLeast(0),
            semaphoreColor       = semaphoreColor,
            cognitiveStatus      = cognitive.label,
            globalRecommendation = CognitiveBatteryEngine.globalRecommendation(semaphoreColor, combined)
        )
    }

    private suspend fun refreshCombinedBattery(checkinId: Int) {
        val sessions = gameSessionRepo.findByCheckin(checkinId)
        val batteryA = sessions.firstOrNull { it.idJuego == JUEGO_NEURO_REFLEX }?.battery
        val batteryB = sessions.firstOrNull { it.idJuego == JUEGO_MEMORY }?.battery
        checkinRepo.updateBattery(checkinId, CognitiveBatteryEngine.combinedBattery(batteryA, batteryB))
    }
}