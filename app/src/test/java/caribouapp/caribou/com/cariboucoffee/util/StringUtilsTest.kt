package caribouapp.caribou.com.cariboucoffee.util

import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {
    @Test
    fun givenNull_whenIsInteger_thenReturnFalse() {
        assertFalse(StringUtils.isInteger(null))
    }

    @Test
    fun givenCharacter_whenIsInteger_thenReturnFalse() {
        assertFalse(StringUtils.isInteger("a"))
    }

    @Test
    fun givenDouble_whenIsInteger_thenReturnFalse() {
        assertFalse(StringUtils.isInteger("10.3"))
    }

    @Test
    fun givenValidNumber_whenIsInteger_thenReturnTrue() {
        assertTrue(StringUtils.isInteger("10"))
    }

    @Test
    fun givenValidNegativeNumber_whenIsInteger_thenReturnTrue() {
        assertTrue(StringUtils.isInteger("-10"))
    }

    @Test
    fun testEmailMasking_GeneralCase() {
        // given
        val given = "john.doe@gmail.com"
        val expected = "j••••••e@gmail.com"

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_MultipleAts() {
        // given
        val given = "john@jacob@gmail.com"
        val expected = "j••••••••b@gmail.com"

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_DegenerateCase_ShortUsername() {
        // given
        val testCases = listOf(
                Pair("jj@gmail.com", "••@gmail.com"),
                Pair("c@gmail.com", "•@gmail.com")
        )
        for ((given, expected) in testCases) {
            // when
            val masked = StringUtils.maskEmail(given)

            // then
            assertEquals(expected, masked)
            assertEquals(given.length, masked.length)
        }
    }

    @Test
    fun testEmailMasking_DegenerateCase_NoUsername() {
        // given
        val given = "@gmail.com"
        val expected = "@gmail.com"

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_DegenerateCase_NoDomain() {
        // given
        val given = "jack@"
        val expected = "j••k@"

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_DegenerateCase_NonEmailAddress() {
        // given
        val given = "samantha.at.home"
        val expected = "••••••••••••••••"

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_DegenerateCase_EmptyString() {
        // given
        val given = ""
        val expected = ""

        // when
        val masked = StringUtils.maskEmail(given)

        // then
        assertEquals(expected, masked)
        assertEquals(given.length, masked.length)
    }

    @Test
    fun testEmailMasking_DegenerateCase_Null() {
        // given
        val given: String? = null

        // when/then
        assertNull(StringUtils.maskEmail(given))
    }
}
