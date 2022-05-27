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
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import ch.epfl.sweng.rps.R
import ch.epfl.sweng.rps.models.remote.User
import ch.epfl.sweng.rps.persistence.Cache
import ch.epfl.sweng.rps.services.ServiceLocator
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
                cache.getUserDetails()?.let { user ->
                    setRow(R.id.email_row, "Email", user.email, onTap = null)
                    setRow(R.id.username_row, "Username", user.username) { setValue ->
                        showDialog(initialValue = user.username) { value ->
                            lifecycleScope.launch {
                                val repo = ServiceLocator.getInstance().repository
                                repo.updateUser(User.Field.USERNAME to value)
                                val newUser = repo.getUser(repo.getCurrentUid())
                                cache.setUserDetails(newUser)
                                setValue(newUser?.username)
                            }
                        }
                    }
                }
                profileImage = view.findViewById(R.id.profileImage)
                viewModel.getCachedUserPicture()?.let { profileImage.setImageBitmap(it) }
                viewModel.getProfilePicture()?.let { profileImage.setImageBitmap(it) }
            }
        }
    }

    private fun setRow(
        resource: Int,
        key: String,
        value: String?,
        onTap: ((setValue: (String?) -> Unit) -> Unit)?
    ) {
        requireView().apply {
            val row = findViewById<View>(resource)
            row.findViewById<TextView>(R.id.rowTextViewKey).text = key
            val valueText = row.findViewById<TextView>(R.id.rowTextViewValue)
            valueText.text = value
            val btn = row.findViewById<Button>(R.id.rowEditButton)
            if (onTap != null) {
                btn.setOnClickListener {
                    onTap {
                        valueText.text = it
                    }
                }
            } else {
                btn.visibility = View.GONE
            }
        }
    }

    private fun showDialog(
        title: String = "New value",
        initialValue: String? = "",
        onConfirmed: (String) -> Unit
    ) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)

        val input = EditText(requireContext())
        input.text = Editable.Factory.getInstance().newEditable(initialValue)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton(
            "OK"
        ) { dialog, _ ->
            onConfirmed(input.text.toString())
            dialog.dismiss()
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, _ -> dialog.cancel() }

        builder.show()
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