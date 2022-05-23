package ch.epfl.sweng.rps.utils

import android.os.Parcel
import androidx.test.core.os.Parcelables
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.sweng.rps.models.remote.Hand
import ch.epfl.sweng.rps.models.Move
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ParcelTests {

    @Test
    fun testMoveParcelable() {
        val move = Move("player", Hand.PAPER, "gameId")
        val move2 = Parcelables.forceParcel(move, Move.CREATOR)
        assertEquals(move, move2)

        val parcel = Parcel.obtain()
        try {
            move.writeToParcel(parcel, 0)
            val marshalled = parcel.marshall()
            assertTrue { marshalled.isNotEmpty() }
        } finally {
            parcel.recycle()
        }
    }

    @Test
    fun testMoveParcelableWithNulls() {
        val move = Move(null, null, null)
        val move2 = Parcelables.forceParcel(move, Move.CREATOR)
        assertEquals(move, move2)
    }

    @Test
    fun testMoveParcelsViaArray() {
        val move = Move("player", Hand.PAPER, "gameId")
        val parcel = Parcel.obtain()
        val parcel2 = Parcel.obtain()
        try {
            move.writeToParcel(parcel, 0)
            parcel.setDataPosition(0)
            parcel2.unmarshall(parcel.marshall(), 0, parcel.dataSize())
            val move2 = Move.CREATOR.createFromParcel(parcel)
            assertEquals(move, move2)
        } finally {
            parcel.recycle()
            parcel2.recycle()
        }
    }
}