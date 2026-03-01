package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.Message

interface IMessageRepository {
    suspend fun create(idDailyCheckin: Int?, idGameSession: Int?, message: String): Message
    suspend fun findByCheckin(checkinId: Int): List<Message>
}