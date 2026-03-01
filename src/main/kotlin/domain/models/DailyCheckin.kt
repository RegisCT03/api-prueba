package com.MindStack.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class DailyCheckin(
    val id: Int = 0,
    val idUser: Int,
    val sleepStart: String? = null,
    val sleepEnd: String? = null,
    val hoursSleep: Double? = null,
    val idMood: Int? = null,
    val idSemaphore: Int? = null,
    val dateTime: String,
    val sleepDebt: Double? = null,
    val batteryCog: Int? = null,
    val fatiga: Int? = null
)