package ch.epfl.sweng.rps.models

enum class Hand(val id: Int) {
    NONE(-1), ROCK(0), PAPER(1), SCISSORS(2);

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
            return if (hand1.losesTo(hand2))
                hand2
            else
                hand1
        }

        internal fun checkEveryHandLosesTo(): Boolean {
            for (h1 in values()) {
                for (h2 in values()) {
                    if (h1 != h2 && !h1.losesTo(h2) && !h2.losesTo(h1)) {
                        return false
                    }
                }
            }
            return true
        }
    }

    enum class Result {
        WIN, LOSS, TIE
    }
}