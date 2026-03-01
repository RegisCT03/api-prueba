package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class MemoryGameResponse(
    val sessionId: Int,
    val accuracyPercent: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)