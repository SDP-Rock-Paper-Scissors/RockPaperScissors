package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository

/*
* ServiceLocator is a singleton class that provides access to the different services
* Services: FirebaseRepository, LocalRepository
* */
class ServiceLocator(private val env: Env) {
    companion object {
        private val instances = mutableMapOf<Env, ServiceLocator>()

        fun getInstance(env: Env = Env.Prod): ServiceLocator {
            return instances.getOrPut(env) { ServiceLocator(env) }
        }
    }

    fun currentEnv() = env

    private lateinit var firebaseReferences: FirebaseReferences
    fun getFirebaseReferences(): FirebaseReferences {
        if (!::firebaseReferences.isInitialized) {
            firebaseReferences = FirebaseReferences(env)
        }
        return firebaseReferences
    }


    private lateinit var firebaseRepository: FirebaseRepository
    fun getFirebaseRepository(): FirebaseRepository {
        if (!::firebaseRepository.isInitialized) {
            firebaseRepository = FirebaseRepository(getFirebaseReferences())
        }
        return firebaseRepository
    }

    private val gameServices = mutableMapOf<String, FirebaseGameService>()

    fun getGameServiceForGame(gameId: String, start: Boolean = true): FirebaseGameService {
        cleanUpServices()
        val service = gameServices.getOrPut(gameId) {
            FirebaseGameService(
                firebase = getFirebaseReferences(),
                firebaseRepository = getFirebaseRepository(),
                gameId = gameId
            )

        }
        if (start) {
            service.startListening()
        }
        return service
    }

    private fun cleanUpServices() {
        for (gameService in gameServices) {
            if (gameService.value.isGameOver) {
                gameService.value.dispose()
                gameServices.remove(gameService.key)
            }
        }
    }

    fun disposeAllGameServices() {
        for (gameService in gameServices.values) {
            if (!gameService.isDisposed) gameService.dispose()
        }
        gameServices.clear()
    }


    val cachedGameServices: List<String>
        get() {
            return gameServices.keys.toList()
        }
}