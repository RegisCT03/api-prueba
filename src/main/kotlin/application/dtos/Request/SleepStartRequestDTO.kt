package com.MindStack.application.dtos.Request

import kotlinx.serialization.Serializable

@Serializable
data class SleepStartRequest(
    val sleepStart: String
)