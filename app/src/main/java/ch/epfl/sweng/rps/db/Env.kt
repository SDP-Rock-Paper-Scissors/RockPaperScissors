package ch.epfl.sweng.rps.db

/**
 * Env is a class that contains the environment variables used by the application.
 */
enum class Env(
    /**
     * Not really used in the application, but used by the tests.
     */
    val value: String
) {
    /**
     * The production environment.
     */
    Prod("prod"),

    /**
     * The test environment.
     */
    Test("test"),
}