package com.MindStack.application.dtos.Response

import com.MindStack.application.dtos.PersonalizedMessage
import kotlinx.serialization.Serializable

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
    val message: String,
    val personalizedMessage: PersonalizedMessage
)