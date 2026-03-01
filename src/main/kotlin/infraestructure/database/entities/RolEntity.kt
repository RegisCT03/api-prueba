package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table

object RolTable : Table("rol") {
    val id  = integer("id").autoIncrement()
    val rol = varchar("rol", 100)
    override val primaryKey = PrimaryKey(id)
}