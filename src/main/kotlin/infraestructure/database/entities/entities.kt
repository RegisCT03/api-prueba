package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

// ─── Catálogos ────────────────────────────────────────────────────────────────

object RolTable : Table("rol") {
    val id  = integer("id").autoIncrement()
    val rol = varchar("rol", 100)
    override val primaryKey = PrimaryKey(id)
}

object MoodTable : Table("mood") {
    val id   = integer("id").autoIncrement()
    val mood = varchar("mood", 100)
    override val primaryKey = PrimaryKey(id)
}

// CORRECCIÓN: nombre "semaforo" (no "semaphore") — coincide con seed y código
object SemaphoreTable : Table("semaphore") {
    val id          = integer("id").autoIncrement()
    val color       = varchar("color", 100)
    val description = varchar("description", 200).nullable()
    override val primaryKey = PrimaryKey(id)
}

// CORRECCIÓN: nombre "juego" (no "game") — coincide con seed y código
object GameTable : Table("game") {
    val id   = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

// ─── Usuarios ─────────────────────────────────────────────────────────────────

object UsersTable : Table("users") {
    val id              = integer("id").autoIncrement()
    val name            = varchar("name", 100)
    val lastName        = varchar("last_name", 100)
    val email           = varchar("email", 255)
    // CORRECCIÓN: VARCHAR(255) — bcrypt genera hashes de 60+ chars
    val password        = varchar("password", 255)
    val dateOfBirth     = date("date_of_birth").nullable()
    val gender          = varchar("gender", 100).nullable()
    val idRol           = integer("id_rol").references(RolTable.id).nullable()
    val idealSleepHours = double("ideal_sleep_hours").default(8.0)
    val createdAt       = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}

// ─── Streaks ──────────────────────────────────────────────────────────────────

object StreaksHistoryTable : Table("streaks_history") {
    val id        = integer("id").autoIncrement()
    // CORRECCIÓN: "userId" coincide con el nombre que usa Exposed internamente
    val userId    = integer("user_id").references(UsersTable.id).nullable()
    val startDate = date("start_date")
    val endDate   = date("end_date").nullable()
    val daysCount = integer("days_count").default(0)
    override val primaryKey = PrimaryKey(id)
}

// ─── Daily Check-in ───────────────────────────────────────────────────────────

object DailyCheckinTable : Table("daily_checkin") {
    val id         = integer("id").autoIncrement()
    val idUser     = integer("id_user").references(UsersTable.id)
    // Ambos nullable: se llena primero sleepStart (Zzz), luego sleepEnd (Levantarse)
    val sleepStart = varchar("sleep_start", 30).nullable()
    val sleepEnd   = varchar("sleep_end", 30).nullable()
    val hoursSleep = double("hours_sleep").nullable()
    val idMood     = integer("id_mood").references(MoodTable.id).nullable()
    // CORRECCIÓN: nombre "id_status" (no "id_semaphore")
    val idStatus   = integer("id_status").references(SemaphoreTable.id).nullable()
    val dateTime   = timestamp("date_time")
    val sleepDebt  = double("sleep_debt").nullable()
    val batteryCog = integer("battery_cog").nullable()
    // CORRECCIÓN: nombre "fatiga" (no "fatigue")
    val fatiga     = integer("fatiga").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Game Sessions ────────────────────────────────────────────────────────────

object GameSessionsTable : Table("game_sessions") {
    val id             = integer("id").autoIncrement()
    val idDailyCheckin = integer("id_daily_checkin").references(DailyCheckinTable.id)
    val startTime      = timestamp("start_time")
    val endTime        = timestamp("end_time")
    // CORRECCIÓN: nombre "id_juego" (no "id_game")
    val idGame        = integer("id_game").references(GameTable.id).nullable()
    val scoreValue     = double("score_value").nullable()
    val battery        = integer("battery").nullable()
    val metadata       = text("metadata").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Mensajes ─────────────────────────────────────────────────────────────────

object MessageTable : Table("message") {
    val id             = integer("id").autoIncrement()
    // CORRECCIÓN: ambos nullable — un mensaje viene de check-in O de juego, no ambos
    val idDailyCheckin = integer("id_daily_checkin")
        .references(DailyCheckinTable.id)
        .nullable()
    val idGameSession  = integer("id_game_session")
        .references(GameSessionsTable.id)
        .nullable()
    val message        = varchar("message", 500)
    val createdAt      = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}
