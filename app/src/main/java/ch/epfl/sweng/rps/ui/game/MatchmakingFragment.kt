package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentMatchmakingBinding
import ch.epfl.sweng.rps.models.remote.GameMode
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.FirebaseGameService.PlayerCount.Full
import ch.epfl.sweng.rps.services.MatchmakingService
import ch.epfl.sweng.rps.services.MatchmakingService.QueueStatus
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.L
import ch.epfl.sweng.rps.utils.TEST_MODE
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class MatchmakingFragment : Fragment() {

    private lateinit var viewModel: MatchmakingViewModel
    private lateinit var binding: FragmentMatchmakingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatchmakingBinding.inflate(inflater, container, false)
        write("Loading...")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MatchmakingViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        viewLifecycleOwner.lifecycleScope.launch { startMatchmaking() }
    }

    private suspend fun wait() {
        delay(1000)
    }

    private fun write(text: String, level: L.Level = L.Level.WARN) {
        L.of(this).log(text, level = level)
        binding.matchmakingStatusTextview.text = text
    }

    private fun setLoading(loading: Boolean) {
        binding.matchmakingStatusProgressIndicator.visibility =
            if (loading) View.VISIBLE else View.GONE
    }

    private fun isTest(): Boolean = requireActivity().intent.getBooleanExtra(TEST_MODE, false)

    private suspend fun startMatchmaking() {
        val mm = ServiceLocator.getInstance().matchmakingService
        var rounds = arguments?.getInt("rounds")
        when {
            rounds == null -> {
                write("Error: rounds is null")
                return
            }

            rounds < 1 -> {
                write(
                    "WARNING: rounds is less than 1 ($rounds), assuming 1."
                )
                rounds = 1
                wait()
            }
        }

        try {
            val game = mm.currentGame()
            val gameId = if (game != null) {
                joinCurrentGame(mm, game)
            } else {
                queueForNewGame(mm, rounds!!)
            }
            write(getString(R.string.ready_to_play))
            if (!isTest())
                findNavController().navigate(
                    MatchmakingFragmentDirections.actionMatchmakingFragmentToGameFragment(
                        gameId
                    )
                )
        } catch (e: Exception) {
            displayCancelButton(mm)
            write(exceptionToString(e))
            setLoading(false)
        }
    }

    companion object {
        fun exceptionToString(e: Exception): String {
            return when (e) {
                is MatchmakingTimeoutException -> return e.message
                is TimeoutCancellationException -> "Timed out: ${e.message}"
                else -> "Error: ${e.message}"
            }
        }
    }

    class MatchmakingTimeoutException(
        val action: String,
        private val timeout: Long?,
        throwable: Throwable? = null
    ) : Exception(throwable) {
        override val message: String
            get() = "Timed out waiting for $action after $timeout ms"
    }

    private suspend fun <T> timeout(timeout: Long, action: String, block: suspend () -> T): T {
        try {
            return withTimeout(timeout) { block() }
        } catch (e: TimeoutCancellationException) {
            throw MatchmakingTimeoutException(action, timeout, e)
        }
    }

    private val twoMinutes = 2 * 60 * 1000L
    val thirtySeconds = 30 * 1000L

    @Throws(MatchmakingTimeoutException::class)
    private suspend fun queueForNewGame(
        mm: MatchmakingService,
        rounds: Int
    ): String {
        write("Queueing for new game...")
        wait()
        val joined = timeout(twoMinutes, "queuing for new game") {
            mm.queue(GameMode.default(rounds)).onEach {
                when (it) {
                    is QueueStatus.Queued -> {
                        write("Queued for game ${it.gameMode}")
                        wait()
                    }

                    is QueueStatus.GameJoined -> {
                    }
                }
            }.first { it is QueueStatus.GameJoined }
        } as QueueStatus.GameJoined
        write("Game found: ${joined.gameService.gameId}")
        val gameService = joined.gameService
        timeout(twoMinutes, "getting the game") {
            gameService.refreshGame()
        }
        val opponentsNbr =
            createDisplayOpponentsFunction(gameService.currentGame.gameMode.playerCount)
        wait()
        timeout(twoMinutes, "waiting for all opponents") {
            joined.gameService.opponentCount().onEach {
                opponentsNbr(it.playerCount)
            }.first { it is Full }
        }
        timeout(twoMinutes, "wait for game to start") {
            joined.gameService.waitForGameStart()
        }
        return joined.gameService.gameId
    }


    private fun createDisplayOpponentsFunction(total: Int): (Int) -> Unit =
        { i: Int -> write("Waiting for opponent ($i/${total})") }

    @Throws(MatchmakingTimeoutException::class)
    private suspend fun joinCurrentGame(
        mm: MatchmakingService,
        game: FirebaseGameService
    ): String {
        write("You already have a game, joining...")
        wait()
        write("Waiting for opponent...")
        val showOpponents = createDisplayOpponentsFunction(game.currentGame.gameMode.playerCount)
        timeout(twoMinutes, "waiting for all opponents") {
            game.opponentCount()
                .onEach { showOpponents(it.playerCount) }
                .first { it is Full }
        }
        write("Waiting for game to start...")
        timeout(twoMinutes, "wait for game to start") {
            game.waitForGameStart()
        }
        return game.gameId
    }

    private fun displayCancelButton(mm: MatchmakingService) {
        binding.matchmakingCancelButton.apply {
            isEnabled = true
            text = getString(R.string.cancel_text)
            visibility = View.VISIBLE
            setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    setLoading(true)
                    write("Cancelling...")
                    mm.cancelQueue()
                    write("Cancelled matchmaking")
                    setLoading(false)
                    binding.matchmakingCancelButton.apply {
                        visibility = View.INVISIBLE
                        isEnabled = false
                        setOnClickListener { }
                    }
                }
            }
        }
    }

}