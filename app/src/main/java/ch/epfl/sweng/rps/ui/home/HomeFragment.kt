package ch.epfl.sweng.rps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setNavigationOnButton(_binding!!.buttonPlay1GamesOffline, R.id.gameFragment)
        setNavigationOnButton(_binding!!.buttonActivateCamera, R.id.cameraXLivePreviewActivity)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setNavigationOnButton(buttonToBind: Button, fragmentID: Int){
        buttonToBind.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(fragmentID)
        }
    }
}