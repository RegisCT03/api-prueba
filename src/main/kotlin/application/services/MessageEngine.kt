package com.MindStack.application.services

object MessageEngine {

    private const val ROL_TRABAJADOR        = 1
    private const val ROL_ESTUDIANTE        = 2
    private const val ROL_ESTUDIA_Y_TRABAJA = 3

    data class MessageResult(
        val prefix: String,
        val body: String,
        val batteryRange: String
    ) {
        val full: String get() = "$prefix: $body"
    }

    fun getMessage(
        idRol: Int?,
        semaphoreColor: String,
        batteryLevel: Int
    ): MessageResult {
        val range = batteryRange(batteryLevel)
        return when {

            idRol == ROL_TRABAJADOR && semaphoreColor == "GREEN" -> MessageResult(
                prefix       = "Óptimo",
                body         = "¡Día ideal para trabajo profundo! Aprovecha para reuniones estratégicas, planificación o tareas que requieran alta concentración.",
                batteryRange = range
            )
            idRol == ROL_TRABAJADOR && semaphoreColor == "YELLOW" -> MessageResult(
                prefix       = "Precaución",
                body         = "Tu energía va en descenso. Prioriza tareas rutinarias (responder correos, llenar reportes) y evita tomar decisiones críticas hoy.",
                batteryRange = range
            )
            idRol == ROL_TRABAJADOR && semaphoreColor == "RED" -> MessageResult(
                prefix       = "Crítico",
                body         = "Riesgo de errores laborales. Enfócate solo en lo urgente, haz pausas activas frecuentes y desconéctate por completo al terminar tu turno.",
                batteryRange = range
            )

            idRol == ROL_ESTUDIANTE && semaphoreColor == "GREEN" -> MessageResult(
                prefix       = "Óptimo",
                body         = "¡Energía a tope! Es el momento perfecto para abordar sesiones intensas y problemas complejos, como cálculo multivariable o álgebra lineal.",
                batteryRange = range
            )
            idRol == ROL_ESTUDIANTE && semaphoreColor == "YELLOW" -> MessageResult(
                prefix       = "Precaución",
                body         = "Tu capacidad de retención está bajando. Dedícate a repasar notas, organizar apuntes o leer, y deja los ejercicios difíciles para después.",
                batteryRange = range
            )
            idRol == ROL_ESTUDIANTE && semaphoreColor == "RED" -> MessageResult(
                prefix       = "Crítico",
                body         = "Estudiar así es contraproducente por la neblina mental. Cierra los libros; tu cerebro necesita dormir para consolidar la memoria de lo que ya estudiaste.",
                batteryRange = range
            )

            idRol == ROL_ESTUDIA_Y_TRABAJA && semaphoreColor == "GREEN" -> MessageResult(
                prefix       = "Óptimo",
                body         = "¡Tienes el combustible necesario para tu doble jornada! Aprovecha para adelantar el proyecto más pesado que tengas, ya sea académico o laboral.",
                batteryRange = range
            )
            idRol == ROL_ESTUDIA_Y_TRABAJA && semaphoreColor == "YELLOW" -> MessageResult(
                prefix       = "Precaución",
                body         = "El cansancio se acumula rápido con tu rutina. Negocia tus energías: elige solo una prioridad hoy (la escuela o el trabajo) y enfócate en sobrevivir el resto.",
                batteryRange = range
            )
            idRol == ROL_ESTUDIA_Y_TRABAJA && semaphoreColor == "RED" -> MessageResult(
                prefix       = "Crítico",
                body         = "Alerta de Burnout. Estás operando con reservas de emergencia. No te quedes despierto de madrugada haciendo tareas; prioriza descansar o tu salud colapsará.",
                batteryRange = range
            )

            else -> genericMessage(semaphoreColor, range)
        }
    }

    private fun genericMessage(semaphoreColor: String, range: String): MessageResult = when (semaphoreColor) {
        "GREEN"  -> MessageResult(
            prefix       = "Óptimo",
            body         = "Estás en tu mejor momento. Ideal para tareas cognitivamente exigentes: estudio, análisis o decisiones importantes.",
            batteryRange = range
        )
        "YELLOW" -> MessageResult(
            prefix       = "Precaución",
            body         = "Energía moderada. Puedes trabajar en tareas rutinarias. Evita decisiones de alto impacto y toma descansos cortos de 20 minutos.",
            batteryRange = range
        )
        else     -> MessageResult(
            prefix       = "Crítico",
            body         = "Tu cuerpo y mente necesitan recuperación urgente. Prioriza descansar antes de cualquier actividad que requiera concentración.",
            batteryRange = range
        )
    }

    private fun batteryRange(level: Int): String = when {
        level >= 71 -> "71% - 100%"
        level >= 31 -> "31% - 70%"
        else        -> "0% - 30%"
    }
}
