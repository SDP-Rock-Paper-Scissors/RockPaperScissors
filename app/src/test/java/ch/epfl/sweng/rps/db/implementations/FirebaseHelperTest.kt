package ch.epfl.sweng.rps.db.implementations

import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.models.User
import org.junit.Assert.assertEquals
import org.junit.Test


internal class FirebaseHelperTest {
    @Test
    fun testProcessUserArgs() {
        val name = "Rei"
        assertEquals(
            hashMapOf(
                User.Field.GAMES_HISTORY_PRIVACY.value to true,
                User.Field.USERNAME.value to name,
            ),
            FirebaseHelper.processUserArguments(
                User.Field.GAMES_HISTORY_PRIVACY to true,
                User.Field.USERNAME to name
            )
        )
    }
}