package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object UsersTable : Table("users") {
    val id              = integer("id").autoIncrement()
    val name            = varchar("name", 100)
    val lastName        = varchar("last_name", 100)
    val email           = varchar("email", 255)
    val password        = varchar("password", 255)
    val dateOfBirth     = date("date_of_birth").nullable()
    val gender          = varchar("gender", 100).nullable()
    val idRol           = integer("id_rol").references(RolTable.id).nullable()
    val idealSleepHours = double("ideal_sleep_hours").default(8.0)
    val createdAt       = timestamp("created_at")
    override val primaryKey = PrimaryKey(id)
}