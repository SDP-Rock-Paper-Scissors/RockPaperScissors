package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.LocalRepository
import ch.epfl.sweng.rps.db.Repository

interface ServiceLocator {


    companion object {
        private val instances = mutableMapOf<Env, ServiceLocator>()

        fun getInstance(env: Env = Env.Prod): ServiceLocator {
            return instances.getOrPut(env) {
                when (env) {
                    Env.Prod -> ProdServiceLocator()
                    Env.Test -> TestServiceLocator()
                }
            }
        }

        fun getProdInstance(): ProdServiceLocator {
            return getInstance(Env.Prod) as ProdServiceLocator
        }
    }

    val repository: Repository

    fun dispose()

    val env: Env

    fun disposeAllGameServices()

    val cachedGameServices: List<String>

    class TestServiceLocator : ServiceLocator {

        override val repository: LocalRepository = LocalRepository()

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