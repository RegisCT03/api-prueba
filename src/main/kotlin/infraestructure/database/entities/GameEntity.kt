package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table

object GameTable : Table("game") {
    val id   = integer("id").autoIncrement()
    val name = varchar("name", 100)
    override val primaryKey = PrimaryKey(id)
}