package com.MindStack.application.services

import com.MindStack.domain.models.TrafficLight

object SemaphoreEngine {

    data class SemaphoreResult(
        val color: TrafficLight,
        val label: String,
        val recommendation: String,
        val semaphoreId: Int
    )

    fun evaluate(sleepPercent: Double, moodScore: Int): SemaphoreResult = when {
        sleepPercent >= 90.0 && moodScore >= 4 -> SemaphoreResult(
            color          = TrafficLight.GREEN,
            label          = "Funcionamiento óptimo",
            recommendation = "Estás en tu mejor momento. Ideal para tareas cognitivamente exigentes: estudio, análisis o decisiones importantes.",
            semaphoreId       = 1
        )
        sleepPercent >= 70.0 && moodScore >= 2 -> SemaphoreResult(
            color          = TrafficLight.YELLOW,
            label          = "Rendimiento reducido",
            recommendation = "Energía moderada. Puedes trabajar en tareas rutinarias. Evita decisiones de alto impacto y toma descansos cortos (20 min).",
            semaphoreId    = 2
        )
        else -> SemaphoreResult(
            color          = TrafficLight.RED,
            label          = "Riesgo de crisis cognitiva",
            recommendation = "Tu cuerpo y mente necesitan recuperación urgente. Prioriza descansar antes de cualquier actividad que requiera concentración.",
            semaphoreId       = 3
        )
    }
}

object CognitiveBatteryEngine {

    data class BatteryResult(
        val batteryLevel: Int,
        val label: String,
        val recommendation: String
    )

    private const val REACTION_BEST_MS  = 250.0
    private const val REACTION_WORST_MS = 650.0

    fun neuroReflexBattery(averageMs: Double): Int = when {
        averageMs <= REACTION_BEST_MS  -> 100
        averageMs >= REACTION_WORST_MS -> 0
        else -> ((REACTION_WORST_MS - averageMs) / (REACTION_WORST_MS - REACTION_BEST_MS) * 100)
            .toInt().coerceIn(0, 100)
    }

    fun memoryBattery(accuracyPercent: Double): Int =
        accuracyPercent.toInt().coerceIn(0, 100)

    fun combinedBattery(batteryA: Int?, batteryB: Int?): Int {
        val values = listOfNotNull(batteryA, batteryB)
        return if (values.isEmpty()) 0 else values.average().toInt()
    }

    fun evaluate(batteryLevel: Int): BatteryResult = when {
        batteryLevel >= 75 -> BatteryResult(
            batteryLevel   = batteryLevel,
            label          = "Alta concentración",
            recommendation = "Capacidad cognitiva alta. Ideal para aprender cosas nuevas, resolver problemas complejos y memorizar contenido."
        )
        batteryLevel >= 45 -> BatteryResult(
            batteryLevel   = batteryLevel,
            label          = "Concentración moderada",
            recommendation = "Puedes realizar tareas de dificultad media. Evita multitarea y trabaja en bloques de 25 minutos (Pomodoro)."
        )
        else -> BatteryResult(
            batteryLevel   = batteryLevel,
            label          = "Fatiga cognitiva detectada",
            recommendation = "Tu cerebro está fatigado. Descansa al menos 30 minutos antes de actividades que requieran atención sostenida."
        )
    }

    fun globalRecommendation(semaphoreColor: String, batteryLevel: Int): String = when {
        semaphoreColor == "GREEN"  && batteryLevel >= 75 ->
            "🟢 Momento ÓPTIMO. Sueño, ánimo y concentración alineados. Es el mejor momento para estudiar o proyectos importantes."
        semaphoreColor == "GREEN"  && batteryLevel < 75  ->
            "🟡 Buen descanso pero concentración baja. Haz actividad física ligera o una siesta de 20 min para activar tu rendimiento."
        semaphoreColor == "YELLOW" && batteryLevel >= 75 ->
            "🟡 Alta concentración pero energía moderada. Aprovecha el foco para tareas cortas antes de que baje tu energía."
        semaphoreColor == "YELLOW" && batteryLevel < 75  ->
            "🟡 Rendimiento reducido en general. Mantén actividades rutinarias y planifica una recuperación de sueño esta noche."
        semaphoreColor == "RED"    && batteryLevel >= 75 ->
            "🔴 Concentración resistente pero cuerpo en riesgo. Completa lo urgente y prioriza descanso completo hoy."
        else ->
            "🔴 Estado crítico. Cuerpo y mente requieren recuperación. Evita decisiones importantes. Duerme y aliméntate bien antes de continuar."
    }
}
