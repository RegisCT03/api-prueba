package com.MindStack.domain.interfaces.services

import com.MindStack.application.dtos.Request.DailyCheckinRequest
import com.MindStack.application.dtos.Request.SleepStartRequest
import com.MindStack.application.dtos.Response.DailyCheckinResponse
import com.MindStack.application.dtos.Response.SleepEndRequest
import com.MindStack.application.dtos.Response.SleepStartResponse
import com.MindStack.domain.models.DailyCheckin

interface IDailyCheckinService {
    suspend fun submitCheckin(userId: Int, req: DailyCheckinRequest): DailyCheckinResponse
    suspend fun startSleep(userId: Int, req: SleepStartRequest): SleepStartResponse
    suspend fun endSleep(userId: Int, checkinId: Int, req: SleepEndRequest): DailyCheckinResponse
    suspend fun getHistory(userId: Int): List<DailyCheckinResponse>
    suspend fun getTodayCheckin(userId: Int): DailyCheckinResponse?
    suspend fun findOpenToday(userId: Int): DailyCheckin?
}