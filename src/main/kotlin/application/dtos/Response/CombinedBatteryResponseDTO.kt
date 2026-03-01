package com.MindStack.application.dtos.Response

import com.MindStack.application.dtos.PersonalizedMessage
import kotlinx.serialization.Serializable

@Serializable
data class CombinedBatteryResponse(
    val finalBattery: Int,
    val fatiga: Int,
    val semaphoreColor: String,
    val cognitiveSemaphore: String,
    val globalRecommendation: String,
    val personalizedMessage: PersonalizedMessage
)