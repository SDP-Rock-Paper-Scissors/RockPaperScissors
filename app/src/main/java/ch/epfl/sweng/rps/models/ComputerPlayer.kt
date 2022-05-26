package ch.epfl.sweng.rps.models

abstract class ComputerPlayer(
    override val username: String? = "",
    override val uid: String,
    override val games_history_privacy: String = "",
    override val has_profile_photo: Boolean = false,
) : AbstractUser() {
    /**
     * @return one of the possible moves (represented by a class e.g. Hand)
     */
    abstract fun makeMove(): Hand
}