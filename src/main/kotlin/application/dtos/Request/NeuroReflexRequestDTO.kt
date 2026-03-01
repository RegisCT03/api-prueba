package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class NeuroReflexRequest(
    val idDailyCheckin: Int,
    val reactionTime1Ms: Double,
    val reactionTime2Ms: Double,
    val reactionTime3Ms: Double
)