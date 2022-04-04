package ch.epfl.sweng.rps.ui.camera

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ch.epfl.sweng.rps.databinding.FragmentCameraBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class CameraFragment : Fragment() {

    private var _binding: FragmentCameraBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {


        _binding = FragmentCameraBinding.inflate(inflater, container, false);
        val root: View = binding.root

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        return root
    }

    private fun startCamera() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}