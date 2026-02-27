package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.domain.interfaces.repositories.IGameSessionRepository
import com.MindStack.domain.interfaces.repositories.IMessageRepository
import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.models.DailyCheckin
import com.MindStack.domain.models.GameSession
import com.MindStack.domain.models.Message
import com.MindStack.domain.models.User
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.DailyCheckinTable
import com.MindStack.infraestructure.database.entities.GameSessionsTable
import com.MindStack.infraestructure.database.entities.MessageTable
import com.MindStack.infraestructure.database.entities.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

// ─── UserRepository ───────────────────────────────────────────────────────────

class UserRepository : IUserRepository {

    override suspend fun create(
        name: String, lastName: String, email: String, hashedPassword: String,
        dateOfBirth: String?, gender: String?, idealSleepHours: Double
    ): User = dbQuery {
        val insertedId = UsersTable.insert {
            it[UsersTable.name]            = name
            it[UsersTable.lastName]        = lastName
            it[UsersTable.email]           = email
            it[UsersTable.password]        = hashedPassword
            it[UsersTable.dateOfBirth]     = dateOfBirth?.let { d -> LocalDate.parse(d) }
            it[UsersTable.gender]          = gender
            it[UsersTable.idealSleepHours] = idealSleepHours
            it[UsersTable.idRol]           = 2
            it[UsersTable.createdAt]       = Instant.now()
        } get UsersTable.id

        User(
            id              = insertedId,
            name            = name,
            lastName        = lastName,
            email           = email,
            dateOfBirth     = dateOfBirth,
            gender          = gender,
            idRol           = 2,
            idealSleepHours = idealSleepHours
        )
    }

    override suspend fun findByEmail(email: String): Pair<User, String>? = dbQuery {
        UsersTable.select { UsersTable.email eq email }
            .map { row -> rowToUserWithPassword(row) }
            .singleOrNull()
    }

    override suspend fun findById(id: Int): User? = dbQuery {
        UsersTable.select { UsersTable.id eq id }
            .map { row -> rowToUser(row) }
            .singleOrNull()
    }

    override suspend fun getIdealSleepHours(userId: Int): Double = dbQuery {
        UsersTable.select { UsersTable.id eq userId }
            .single()[UsersTable.idealSleepHours]
    }

    private fun rowToUser(row: ResultRow) = User(
        id              = row[UsersTable.id],
        name            = row[UsersTable.name],
        lastName        = row[UsersTable.lastName],
        email           = row[UsersTable.email],
        dateOfBirth     = row[UsersTable.dateOfBirth]?.toString(),
        gender          = row[UsersTable.gender],
        idRol           = row[UsersTable.idRol],
        idealSleepHours = row[UsersTable.idealSleepHours]
    )

    private fun rowToUserWithPassword(row: ResultRow): Pair<User, String> =
        Pair(rowToUser(row), row[UsersTable.password])
}

// ─── DailyCheckinRepository ───────────────────────────────────────────────────

class DailyCheckinRepository : IDailyCheckinRepository {

    override suspend fun create(
        idUser: Int, sleepStart: String, sleepEnd: String,
        hoursSleep: Double, idMood: Int, idStatus: Int,
        sleepDebt: Double, battery: Int
    ): DailyCheckin = dbQuery {
        val now = Instant.now()
        val insertedId = DailyCheckinTable.insert {
            it[DailyCheckinTable.idUser]     = idUser
            it[DailyCheckinTable.sleepStart] = sleepStart
            it[DailyCheckinTable.sleepEnd]   = sleepEnd
            it[DailyCheckinTable.hoursSleep] = hoursSleep
            it[DailyCheckinTable.idMood]     = idMood
            it[DailyCheckinTable.idStatus]   = idStatus
            it[DailyCheckinTable.dateTime]   = now
            it[DailyCheckinTable.sleepDebt]  = sleepDebt
            it[DailyCheckinTable.batteryCog] = battery
            it[DailyCheckinTable.fatiga]     = (100 - battery).coerceAtLeast(0)
        } get DailyCheckinTable.id

        DailyCheckin(
            id         = insertedId,
            idUser     = idUser,
            sleepStart = sleepStart,
            sleepEnd   = sleepEnd,
            hoursSleep = hoursSleep,
            idMood     = idMood,
            idStatus   = idStatus,
            dateTime   = now.toString(),
            sleepDebt  = sleepDebt,
            batteryCog = battery,
            fatiga     = (100 - battery).coerceAtLeast(0)
        )
    }

