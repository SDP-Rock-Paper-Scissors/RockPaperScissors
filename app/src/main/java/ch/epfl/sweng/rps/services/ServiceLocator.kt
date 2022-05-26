package ch.epfl.sweng.rps.services

import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import ch.epfl.sweng.rps.remote.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ServiceLocator {

    val repository: Repository
    val env: Env
    val cachedGameServices: List<String>
    val matchmakingService: MatchmakingService
    fun getGameServiceForGame(gameId: String, start: Boolean = true): FirebaseGameService
    fun dispose()
    fun disposeAllGameServices()
    class TestServiceLocator : ServiceLocator {


        override val repository = LocalRepository()
        override val env: Env = Env.Test
        override val cachedGameServices: List<String>
            get() = listOf()
        override val matchmakingService: MatchmakingService
            get() = object : MatchmakingService() {
                override fun queue(gameMode: GameMode): Flow<QueueStatus> = flow { }
                override suspend fun currentGame(): FirebaseGameService =
                    throw IllegalArgumentException()
            }

        override fun dispose() {
        }

        override fun getGameServiceForGame(
            gameId: String,
            start: Boolean
        ): FirebaseGameService {
            TODO("Not yet implemented")
        }

        override fun disposeAllGameServices() {

        }
    }

    companion object {

        private val instances = mutableMapOf<Env, ServiceLocator>()
        private var currentEnv: Env = Env.Prod

        @VisibleForTesting
        val localRepository: LocalRepository
            get() = getInstance(Env.Test).repository as LocalRepository

        fun getInstance(env: Env = currentEnv): ServiceLocator {
            return instances.getOrPut(env) {
                when (env) {
                    Env.Prod -> ProdServiceLocator()
                    Env.Test -> TestServiceLocator()
                }
            }
        }

        fun setCurrentEnv(env: Env) {
            currentEnv = env
        }

        fun getCurrentEnv(): Env {
            return currentEnv
        }
    }


}