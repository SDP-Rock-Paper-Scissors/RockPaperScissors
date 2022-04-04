package ch.epfl.sweng.rps.models

enum class Hand(val id: Int) {
    NONE(-1), ROCK(0), PAPER(1), SCISSORS(2), ;

    infix fun vs(other: Hand): Result {
        return when (winner(this, other)) {
            this -> Result.WIN
            other -> Result.LOSS
            null -> Result.TIE
            else -> throw IllegalStateException("Impossible")
        }
    }

    private fun losesTo(hand: Hand): Boolean {
        val loss = when (this) {
            ROCK -> listOf(PAPER)
            PAPER -> listOf(SCISSORS)
            SCISSORS -> listOf(ROCK)
            NONE -> values().filter { it != NONE }
        }
        return loss.contains(hand)
    }

    companion object {
        fun winner(hand1: Hand, hand2: Hand): Hand? {
            if (hand1 == hand2) return null
            if (hand1.losesTo(hand2)) return hand2
            if (hand2.losesTo(hand1)) return hand1
            throw IllegalArgumentException("$hand1 vs $hand2")
        }
    }

    enum class Result {
        WIN, LOSS, TIE
    }
}