    override suspend fun findById(id: Int): DailyCheckin? = dbQuery {
        DailyCheckinTable.select { DailyCheckinTable.id eq id }
            .map { toModel(it) }
            .singleOrNull()
    }

    override suspend fun findByUser(userId: Int): List<DailyCheckin> = dbQuery {
        DailyCheckinTable
            .select { DailyCheckinTable.idUser eq userId }
            .orderBy(DailyCheckinTable.dateTime, SortOrder.DESC)
            .map { toModel(it) }
    }

    override suspend fun findTodayByUser(userId: Int): DailyCheckin? = dbQuery {
        val startOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
        DailyCheckinTable
            .select {
                (DailyCheckinTable.idUser eq userId) and
                        (DailyCheckinTable.dateTime greaterEq startOfDay)
            }
            .orderBy(DailyCheckinTable.dateTime, SortOrder.DESC)
            .map { toModel(it) }
            .firstOrNull()
    }

    override suspend fun updateBattery(checkinId: Int, battery: Int): Unit = dbQuery {
        DailyCheckinTable.update({ DailyCheckinTable.id eq checkinId }) {
            it[batteryCog] = battery
            it[fatiga]     = (100 - battery).coerceAtLeast(0)
        }
    }

    private fun toModel(row: ResultRow) = DailyCheckin(
        id         = row[DailyCheckinTable.id],
        idUser     = row[DailyCheckinTable.idUser],
        sleepStart = row[DailyCheckinTable.sleepStart],
        sleepEnd   = row[DailyCheckinTable.sleepEnd],
        hoursSleep = row[DailyCheckinTable.hoursSleep],
        idMood     = row[DailyCheckinTable.idMood],
        idStatus   = row[DailyCheckinTable.idStatus],
        dateTime   = row[DailyCheckinTable.dateTime].toString(),
        sleepDebt  = row[DailyCheckinTable.sleepDebt],
        batteryCog = row[DailyCheckinTable.batteryCog],
        fatiga     = row[DailyCheckinTable.fatiga]
    )
}

// ─── GameSessionRepository ────────────────────────────────────────────────────

class GameSessionRepository : IGameSessionRepository {

    override suspend fun create(
        idDailyCheckin: Int, idJuego: Int,
        scoreValue: Double, battery: Int, metadata: String
    ): GameSession = dbQuery {
        val now = Instant.now()
        val insertedId = GameSessionsTable.insert {
            it[GameSessionsTable.idDailyCheckin] = idDailyCheckin
            it[GameSessionsTable.idJuego]        = idJuego
            it[GameSessionsTable.startTime]      = now
            it[GameSessionsTable.endTime]        = now
            it[GameSessionsTable.scoreValue]     = scoreValue
            it[GameSessionsTable.battery]        = battery
            it[GameSessionsTable.metadata]       = metadata
        } get GameSessionsTable.id

        GameSession(
            id             = insertedId,
            idDailyCheckin = idDailyCheckin,
            idJuego        = idJuego,
            startTime      = now.toString(),
            endTime        = now.toString(),
            scoreValue     = scoreValue,
            battery        = battery,
            metadata       = metadata
        )
    }

    override suspend fun findByCheckin(checkinId: Int): List<GameSession> = dbQuery {
        GameSessionsTable
            .select { GameSessionsTable.idDailyCheckin eq checkinId }
            .map { row ->
                GameSession(
                    id             = row[GameSessionsTable.id],
                    idDailyCheckin = row[GameSessionsTable.idDailyCheckin],
                    idJuego        = row[GameSessionsTable.idJuego] ?: 0,
                    startTime      = row[GameSessionsTable.startTime].toString(),
                    endTime        = row[GameSessionsTable.endTime].toString(),
                    scoreValue     = row[GameSessionsTable.scoreValue],
                    battery        = row[GameSessionsTable.battery],
                    metadata       = row[GameSessionsTable.metadata]
                )
            }
    }
}

// ─── MessageRepository ────────────────────────────────────────────────────────

class MessageRepository : IMessageRepository {

    override suspend fun create(
        idDailyCheckin: Int?, idGameSession: Int?, message: String
    ): Message = dbQuery {
        val insertedId = MessageTable.insert {
            it[MessageTable.idDailyCheckin] = idDailyCheckin ?: 0
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
                    idGameSession  = null,
                    message        = row[MessageTable.message]
                )
            }
    }
}