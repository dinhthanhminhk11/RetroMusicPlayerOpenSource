package code.name.monkey.retromusic.fragments.other

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import code.name.monkey.retromusic.BASE_URL_IMAGE
import code.name.monkey.retromusic.Constants.USER_BANNER
import code.name.monkey.retromusic.Constants.USER_PROFILE
import code.name.monkey.retromusic.R
import code.name.monkey.retromusic.TOKEN_USER
import code.name.monkey.retromusic.databinding.FragmentUserInfoBinding
import code.name.monkey.retromusic.extensions.accentColor
import code.name.monkey.retromusic.extensions.applyToolbar
import code.name.monkey.retromusic.extensions.findNavControllerOpen
import code.name.monkey.retromusic.extensions.loadImage
import code.name.monkey.retromusic.extensions.showToast
import code.name.monkey.retromusic.fragments.LibraryViewModel
import code.name.monkey.retromusic.glide.RetroGlideExtension
import code.name.monkey.retromusic.glide.RetroGlideExtension.profileBannerOptions
import code.name.monkey.retromusic.glide.RetroGlideExtension.userProfileOptions
import code.name.monkey.retromusic.model.user.User
import code.name.monkey.retromusic.model.user.UserClient
import code.name.monkey.retromusic.network.Result
import code.name.monkey.retromusic.util.ImageUtil
import code.name.monkey.retromusic.util.MySharedPreferences
import code.name.monkey.retromusic.util.PreferenceUtil.image
import code.name.monkey.retromusic.util.PreferenceUtil.imageBanner
import code.name.monkey.retromusic.util.PreferenceUtil.userName
import code.name.monkey.retromusic.util.extention.showToastSuccess
import code.name.monkey.retromusic.views.dialog.DialogConfirmCustom
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val libraryViewModel: LibraryViewModel by activityViewModel()
    private var imagePath: Uri? = null
    private var imagePathBanner: Uri? = null
    private var hasImageChanged = false
    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val hasChanged = s?.toString() != UserClient.name
            if (hasChanged || hasImageChanged) {
                binding.saveProfile?.visibility = View.VISIBLE
            } else {
                binding.saveProfile?.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment_container
            duration = 300L
            scrimColor = Color.TRANSPARENT
        }
        _binding = FragmentUserInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyToolbar(binding.toolbar)

        binding.nameContainer.accentColor()
        binding.next.accentColor()
        binding.email?.isEnabled = false
        binding.phone?.isEnabled = false
//        toggleVisibilityWithAnimation(binding.loginNull!!)
//        toggleVisibilityWithAnimation(binding.loginNotNullContainer!!)
        binding.userImage.setOnClickListener {
            showUserImageOptions()
        }

        binding.name.addTextChangedListener(textWatcher)

        binding.bannerImage.setOnClickListener {
            showBannerImageOptions()
        }

        binding.login?.setOnClickListener {
            findNavController().findNavControllerOpen(
                R.id.loginFragment
            )
        }

        binding.logOut?.setOnClickListener {
            DialogConfirmCustom.create(
                activity!!,
                getString(R.string.confirmLogOut),
                onAccessClick = {
                    MySharedPreferences.getInstance(activity!!)
                        .putString(TOKEN_USER, "")
                    checkTokenAndVisibility("")
                    UserClient.setUserFromUser(User())
                    userName = getString(R.string.user_name)
                },
                onCancelClick = {}
            ).show()
        }

        binding.saveProfile?.setOnClickListener {
            val emailBodyRequest: RequestBody = RequestBody.create(
                "text/plain".toMediaTypeOrNull(), UserClient.email.toString()
            )

            val fullNameRequestBody: RequestBody? =
                if (binding.name.text.toString() != null) RequestBody.create(
                    "text/plain".toMediaTypeOrNull(), binding.name.text.toString()
                ) else null

            val imageFilePart: MultipartBody.Part? = if (imagePath != null) {
                val imageFile = File(imagePath!!.path!!)
                val imageRequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)
                MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
            } else {
                null
            }

            val imageBannerFilePart: MultipartBody.Part? = if (imagePathBanner != null) {
                val imageBannerFile = File(imagePathBanner!!.path!!)
                val imageBannerRequestBody = RequestBody.create(
                    "image/*".toMediaTypeOrNull(),
                    imageBannerFile
                )
                MultipartBody.Part.createFormData(
                    "imageBanner",
                    imageBannerFile.name,
                    imageBannerRequestBody
                )
            } else {
                null
            }

            libraryViewModel.updateUserInfo(
                emailBodyRequest,
                fullNameRequestBody,
                imageFilePart,
                imageBannerFilePart
            )
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {

                        }

                        is Result.Error -> {

                        }

                        is Result.Success -> {
                            userName = result.data.data.fullName
                            image = result.data.data.image
                            imageBanner = result.data.data.imageBanner
                            showToastSuccess(
                                activity!!,
                                getString(R.string.notification),
                                result.data.message.message
                            )
                            binding.saveProfile?.visibility = View.GONE
                        }
                    }
                }
        }

        binding.next.setOnClickListener {
            val nameString = binding.name.text.toString().trim { it <= ' ' }
            if (nameString.isEmpty()) {
                showToast(R.string.error_empty_name)
                return@setOnClickListener
            }
            userName = nameString
            findNavController().navigateUp()
        }



        loadProfile()
        postponeEnterTransition()
        view.doOnPreDraw {
            startPostponedEnterTransition()
        }
        libraryViewModel.getFabMargin().observe(viewLifecycleOwner) {
            binding.next.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = it
            }
