package ch.epfl.sweng.rps.ui.camera

import android.Manifest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.databinding.FragmentCameraBinding



class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        // returns boolean representind whether the
        // permission is granted or not
        if (isGranted) {
            // permission granted continue the normal workflow of app
            Log.i("DEBUG", "permission granted")
        } else {
            navigateBackHome()
            // if permission denied then check whether never ask
            // again is selected or not by making use of
            // !ActivityCompat.shouldShowRequestPermissionRationale(
            // requireActivity(), Manifest.permission.CAMERA)
            Log.i("DEBUG", "permission denied")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {


        _binding = FragmentCameraBinding.inflate(inflater, container, false);
        val root: View = binding.root

        requestPermission.launch(Manifest.permission.CAMERA)


        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateBackHome() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main).navigate(
                CameraFragmentDirections.actionCameraFragmentToNavHome())
        }
    }
}