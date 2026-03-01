package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class SleepStartResponse(
    val checkinId: Int,
    val sleepStart: String,
    val message: String
)

@Serializable
data class SleepEndRequest(
    val sleepEnd: String,
    val moodScore: Int
)