//            binding.login?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
//                bottomMargin = it
//            }
        }
    }

    private fun checkTokenAndVisibility(token: String) {
        if (!token.isEmpty()) {
            binding.loginNotNullContainer?.visibility = View.VISIBLE
            binding.loginNull?.visibility = View.GONE
        } else {
            binding.loginNotNullContainer?.visibility = View.GONE
            binding.loginNull?.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onResume() {
        super.onResume()
        binding.name.setText(UserClient.name)
        binding.phone?.setText(UserClient.phone)
        binding.email?.setText(UserClient.email)
        checkTokenAndVisibility(
            MySharedPreferences.getInstance(activity!!).getString(TOKEN_USER, "")
                .toString()
        )
    }

    private fun showBannerImageOptions() {
        val list = requireContext().resources.getStringArray(R.array.image_settings_options)
        MaterialAlertDialogBuilder(requireContext()).setTitle("Banner Image")
            .setItems(list) { _, which ->
                when (which) {
                    0 -> selectBannerImage()
                    1 -> {
                        val appDir = requireContext().filesDir
                        val file = File(appDir, USER_BANNER)
                        file.delete()
                        loadProfile()
                    }
                }
            }.setNegativeButton(R.string.action_cancel, null).create().show()
    }

    private fun showUserImageOptions() {
        val list = requireContext().resources.getStringArray(R.array.image_settings_options)
        MaterialAlertDialogBuilder(requireContext()).setTitle("Profile Image")
            .setItems(list) { _, which ->
                when (which) {
                    0 -> pickNewPhoto()
                    1 -> {
                        val appDir = requireContext().filesDir
                        val file = File(appDir, USER_PROFILE)
                        file.delete()
                        loadProfile()
                    }
                }
            }.setNegativeButton(R.string.action_cancel, null).create().show()
    }

    private fun loadProfile() {
        loadImage(requireContext() ,BASE_URL_IMAGE + imageBanner, binding.bannerImage)
        loadImage(requireContext() ,BASE_URL_IMAGE + image, binding.userImage)
    }

    private fun selectBannerImage() {
        ImagePicker.with(this).compress(1440).provider(ImageProvider.GALLERY).crop(16f, 9f)
            .createIntent {
                startForBannerImageResult.launch(it)
            }
    }

    private fun pickNewPhoto() {
        ImagePicker.with(this).provider(ImageProvider.GALLERY).cropSquare().compress(1440)
            .createIntent {
                startForProfileImageResult.launch(it)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            saveImage(result) { fileUri -> // get uri
                setAndSaveUserImage(fileUri)
            }
        }

    private val startForBannerImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            saveImage(result) { fileUri ->
                setAndSaveBannerImage(fileUri)
            }
        }

    private fun saveImage(result: ActivityResult, doIfResultOk: (uri: Uri) -> Unit) {
        val resultCode = result.resultCode
        val data = result.data
        when (resultCode) {
            Activity.RESULT_OK -> {
                data?.data?.let { uri ->
                    doIfResultOk(uri)
                }
            }

            ImagePicker.RESULT_ERROR -> {
                showToast(ImagePicker.getError(data))
            }

            else -> {
//                showToast("Task Cancelled")
            }
        }
    }

    private fun setAndSaveBannerImage(fileUri: Uri) {
        Glide.with(this).asBitmap().load(fileUri).diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    resource?.let {
//                        saveImage(it, USER_BANNER)
                        imagePathBanner = fileUri
                        binding.saveProfile?.visibility = View.VISIBLE
                        hasImageChanged = true
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }
            }).into(binding.bannerImage)
    }

    private fun saveImage(bitmap: Bitmap, fileName: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val appDir = requireContext().filesDir
            val file = File(appDir, fileName)
            var successful: Boolean
            file.outputStream().buffered().use {
                successful = ImageUtil.resizeBitmap(bitmap, 2048)
                    .compress(Bitmap.CompressFormat.WEBP, 100, it)
            }
            if (successful) {
                withContext(Dispatchers.Main) {
                    showToast(R.string.message_updated)
                }
            }
        }
    }

    private fun setAndSaveUserImage(fileUri: Uri) {
        Glide.with(this).asBitmap().load(fileUri).diskCacheStrategy(DiskCacheStrategy.NONE)
            .listener(object : RequestListener<Bitmap> {
                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    resource?.let {
                        /*saveImage(it, USER_PROFILE)*/
                        imagePath = fileUri
                        binding.saveProfile?.visibility = View.VISIBLE
                        hasImageChanged = true
                    }
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }
            }).into(binding.userImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
