package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class NeuroReflexResponse(
    val sessionId: Int,
    val averageMs: Double,
    val battery: Int,
    val label: String,
    val recommendation: String
)