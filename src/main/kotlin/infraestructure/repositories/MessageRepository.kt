package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.models.Message
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.MessageTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant

class MessageRepository : IMessageRepository {

    override suspend fun create(
        idDailyCheckin: Int?, idGameSession: Int?, message: String
    ): Message = dbQuery {
        val insertedId = MessageTable.insert {
            it[MessageTable.idDailyCheckin] = idDailyCheckin
            it[MessageTable.idGameSession]  = idGameSession
            it[MessageTable.message]        = message
            it[MessageTable.createdAt]      = Instant.now()
        } get MessageTable.id

        Message(
            id             = insertedId,
            idDailyCheckin = idDailyCheckin,
            idGameSession  = idGameSession,
            message        = message
        )
    }

    override suspend fun findByCheckin(checkinId: Int): List<Message> = dbQuery {
        MessageTable
            .select { MessageTable.idDailyCheckin eq checkinId }
            .map { row ->
                Message(
                    id             = row[MessageTable.id],
                    idDailyCheckin = row[MessageTable.idDailyCheckin],
                    idGameSession  = row[MessageTable.idGameSession],
                    message        = row[MessageTable.message]
                )
            }
    }
}