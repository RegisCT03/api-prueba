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

object SemaforoTable : Table("semaforo") {
    val id          = integer("id").autoIncrement()
    val color       = varchar("color", 100)
    val description = varchar("description", 100).nullable()
    override val primaryKey = PrimaryKey(id)
}

object JuegoTable : Table("juego") {
    val id   = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}

// ─── Usuarios ─────────────────────────────────────────────────────────────────

object UsersTable : Table("users") {
    val id              = integer("id").autoIncrement()
    val name            = varchar("name", 100)
    val lastName        = varchar("last_name", 100)
    val email           = varchar("email", 100)
    val password        = varchar("password", 100)
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
    val sleepStart = varchar("sleep_start", 10).nullable()
    val sleepEnd   = varchar("sleep_end", 10).nullable()
    val hoursSleep = double("hours_sleep").nullable()
    val idMood     = integer("id_mood").references(MoodTable.id).nullable()
    val idStatus   = integer("id_status").references(SemaforoTable.id).nullable()
    val dateTime   = timestamp("date_time")
    val sleepDebt  = double("sleep_debt").nullable()
    val batteryCog = integer("battery_cog").nullable()
    val fatiga     = integer("fatiga").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Game Sessions ────────────────────────────────────────────────────────────

object GameSessionsTable : Table("game_sessions") {
    val id             = integer("id").autoIncrement()
    val idDailyCheckin = integer("id_daily_checkin").references(DailyCheckinTable.id)
    val startTime      = timestamp("start_time")
    val endTime        = timestamp("end_time")
    val idJuego        = integer("id_juego").references(JuegoTable.id).nullable()
    val scoreValue     = double("score_value").nullable()
    val battery        = integer("battery").nullable()
    val metadata       = text("metadata").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Mensajes ─────────────────────────────────────────────────────────────────

object MessageTable : Table("message") {
    val id             = integer("id").autoIncrement()
    val idDailyCheckin = integer("id_daily_checkin").references(DailyCheckinTable.id)
    val message        = varchar("message", 255)
    val createdAt      = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}