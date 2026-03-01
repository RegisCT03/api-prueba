package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class MemoryGameRequest(
    val idDailyCheckin: Int,
    val correctHits: Int,
    val totalRequired: Int
)