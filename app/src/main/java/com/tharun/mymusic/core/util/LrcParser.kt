package com.tharun.mymusic.core.util

import com.tharun.mymusic.domain.model.Lyrics
import com.tharun.mymusic.domain.model.LyricsLine
import java.util.regex.Pattern

object LrcParser {
    // Matches [mm:ss.xx] or [mm:ss.xxx]
    private val LRC_LINE_REGEX = Pattern.compile("^\\[(\\d{1,2}):(\\d{2})(?:\\.(\\d{2,3}))?\\](.*)$")

    fun parse(songId: String, rawContent: String?): Lyrics {
        if (rawContent.isNullOrBlank()) {
            return Lyrics(songId = songId, plainText = null, lines = emptyList(), isSynced = false)
        }

        val parsedLines = mutableListOf<LyricsLine>()
        val plainLines = mutableListOf<String>()

        val rawLines = rawContent.lines()
        for (line in rawLines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            val matcher = LRC_LINE_REGEX.matcher(trimmed)
            if (matcher.matches()) {
                val minutes = matcher.group(1)?.toLongOrNull() ?: 0L
                val seconds = matcher.group(2)?.toLongOrNull() ?: 0L
                val msGroup = matcher.group(3)
                val millis = when {
                    msGroup == null -> 0L
                    msGroup.length == 2 -> (msGroup.toLongOrNull() ?: 0L) * 10L
                    else -> msGroup.toLongOrNull() ?: 0L
                }
                val text = matcher.group(4)?.trim().orEmpty()
                val totalMs = minutes * 60_000L + seconds * 1_000L + millis
                parsedLines.add(LyricsLine(timestampMs = totalMs, text = text))
                plainLines.add(text)
            } else {
                // Metadata tags like [ti:Title] or plain text lines
                if (!trimmed.startsWith("[") || !trimmed.contains("]")) {
                    plainLines.add(trimmed)
                }
            }
        }

        val sortedLines = parsedLines.sortedBy { it.timestampMs }
        val plainText = if (plainLines.isNotEmpty()) plainLines.joinToString("\n") else rawContent

        return Lyrics(
            songId = songId,
            plainText = plainText,
            lines = sortedLines,
            isSynced = sortedLines.isNotEmpty()
        )
    }

    /**
     * Finds the index of the currently active lyric line for the given playback position in milliseconds.
     * Returns -1 if no lines or before the first line.
     */
    fun findActiveLineIndex(lines: List<LyricsLine>, currentPositionMs: Long): Int {
        if (lines.isEmpty()) return -1
        if (currentPositionMs < lines.first().timestampMs) return 0

        var low = 0
        var high = lines.size - 1
        var activeIndex = 0

        while (low <= high) {
            val mid = (low + high) ushr 1
            if (lines[mid].timestampMs <= currentPositionMs) {
                activeIndex = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return activeIndex
    }
}
