package ch.epfl.sweng.rps.models

import android.os.Parcel
import android.os.Parcelable

data class Move(val playerUid: String?, val hand: Hand?, val gameId: String?) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(playerUid)
        parcel.writeString(hand?.name)
        parcel.writeString(gameId)
    }

    companion object CREATOR : Parcelable.Creator<Move> {
        override fun createFromParcel(parcel: Parcel): Move {
            val playerUid = parcel.readString()
            val hand = parcel.readString()?.let { Hand.valueOf(it) }
            val gameId = parcel.readString()
            return Move(
                playerUid = playerUid,
                gameId = gameId,
                hand = hand
            )
        }

        override fun newArray(size: Int): Array<Move?> {
            return arrayOfNulls(size)
        }
    }

}
