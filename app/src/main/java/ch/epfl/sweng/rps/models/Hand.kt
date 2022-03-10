package ch.epfl.sweng.rps.models

enum class Hand(val id: Int) {
    ROCK(0), PAPER(1), SCISSORS(2);

    infix fun vs(other: Hand): Hand {
        val beats: Map<Hand, Hand> = hashMapOf(
            ROCK to SCISSORS,
            SCISSORS to PAPER,
            PAPER to ROCK
        )
        return if (beats[this] == other) this
        else other
    }
}