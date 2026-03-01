package com.MindStack.presentation.di

import com.MindStack.application.services.AuthService
import com.MindStack.domain.interfaces.services.IAuthService
import com.MindStack.infraestructure.repositories.UserRepository

class DependenciesDeclaration {

    private val userRepo        = UserRepository()

    val authService: IAuthService = AuthService(userRepo = userRepo)
}