package com.MindStack.presentation.di

import com.MindStack.application.services.AuthService
import com.MindStack.application.services.DailyCheckinService
import com.MindStack.application.services.GameService
import com.MindStack.domain.interfaces.services.IAuthService
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.domain.interfaces.services.IGameService
import com.MindStack.infraestructure.repositories.DailyCheckinRepository
import com.MindStack.infraestructure.repositories.GameSessionRepository
import com.MindStack.infraestructure.repositories.MessageRepository
import com.MindStack.infraestructure.repositories.UserRepository

class DependenciesDeclaration {

    private val userRepo        = UserRepository()
    private val checkinRepo     = DailyCheckinRepository()
    private val gameSessionRepo = GameSessionRepository()
    private val messageRepo     = MessageRepository()

    val authService: IAuthService = AuthService(userRepo = userRepo)

    val checkinService: IDailyCheckinService = DailyCheckinService(
        checkinRepo = checkinRepo,
        userRepo    = userRepo,
        messageRepo = messageRepo
    )

    val gameService: IGameService = GameService(
        gameSessionRepo = gameSessionRepo,
        checkinRepo     = checkinRepo,
        messageRepo     = messageRepo
    )
}