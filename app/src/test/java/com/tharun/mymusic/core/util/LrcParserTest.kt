package com.tharun.mymusic.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LrcParserTest {

    @Test
    fun `parse valid LRC string extracts timestamps and text accurately`() {
        val lrc = """
            [ti:Test Title]
            [00:05.50]First line of song
            [00:15.000]Second line of song
            [01:02.25]Third line in next minute
        """.trimIndent()

        val lyrics = LrcParser.parse("test_song", lrc)

        assertTrue(lyrics.isSynced)
        assertEquals(3, lyrics.lines.size)

        assertEquals(5500L, lyrics.lines[0].timestampMs)
        assertEquals("First line of song", lyrics.lines[0].text)

        assertEquals(15000L, lyrics.lines[1].timestampMs)
        assertEquals("Second line of song", lyrics.lines[1].text)

        assertEquals(62250L, lyrics.lines[2].timestampMs)
        assertEquals("Third line in next minute", lyrics.lines[2].text)
    }

    @Test
    fun `parse empty or null returns unsynced empty lyrics`() {
        val emptyLyrics = LrcParser.parse("empty", "")
        assertFalse(emptyLyrics.isSynced)
        assertTrue(emptyLyrics.lines.isEmpty())

        val nullLyrics = LrcParser.parse("null", null)
        assertFalse(nullLyrics.isSynced)
        assertTrue(nullLyrics.lines.isEmpty())
    }

    @Test
    fun `findActiveLineIndex locates correct line binary search`() {
        val lrc = """
            [00:05.00]Line 1
            [00:10.00]Line 2
            [00:20.00]Line 3
            [00:30.00]Line 4
        """.trimIndent()

        val lyrics = LrcParser.parse("song", lrc)

        assertEquals(0, LrcParser.findActiveLineIndex(lyrics.lines, 0L))
        assertEquals(0, LrcParser.findActiveLineIndex(lyrics.lines, 6000L))
        assertEquals(1, LrcParser.findActiveLineIndex(lyrics.lines, 12000L))
        assertEquals(2, LrcParser.findActiveLineIndex(lyrics.lines, 25000L))
        assertEquals(3, LrcParser.findActiveLineIndex(lyrics.lines, 35000L))
    }
}
