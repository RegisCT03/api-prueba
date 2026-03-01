package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table

object MoodTable : Table("mood") {
    val id   = integer("id").autoIncrement()
    val mood = varchar("mood", 100)
    override val primaryKey = PrimaryKey(id)
}