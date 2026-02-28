package com.MindStack.presentation.routing.routes

import com.MindStack.application.dtos.DashboardResponse
import com.MindStack.application.services.StreakService
import com.MindStack.domain.interfaces.services.IDailyCheckinService
import com.MindStack.presentation.plugins.userId
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.dashboardRoutes(
    checkinService: IDailyCheckinService,
    streakService: StreakService
) {
    authenticate("auth-jwt") {
        route("/dashboard") {

            // GET /api/v1/dashboard
            // Retorna: check-in de hoy (si existe), racha, promedios semanales,
            //          y si hay un Zzz sin Levantarse pendiente
            get {
                val userId  = call.principal<JWTPrincipal>()!!.userId()
                val today   = checkinService.getTodayCheckin(userId)
                val streak  = streakService.getStreakInfo(userId)
                val history = checkinService.getHistory(userId)
                val openCheckin = checkinService.findOpenToday(userId)

                // Últimos 7 días para promedios
                val week = history.take(7)
                val weekSleepAvg = week
                    .map { it.hoursSleep }
                    .takeIf { it.isNotEmpty() }
                    ?.average() ?: 0.0
                val weekBatteryAvg = week
                    .map { it.batteryCog.toDouble() }
                    .takeIf { it.isNotEmpty() }
                    ?.average() ?: 0.0

                call.respond(
                    HttpStatusCode.OK,
                    DashboardResponse(
                        todayCheckin         = today,
                        streak               = streak,
                        weekSleepAvgHours    = weekSleepAvg,
                        weekBatteryAvg       = weekBatteryAvg,
                        hasPendingSleepStart = openCheckin != null,
                        pendingCheckinId     = openCheckin?.id
                    )
                )
            }
        }
    }
}
