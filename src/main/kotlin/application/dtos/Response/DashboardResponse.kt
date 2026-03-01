package com.MindStack.application.dtos.Response

import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    val todayCheckin: DailyCheckinResponse?,
    val streak: StreakResponse,
    val weekSleepAvgHours: Double,
    val weekBatteryAvg: Double,
    val hasPendingSleepStart: Boolean,
    val pendingCheckinId: Int?
)