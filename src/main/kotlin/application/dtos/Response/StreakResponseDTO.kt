package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class StreakResponse(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDays: Int,
    val goalDays: Int = 20,
    val progressPercent: Double,
    val isGoalAchieved: Boolean
)