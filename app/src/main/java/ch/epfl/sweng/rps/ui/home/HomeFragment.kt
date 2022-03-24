package ch.epfl.sweng.rps.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.buttonPlayOneOfflineGame.setOnClickListener { view: View ->
            Navigation.findNavController(view).navigate(R.id.gameFragment)
        }

        return binding.root
    }
//    seems not needed!!!!ASK SILVIO!!!!
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
}