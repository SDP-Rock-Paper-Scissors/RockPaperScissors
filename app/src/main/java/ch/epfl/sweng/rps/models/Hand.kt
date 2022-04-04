package ch.epfl.sweng.rps.models

enum class Hand(val id: Int) {
    ROCK(0), PAPER(1), SCISSORS(2);

    infix fun vs(other: Hand): Result {
        if (this == other) return Result.DRAW
        return when (winner(this, other)) {
            this -> Result.WIN
            other -> Result.LOSE
            else -> throw IllegalStateException("Impossible")
        }
    }

    companion object {
        fun winner(hand1: Hand, hand2: Hand): Hand {
            val beats: Map<Hand, Hand> = hashMapOf(
                ROCK to SCISSORS,
                SCISSORS to PAPER,
                PAPER to ROCK
            )
            return if (beats[hand1] == hand2) hand1
            else hand2
        }
    }

    enum class Result {
        WIN, LOSE, DRAW
    }
}