package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class DailyCheckinRequest(
    val sleepStart: String,
    val sleepEnd: String,
    val moodScore: Int
)