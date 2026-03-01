package com.MindStack.infraestructure.database.entities

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

// ─── Tipo personalizado para columnas JSONB de PostgreSQL ─────────────────────
//
// PROBLEMA:  Exposed no tiene soporte nativo para JSONB.
//            Si usas text() o varchar(), el driver JDBC envía el valor como
//            'character varying' y Postgres lanza:
//            "column X is of type jsonb but expression is of type character varying"
//
// SOLUCIÓN:  Usar PGobject con type = "jsonb". El driver PostgreSQL JDBC
//            reconoce PGobject y hace el binding correcto al tipo de la columna.
//
class JsonbColumnType : ColumnType() {

    // Indica a Postgres el tipo SQL de la columna al crear/alterar la tabla
    override fun sqlType(): String = "JSONB"

    // Al leer de la BD: PGobject → String
    override fun valueFromDB(value: Any): Any = when (value) {
        is PGobject -> value.value ?: "" // Si es nulo, devolvemos un string vacío
        is String   -> value
        else        -> value
    }

    // Al escribir en la BD: String → PGobject con type "jsonb"
    // Esto es lo que resuelve el error — el driver ve un PGobject, no un VARCHAR
    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val pgObj = PGobject().apply {
            type = "jsonb"
            this.value = value as? String  // null si el valor es null
        }
        stmt[index] = pgObj
    }

    // Permite que Exposed sepa que esta columna puede ser null
    override fun notNullValueToDB(value: Any): Any {
        val pgObj = PGobject().apply {
            type  = "jsonb"
            this.value = value as? String
        }
        return pgObj
    }
}

// ─── Extension function para registrar la columna JSONB en una Table ─────────
fun Table.jsonb(name: String): Column<String> =
    registerColumn(name, JsonbColumnType())