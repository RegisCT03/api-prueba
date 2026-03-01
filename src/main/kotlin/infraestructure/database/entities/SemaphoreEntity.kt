package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table

object SemaphoreTable : Table("semaphore") {
    val id          = integer("id").autoIncrement()
    val color       = varchar("color", 100)
    val description = varchar("description", 200).nullable()
    override val primaryKey = PrimaryKey(id)
}