package com.MindStack.infraestructure.database

data class DatabaseConfig(
    val jdbc: String = System.getenv("JDBC_DATABASE_URL") ?: run {
        val host     = System.getenv("DB_HOST")     ?: "db"          // "db" = nombre del servicio en Docker
        val port     = System.getenv("DB_PORT")     ?: "5432"
        val database = System.getenv("DB_NAME")     ?: "mindstack"
        "jdbc:postgresql://$host:$port/$database"
    },
    val username: String = System.getenv("DB_USER")     ?: "postgres",
    val password: String = System.getenv("DB_PASSWORD") ?: "postgres",
    val driver: String   = "org.postgresql.Driver",
    val maxPoolSize: Int = 10
)