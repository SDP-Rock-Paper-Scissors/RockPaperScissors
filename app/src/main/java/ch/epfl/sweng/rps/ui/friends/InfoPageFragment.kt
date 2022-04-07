package ch.epfl.sweng.rps.ui.friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import ch.epfl.sweng.rps.R


class InfoPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_page_, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val onlineImage = view.findViewById<ImageView>(R.id.onlineImage_infoPage)
        val offlineImage = view.findViewById<ImageView>(R.id.offlineImage_infoPage)
        val username = view.findViewById<TextView>(R.id.userName_infoPage)
        val gamesPlayed = view.findViewById<TextView>(R.id.gamesPlayedText_infoPage)
        val gamesWon = view.findViewById<TextView>(R.id.gamesWonText_infoPage)
        val winRate = view.findViewById<TextView>(R.id.winRateText_infoPage)
        val isOnline = InfoPageFragmentArgs.fromBundle(requireArguments()).isOnline

        onlineImage.visibility = if(isOnline) View.VISIBLE else View.INVISIBLE
        offlineImage.visibility = if(isOnline) View.INVISIBLE else View.VISIBLE

        username.text = InfoPageFragmentArgs.fromBundle(requireArguments()).username
        gamesPlayed.text = InfoPageFragmentArgs.fromBundle(requireArguments()).gamesPlayedText
        gamesWon.text = InfoPageFragmentArgs.fromBundle(requireArguments()).gamesWonText
        winRate.text = InfoPageFragmentArgs.fromBundle(requireArguments()).winRateText

        val backButton = view.findViewById<ImageButton>(R.id.infoPage_backButton)
        val playButton = view.findViewById<ImageButton>(R.id.infoPage_playButton)

        backButton.setOnClickListener {
            findNavController().navigate(InfoPageFragmentDirections.actionInfoPageFragmentToNavFriends2())
        }

        playButton.setOnClickListener {
            findNavController().navigate(InfoPageFragmentDirections.actionInfoPageFragmentToGameFragment2())
        }

    }
}