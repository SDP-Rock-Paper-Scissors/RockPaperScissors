package ch.epfl.sweng.rps.db.implementations

import ch.epfl.sweng.rps.db.FirebaseHelper
import ch.epfl.sweng.rps.models.User
import org.junit.Assert.assertEquals
import org.junit.Test


internal class FirebaseHelperTest {
    @Test
    fun testProcessUserArgs() {
        val name = "Hamasaki"
        assertEquals(
            hashMapOf(
                User.Field.gamesHistoryPublic.value to true,
                User.Field.username.value to name,
            ),
            FirebaseHelper.processUserArguments(
                User.Field.gamesHistoryPublic to true,
                User.Field.username to name
            )
        )
    }
}