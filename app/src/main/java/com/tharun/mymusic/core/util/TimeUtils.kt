package com.tharun.mymusic.core.util

import java.util.Locale
import java.util.concurrent.TimeUnit

object TimeUtils {
    fun formatDurationMs(durationMs: Long): String {
        if (durationMs <= 0) return "0:00"
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }

    fun parseDurationIso(isoDuration: String?): Long {
        if (isoDuration.isNullOrBlank()) return 0L
        return try {
            // Parses ISO 8601 duration e.g. PT3M45S
            var temp = isoDuration.removePrefix("PT")
            var hours = 0L
            var minutes = 0L
            var seconds = 0L

            if (temp.contains("H")) {
                val parts = temp.split("H")
                hours = parts[0].toLongOrNull() ?: 0L
                temp = if (parts.size > 1) parts[1] else ""
            }
            if (temp.contains("M")) {
                val parts = temp.split("M")
                minutes = parts[0].toLongOrNull() ?: 0L
                temp = if (parts.size > 1) parts[1] else ""
            }
            if (temp.contains("S")) {
                val parts = temp.split("S")
                seconds = parts[0].toLongOrNull() ?: 0L
            }
            TimeUnit.HOURS.toMillis(hours) +
                    TimeUnit.MINUTES.toMillis(minutes) +
                    TimeUnit.SECONDS.toMillis(seconds)
        } catch (_: Exception) {
            0L
        }
    }
}
