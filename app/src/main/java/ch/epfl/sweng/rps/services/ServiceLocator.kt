package ch.epfl.sweng.rps.services

import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.remote.Repository

/**
 * This class is used to manage the different repositories.
 *
 * It provides an easy way to switch between local and remote repositories
 * by using different environments.
 */
interface ServiceLocator {

    /**
     * The repository used to communicate with the backend.
     */
    val repository: Repository

    /**
     *  The current environment of the app.
     */
    val env: Env

    /**
     * The matchmaking service.
     */
    val matchmakingService: MatchmakingService

    /**
     * Cached game services.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val cachedGameServices: List<String>

    /**
     * Returns the game service for the given [gameId].
     * If the game service is not cached, it is created and cached.
     */
    fun getGameServiceForGame(gameId: String, start: Boolean = true): FirebaseGameService

    /**
     * Disposes all cached game services.
     */
    fun dispose()

    /**
     * Disposes all cached game services.
     */
    fun disposeAllGameServices()


    companion object {

        private val instances = mutableMapOf<Env, ServiceLocator>()
        private var currentEnv: Env = Env.Prod

        /**
         * Returns the local repository, equivalent to [getInstance] with [Env.Test] then [repository] is [LocalRepository].
         */
        @VisibleForTesting
        val localRepository: LocalRepository
            get() = getInstance(Env.Test).repository as LocalRepository

        /**
         * Returns the current instance of the service locator.
         */
        fun getInstance(env: Env = currentEnv): ServiceLocator {
            return instances.getOrPut(env) {
                when (env) {
                    Env.Prod -> ProdServiceLocator()
                    Env.Test -> TestServiceLocator()
                }
            }
        }

        /**
         * Sets the current environment.
         */
        fun setCurrentEnv(env: Env) {
            currentEnv = env
        }

        /**
         * Returns the current environment.
         */
        fun getCurrentEnv(): Env {
            return currentEnv
        }
    }


}