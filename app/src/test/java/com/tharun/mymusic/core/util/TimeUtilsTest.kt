package com.tharun.mymusic.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {

    @Test
    fun `formatDurationMs correctly formats milliseconds to mm ss`() {
        assertEquals("0:00", TimeUtils.formatDurationMs(0L))
        assertEquals("0:05", TimeUtils.formatDurationMs(5000L))
        assertEquals("1:00", TimeUtils.formatDurationMs(60000L))
        assertEquals("3:45", TimeUtils.formatDurationMs(225000L))
        assertEquals("10:12", TimeUtils.formatDurationMs(612000L))
    }

    @Test
    fun `parseDurationIso parses ISO 8601 strings accurately`() {
        assertEquals(225000L, TimeUtils.parseDurationIso("PT3M45S"))
        assertEquals(60000L, TimeUtils.parseDurationIso("PT1M"))
        assertEquals(30000L, TimeUtils.parseDurationIso("PT30S"))
        assertEquals(3661000L, TimeUtils.parseDurationIso("PT1H1M1S"))
        assertEquals(0L, TimeUtils.parseDurationIso(""))
        assertEquals(0L, TimeUtils.parseDurationIso(null))
    }
}
