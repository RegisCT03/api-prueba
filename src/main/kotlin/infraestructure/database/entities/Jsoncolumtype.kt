package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

class JsonbColumnType : ColumnType() {

    override fun sqlType(): String = "JSONB"

    override fun valueFromDB(value: Any): Any = when (value) {
        is PGobject -> value.value ?: ""
        is String   -> value
        else        -> value
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val pgObj = PGobject().apply {
            type = "jsonb"
            this.value = value as? String
        }
        stmt[index] = pgObj
    }

    override fun notNullValueToDB(value: Any): Any {
        val pgObj = PGobject().apply {
            type  = "jsonb"
            this.value = value as? String
        }
        return pgObj
    }
}

fun Table.jsonb(name: String): Column<String> =
    registerColumn(name, JsonbColumnType())