package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object MessageTable : Table("message") {
    val id             = integer("id").autoIncrement()
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