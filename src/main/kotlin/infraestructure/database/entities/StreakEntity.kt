package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date

object StreaksHistoryTable : Table("streaks_history") {
    val id        = integer("id").autoIncrement()
    val userId    = integer("user_id").references(UsersTable.id).nullable()
    val startDate = date("start_date")
    val endDate   = date("end_date").nullable()
    val daysCount = integer("days_count").default(1)
    override val primaryKey = PrimaryKey(id)
}