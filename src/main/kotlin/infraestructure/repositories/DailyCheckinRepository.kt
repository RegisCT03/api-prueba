package com.MindStack.infraestructure.repositories

import com.MindStack.domain.models.DailyCheckin
import com.MindStack.domain.interfaces.repositories.IDailyCheckinRepository
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.DailyCheckinTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class DailyCheckinRepository : IDailyCheckinRepository {

    override suspend fun create(
        idUser: Int, sleepStart: String, sleepEnd: String,
        hoursSleep: Double, idMood: Int, idSemaphore: Int,
        sleepDebt: Double, battery: Int
    ): DailyCheckin = dbQuery {
        val now = Instant.now()
        val insertedId = DailyCheckinTable.insert {
            it[DailyCheckinTable.idUser]     = idUser
            it[DailyCheckinTable.sleepStart] = sleepStart
            it[DailyCheckinTable.sleepEnd]   = sleepEnd
            it[DailyCheckinTable.hoursSleep] = hoursSleep
            it[DailyCheckinTable.idMood]     = idMood
            it[DailyCheckinTable.idSemaphore]   = idSemaphore
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
            idSemaphore   = idSemaphore,
            dateTime   = now.toString(),
            sleepDebt  = sleepDebt,
            batteryCog = battery,
            fatiga     = (100 - battery).coerceAtLeast(0)
        )
    }

    override suspend fun createOpen(idUser: Int, sleepStart: String): DailyCheckin = dbQuery {
        val now = Instant.now()
        val insertedId = DailyCheckinTable.insert {
            it[DailyCheckinTable.idUser]     = idUser
            it[DailyCheckinTable.sleepStart] = sleepStart
            it[DailyCheckinTable.dateTime]   = now
        } get DailyCheckinTable.id

        DailyCheckin(
            id         = insertedId,
            idUser     = idUser,
            sleepStart = sleepStart,
            dateTime   = now.toString()
        )
    }

    override suspend fun findOpenTodayByUser(userId: Int): DailyCheckin? = dbQuery {
        val startOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)
        DailyCheckinTable
            .select {
                (DailyCheckinTable.idUser eq userId) and
                        (DailyCheckinTable.dateTime greaterEq startOfDay) and
                        (DailyCheckinTable.sleepEnd.isNull())
            }
            .orderBy(DailyCheckinTable.dateTime, SortOrder.DESC)
            .map { toModel(it) }
            .firstOrNull()
    }

    override suspend fun closeCheckin(
        checkinId: Int,
        sleepEnd: String,
        hoursSleep: Double,
        idMood: Int,
        idSemaphore: Int,
        sleepDebt: Double,
        battery: Int
    ): DailyCheckin = dbQuery {
        DailyCheckinTable.update({ DailyCheckinTable.id eq checkinId }) {
            it[DailyCheckinTable.sleepEnd]   = sleepEnd
            it[DailyCheckinTable.hoursSleep] = hoursSleep
            it[DailyCheckinTable.idMood]     = idMood
            it[DailyCheckinTable.idSemaphore]   = idSemaphore
            it[DailyCheckinTable.sleepDebt]  = sleepDebt
            it[DailyCheckinTable.batteryCog] = battery
            it[DailyCheckinTable.fatiga]     = (100 - battery).coerceAtLeast(0)
        }
        findById(checkinId)!!
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
        idSemaphore   = row[DailyCheckinTable.idSemaphore],
        dateTime   = row[DailyCheckinTable.dateTime].toString(),
        sleepDebt  = row[DailyCheckinTable.sleepDebt],
        batteryCog = row[DailyCheckinTable.batteryCog],
        fatiga     = row[DailyCheckinTable.fatiga]
    )
}