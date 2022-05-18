package ch.epfl.sweng.rps.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentMatchmakingBinding
import ch.epfl.sweng.rps.models.GameMode
import ch.epfl.sweng.rps.services.FirebaseGameService
import ch.epfl.sweng.rps.services.FirebaseGameService.PlayerCount.Full
import ch.epfl.sweng.rps.services.MatchmakingService
import ch.epfl.sweng.rps.services.ServiceLocator
import ch.epfl.sweng.rps.utils.L
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
        viewLifecycleOwner.lifecycleScope.launch {
            startMatchmaking()
        }
    }

    suspend fun wait() {
        delay(1000)
    }

    private fun write(text: String, level: L.Level = L.Level.WARN) {
        L.of<MatchmakingFragment>().log(text, level = level)
        binding.matchmakingStatusTextview.text = text
    }

    private fun setLoading(loading: Boolean) {
        binding.matchmakingStatusProgressIndicator.visibility =
            if (loading) View.VISIBLE else View.GONE
    }

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

        val game = mm.currentGame()
        try {
            val gameId = if (game != null) {
                joinCurrentGame(mm, game)
            } else {
                queueForNewGame(mm, rounds!!)
            }
            if (gameId == null) {
                write("Error: gameId is null")
                return
            } else {
                MatchmakingFragmentDirections.actionMatchmakingFragmentToGameFragment(gameId)
                    .also { findNavController().navigate(it) }
            }
        } catch (e: TimeoutCancellationException) {
            write("Timed out: ${e.message}")
            setLoading(false)
            displayCancelButton(mm)
        } catch (e: Exception) {
            write("Error: ${e.message}")
            Log.e(null, null, e)
            setLoading(false)
            displayCancelButton(mm)
        }
    }

    private suspend fun queueForNewGame(
        mm: MatchmakingService,
        rounds: Int
    ): String? {
        write("Queueing for new game...")
        wait()

        val timeout = 1000 * 60 * 2L

        val joined = withTimeout(timeout) {
            mm.queue(GameMode.default(rounds)).onEach {
                when (it) {
                    is MatchmakingService.QueueStatus.Queued -> {
                        write("Queued for game ${it.gameMode}")
                        wait()
                    }
                    is MatchmakingService.QueueStatus.GameJoined -> {
                    }
                }
            }.first { it is MatchmakingService.QueueStatus.GameJoined }
        } as MatchmakingService.QueueStatus.GameJoined
        write("Game found: ${joined.gameService.gameId}")
        val gameService = joined.gameService
        gameService.awaitForGame()
        val opponentsNbr =
            { i: Int -> write("Waiting for opponent ($i/${gameService.currentGame.gameMode.playerCount})") }
        wait()
        joined.gameService.opponentCount().onEach {
            opponentsNbr(it.playerCount)
        }.first { it is Full }
        joined.gameService.waitForGameStart()
        return joined.gameService.gameId
    }

    private fun displayCancelButton(mm: MatchmakingService) {
        binding.button.apply {
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
                    binding.button.visibility = View.INVISIBLE
                    binding.button.isEnabled = false
                    binding.button.setOnClickListener { }
                }
            }
        }
    }

    private suspend fun joinCurrentGame(
        mm: MatchmakingService,
        game: FirebaseGameService
    ): String? {
        write("You already have a game, joining...")
        wait()
        write("Waiting for opponent...")
        game.opponentCount().first { it is Full }
        write("Waiting for game to start...")
        game.waitForGameStart()
        wait()
        write("Game started!")
        wait()
        return game.gameId

    }

}