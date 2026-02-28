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

object SemaphoreTable : Table("semaphore") {
    val id          = integer("id").autoIncrement()
    val color       = varchar("color", 100)
    val description = varchar("description", 200).nullable()
    override val primaryKey = PrimaryKey(id)
}

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
    val password        = varchar("password", 255)   // 255 requerido por bcrypt
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
    // FIX: default = 1, no 0. Un streak recién creado empieza en el día 1.
    // El default 0 causaba el ALTER TABLE que PostgreSQL bloqueaba por la vista.
    val daysCount = integer("days_count").default(1)
    override val primaryKey = PrimaryKey(id)
}

// ─── Daily Check-in ───────────────────────────────────────────────────────────

object DailyCheckinTable : Table("daily_checkin") {
    val id          = integer("id").autoIncrement()
    val idUser      = integer("id_user").references(UsersTable.id)
    val sleepStart  = varchar("sleep_start", 100).nullable()
    val sleepEnd    = varchar("sleep_end", 100).nullable()
    val hoursSleep  = double("hours_sleep").nullable()
    val idMood      = integer("id_mood").references(MoodTable.id).nullable()
    val idSemaphore = integer("id_semaphore").references(SemaphoreTable.id).nullable()
    val dateTime    = timestamp("date_time")
    val sleepDebt   = double("sleep_debt").nullable()
    val batteryCog  = integer("battery_cog").nullable()
    // FIX: el nombre de columna en BD es "fatigue" — Exposed usa este string
    // para construir el SQL. El campo Kotlin se llama "fatiga" pero apunta a "fatigue".
    val fatiga      = integer("fatigue").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Game Sessions ────────────────────────────────────────────────────────────

object GameSessionsTable : Table("game_sessions") {
    val id             = integer("id").autoIncrement()
    val idDailyCheckin = integer("id_daily_checkin").references(DailyCheckinTable.id)
    val startTime      = timestamp("start_time")
    val endTime        = timestamp("end_time")
    val idGame         = integer("id_game").references(GameTable.id).nullable()
    val scoreValue     = double("score_value").nullable()
    val battery        = integer("battery").nullable()
    val metadata       = text("metadata").nullable()
    override val primaryKey = PrimaryKey(id)
}

// ─── Mensajes ─────────────────────────────────────────────────────────────────

object MessageTable : Table("message") {
    val id             = integer("id").autoIncrement()
    // FIX: ambos nullable — mensaje de sleep O de juego, nunca los dos a la vez
    val idDailyCheckin = integer("id_daily_checkin")
        .references(DailyCheckinTable.id)
        .nullable()
    // FIX: columna id_game_session agregada (faltaba — causaba 500 en juegos)
    val idGameSession  = integer("id_game_session")
        .references(GameSessionsTable.id)
        .nullable()
    val message        = varchar("message", 500)
    val createdAt      = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}