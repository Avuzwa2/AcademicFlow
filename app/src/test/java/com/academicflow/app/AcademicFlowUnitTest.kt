package com.academicflow.app

import com.academicflow.app.util.DateUtils
import com.academicflow.app.util.PasswordHasher
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class AcademicFlowUnitTest {

    @Test
    fun testPasswordHasher_hashAndVerify() {
        val rawPassword = "securePassword123"
        val hash = PasswordHasher.hash(rawPassword)

        assertNotNull(hash)
        assertNotEquals(rawPassword, hash)
        assertTrue(PasswordHasher.verify(rawPassword, hash))
        assertFalse(PasswordHasher.verify("wrongPassword", hash))
    }

    @Test
    fun testDateUtils_getStartOfWeek() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 5, 14, 30, 0) // Wednesday
        }
        val startOfWeek = DateUtils.getStartOfWeek(calendar.timeInMillis)

        val startCal = Calendar.getInstance().apply { timeInMillis = startOfWeek }
        assertEquals(Calendar.MONDAY, startCal.get(Calendar.DAY_OF_WEEK))
        assertEquals(0, startCal.get(Calendar.HOUR_OF_DAY))
        assertEquals(0, startCal.get(Calendar.MINUTE))
    }

    @Test
    fun testDateUtils_formatDate() {
        val calendar = Calendar.getInstance().apply {
            set(2026, Calendar.AUGUST, 5, 12, 0, 0)
        }
        val formatted = DateUtils.formatDate(calendar.timeInMillis)
        assertTrue(formatted.contains("Aug") || formatted.contains("08"))
    }
}