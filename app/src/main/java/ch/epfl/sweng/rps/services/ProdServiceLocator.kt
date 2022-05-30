package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.db.Env
import ch.epfl.sweng.rps.db.FirebaseReferences
import ch.epfl.sweng.rps.db.FirebaseRepository

/**
 * ServiceLocator is a singleton class that provides access to the different services
 *
 * Repositories: [FirebaseRepository], [LocalRepository]
 *
 * Services: [FirebaseGameService]
 * */
class ProdServiceLocator : ServiceLocator {

    val firebaseReferences by lazy { FirebaseReferences() }

    override val repository by lazy { FirebaseRepository.createInstance(firebaseReferences) }

    override fun dispose() {
        disposeAllGameServices()
    }

    override val env: Env = Env.Prod

    private val gameServices = mutableMapOf<String, FirebaseGameService>()

    override fun getGameServiceForGame(gameId: String, start: Boolean): FirebaseGameService {
        cleanUpServices()
        val service = gameServices.getOrPut(gameId) {
            FirebaseGameService(
                firebase = firebaseReferences,
                firebaseRepository = repository,
                gameId = gameId
            )
        }
        if (start) {
            service.startListening()
        }
        return service
    }

    private fun cleanUpServices() {
        for (e in gameServices) {
            if (e.value.isGameOver) {
                if (!e.value.isDisposed) e.value.dispose()
                gameServices.remove(e.key)
            }
        }
    }

    override fun disposeAllGameServices() {
        for (gameService in gameServices.values) {
            if (!gameService.isDisposed) gameService.dispose()
        }
        gameServices.clear()
    }


    override val cachedGameServices: List<String>
        get() = gameServices.keys.toList()

    override val matchmakingService: MatchmakingService by lazy { MatchmakingService() }


}