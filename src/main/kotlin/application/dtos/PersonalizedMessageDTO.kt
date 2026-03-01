package com.MindStack.application.dtos

import kotlinx.serialization.Serializable

@Serializable
data class PersonalizedMessage(
    val prefix: String,
    val body: String,
    val full: String,
    val batteryRange: String
)