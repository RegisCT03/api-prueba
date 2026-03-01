package com.MindStack.infraestructure.repositories

import com.MindStack.domain.interfaces.repositories.IUserRepository
import com.MindStack.domain.models.User
import com.MindStack.infraestructure.database.DatabaseFactory.dbQuery
import com.MindStack.infraestructure.database.entities.UsersTable
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.Instant
import java.time.LocalDate

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