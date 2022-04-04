package ch.epfl.sweng.rps.db

enum class Env {
    DEV,
    PROD;

    override fun toString(): String {
        return when (this) {
            DEV -> "dev"
            PROD -> "prod"
        }
    }
}