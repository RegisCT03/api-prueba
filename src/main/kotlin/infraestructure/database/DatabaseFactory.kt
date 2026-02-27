package com.MindStack.infraestructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory



object DatabaseFactory {
    private lateinit var database: Database
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init(config: DatabaseConfig) {
        val hikariConfig = HikariConfig().apply {
            // Estos valores vienen de tu archivo .env del Back-end
            jdbcUrl = config.jdbc
            driverClassName = "org.postgresql.Driver"
            username = config.username
            password = config.password

            maximumPoolSize = config.maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            // Optimización para Postgres en contenedores
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

            validate()
        }

        val dataSource = HikariDataSource(hikariConfig)
        database = Database.connect(dataSource)

        logger.info("Database connection established at ${config.jdbc}")
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
}