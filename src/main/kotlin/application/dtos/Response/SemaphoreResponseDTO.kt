package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class SemaphoreResponse(
    val color: String,
    val label: String,
    val recommendation: String
)