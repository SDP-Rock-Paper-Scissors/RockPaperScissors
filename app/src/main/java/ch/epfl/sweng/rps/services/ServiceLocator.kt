package ch.epfl.sweng.rps.services

import androidx.annotation.VisibleForTesting
import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.db.Repository
import ch.epfl.sweng.rps.models.remote.GameMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface ServiceLocator {

    companion object {
        private val instances = mutableMapOf<Env, ServiceLocator>()

        private var currentEnv: Env = Env.Prod

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

        @VisibleForTesting
        val localRepository: LocalRepository
            get() = getInstance(Env.Test).repository as LocalRepository
    }

    fun getGameServiceForGame(gameId: String, start: Boolean = true): FirebaseGameService

    val repository: Repository

    fun dispose()

    val env: Env

    fun disposeAllGameServices()

    val cachedGameServices: List<String>

    val matchmakingService: MatchmakingService

    class TestServiceLocator : ServiceLocator {


        override val repository = LocalRepository()

        override fun dispose() {
        }

        override val env: Env = Env.Test

        override fun getGameServiceForGame(
            gameId: String,
            start: Boolean
        ): FirebaseGameService {
            TODO("Not yet implemented")
        }

        override fun disposeAllGameServices() {

        }

        override val cachedGameServices: List<String>
            get() = listOf()

        override val matchmakingService: MatchmakingService
            get() = object : MatchmakingService() {
                override fun queue(gameMode: GameMode): Flow<QueueStatus> = flow { }
                override suspend fun currentGame(): FirebaseGameService? =
                    throw IllegalArgumentException()
            }
    }


}