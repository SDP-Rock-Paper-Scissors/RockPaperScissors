package ch.epfl.sweng.rps.services

import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.remote.Env
import ch.epfl.sweng.rps.remote.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class TestServiceLocator : ServiceLocator {
    override val repository = LocalRepository()
    override val env: Env = Env.Test
    override val cachedGameServices: List<String>
        get() = listOf()

    override var matchmakingService: MatchmakingService = object : MatchmakingService() {
        override fun queue(gameMode: GameMode): Flow<QueueStatus> = flow { }
        override suspend fun currentGame(): FirebaseGameService =
            throw IllegalArgumentException()
    }

    override fun dispose() {
    }

    private var _gameServiceFn: (gameId: String, start: Boolean) -> FirebaseGameService =
        { _, _ -> throw IllegalArgumentException() }


    fun setGameServiceFn(fn: (gameId: String, start: Boolean) -> FirebaseGameService) {
        _gameServiceFn = fn
    }

    override fun getGameServiceForGame(
        gameId: String,
        start: Boolean
    ): FirebaseGameService = _gameServiceFn(gameId, start)

    override fun disposeAllGameServices() {

    }
}