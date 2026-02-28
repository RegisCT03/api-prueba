package com.MindStack.infraestructure.database

import com.MindStack.infraestructure.database.entities.DailyCheckinTable
import com.MindStack.infraestructure.database.entities.GameSessionsTable
import com.MindStack.infraestructure.database.entities.GameTable
import com.MindStack.infraestructure.database.entities.MessageTable
import com.MindStack.infraestructure.database.entities.MoodTable
import com.MindStack.infraestructure.database.entities.RolTable
import com.MindStack.infraestructure.database.entities.SemaphoreTable
import com.MindStack.infraestructure.database.entities.StreaksHistoryTable
import com.MindStack.infraestructure.database.entities.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private lateinit var database: Database
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl              = config.jdbc
            driverClassName      = config.driver
            username             = config.username
            password             = config.password
            maximumPoolSize      = config.maxPoolSize
            isAutoCommit         = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            validate()
        }

        database = Database.connect(HikariDataSource(hikariConfig))
        logger.info("Database connected: ${config.jdbc}")

        transaction(database) {
            exec("DROP VIEW IF EXISTS vw_dashboard;")
            exec("DROP VIEW IF EXISTS vw_history;")
            exec("DROP VIEW IF EXISTS vw_user_profile;")
            // Orden importa: primero catálogos, luego tablas que los referencian
            SchemaUtils.createMissingTablesAndColumns(
                RolTable,
                MoodTable,
                SemaphoreTable,      // CORRECCIÓN: nombre "semaforo"
                GameTable,         // CORRECCIÓN: nombre "juego"
                UsersTable,
                StreaksHistoryTable,
                DailyCheckinTable,
                GameSessionsTable,
                MessageTable
            )
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}
