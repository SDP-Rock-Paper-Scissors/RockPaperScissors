package ch.epfl.sweng.rps.models

data class Game(
    val uid: String,
    val games: List<Game>,
    val players: List<String>,
    val mode: Mode
) {
    data class Mode(val players: Int, val type: Type, val time: Int?) {
        enum class Type {
            local, online, pc
        }
    }
}
