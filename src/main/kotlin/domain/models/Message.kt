package com.MindStack.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: Int = 0,
    val idDailyCheckin: Int? = null,
    val idGameSession: Int? = null,
    val message: String
)