package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object GameSessionsTable : Table("game_sessions") {
    val id             = integer("id").autoIncrement()
    val idDailyCheckin = integer("id_daily_checkin").references(DailyCheckinTable.id)
    val startTime      = timestamp("start_time").nullable()
    val endTime        = timestamp("end_time").nullable()
    val idGame         = integer("id_game").references(GameTable.id).nullable()
    val scoreValue     = double("score_value").nullable()
    val battery        = integer("battery").nullable()
    val metadata       = jsonb("metadata").nullable()
    override val primaryKey = PrimaryKey(id)
}