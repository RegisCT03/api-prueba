package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

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
    val fatiga      = integer("fatigue").nullable()
    override val primaryKey = PrimaryKey(id)
}