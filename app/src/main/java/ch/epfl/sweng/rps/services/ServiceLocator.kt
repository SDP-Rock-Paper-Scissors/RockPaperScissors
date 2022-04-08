package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.db.Repository

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
    }

    val repository: Repository

    fun dispose()

    val env: Env

    fun disposeAllGameServices()

    val cachedGameServices: List<String>

    class TestServiceLocator : ServiceLocator {

        override val repository = LocalRepository()

        override fun dispose() {
        }

        override val env: Env = Env.Test

        fun getGameServiceForGame(gameId: String, start: Boolean = true): FirebaseGameService {
            TODO("Not yet implemented")
        }

        override fun disposeAllGameServices() {

        }

        override val cachedGameServices: List<String>
            get() = listOf()
    }
}