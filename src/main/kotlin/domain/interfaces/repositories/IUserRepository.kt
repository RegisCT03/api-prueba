package com.MindStack.domain.interfaces.repositories

import com.MindStack.domain.models.User

interface IUserRepository {
    suspend fun findByEmail(email: String): Pair<User, String>?
    suspend fun create(
        name: String, lastName: String, email: String, hashedPassword: String,
        dateOfBirth: String?, gender: String?, idealSleepHours: Double
    ): User
    suspend fun findById(id: Int): User?
    suspend fun getIdealSleepHours(userId: Int): Double
}