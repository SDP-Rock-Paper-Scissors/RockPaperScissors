package ch.epfl.sweng.rps.ui.profile

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.ui.settings.SettingsActivity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var viewModel: ProfileViewModel
    private lateinit var profileImage: ImageView

    init {
        lifecycleScope.launch {
            whenStarted {
                val view = requireView()
                val cache = Cache.getInstance()
                cache.getUserDetails()?.let {
                    view.findViewById<TextView>(R.id.TextEmail).text = it.email
                    view.findViewById<TextView>(R.id.TextDisplayName).text = it.username
                    view.findViewById<TextView>(R.id.TextPrivacy).text =
                        it.games_history_privacy
                }
                profileImage = view.findViewById(R.id.profileImage)
                viewModel.getCachedUserPicture()?.let { profileImage.setImageBitmap(it) }
                viewModel.getProfilePicture()?.let { profileImage.setImageBitmap(it) }
            }
            // This line runs only after the whenStarted block above has completed.
        }
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                val uri: Uri? = res.data?.data
                if (uri == null) return@registerForActivityResult
                val bitmap: Bitmap = getBitmap(requireContext().contentResolver, uri)!!
                viewModel.updateProfilePicture(bitmap)
                profileImage.setImageBitmap(bitmap)
            }
        }

    private fun getBitmap(contentResolver: ContentResolver, fileUri: Uri?): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, fileUri!!))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(contentResolver, fileUri)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = inflater.inflate(R.layout.profile_fragment, container, false)
        val button: Button = view.findViewById(R.id.editProfilePic)
        button.setOnClickListener { getPicture() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<MaterialToolbar>(R.id.profile_top_toolbar)
            .setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.profile_appbar_settings_btn -> {
                        val intent = Intent(activity, SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
    }


    private fun getPicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        resultLauncher.launch(intent)
    }
}