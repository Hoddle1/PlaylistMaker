package com.example.playlistmaker.presentation.ui.playlist_form.base

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch

abstract class BasePlaylistFragment : Fragment() {
    private var _binding: FragmentAddPlaylistBinding? = null
    private val requester = PermissionRequester.instance()
    private lateinit var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>

    protected val binding
        get() = _binding ?: throw IllegalStateException(getString(R.string.binding_is_null))
    protected var coverImagePath: Uri? = null
    protected abstract val isFromActivity: Boolean
    protected abstract val viewModel: BasePlaylistViewModel
    protected abstract val screenTitle: String
    protected abstract val actionButtonText: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.bPhotoPicker.setImageURI(uri)
                coverImagePath = uri
            } else {
                Log.d("PhotoPicker", getString(R.string.no_select_image))
            }
        }

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )

        binding.tvTitle.text = screenTitle
        binding.bCreate.text = actionButtonText

        if (!isFromActivity) {
            setupInsets()
        }

        setupListeners()
        setupTextWatchers()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.addPlaylistFragment) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }
    }

    private fun setupListeners() {
        binding.iBtnBack.setOnClickListener { handleBackAction() }
        binding.bCreate.setOnClickListener { onSaveClicked() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackAction()
                }
            })

        binding.bPhotoPicker.setOnClickListener { pickImage() }
    }

    private fun setupTextWatchers() {
        binding.etPlaylistName.doOnTextChanged { s, _, _, _ ->
            binding.bCreate.isEnabled = !s.isNullOrEmpty()
        }
    }

    protected open fun pickImage() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            lifecycleScope.launch {
                requester.request(Manifest.permission.READ_EXTERNAL_STORAGE).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> pickMedia.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )

                        is PermissionResult.Denied,
                        is PermissionResult.Denied.NeedsRationale,
                        is PermissionResult.Denied.DeniedPermanently,
                        is PermissionResult.Cancelled -> showPermissionDeniedMessage(result)
                    }
                }
            }
        }
    }

    private fun showPermissionDeniedMessage(result: PermissionResult) {
        val messageRes = when (result) {
            is PermissionResult.Denied -> R.string.permission_denied
            is PermissionResult.Denied.NeedsRationale -> R.string.permission_rationale
            is PermissionResult.Denied.DeniedPermanently -> R.string.permission_denied_permanently
            else -> R.string.permission_cancelled
        }
        Toast.makeText(context, getString(messageRes), Toast.LENGTH_LONG).show()
    }

    protected open fun handleBackAction() {
        if (hasUnsavedChanges()) {
            showExitConfirmationDialog()
        } else {
            closeFragment()
        }
    }

    protected open fun hasUnsavedChanges(): Boolean {
        return !binding.etPlaylistName.text.isNullOrEmpty() ||
                !binding.etPlaylistDescription.text.isNullOrEmpty() ||
                coverImagePath != null
    }

    private fun showExitConfirmationDialog() {
        context?.let { ctx ->
            MaterialAlertDialogBuilder(ctx)
                .setTitle(getString(R.string.dialog_finish_creating_playlist_title))
                .setMessage(getString(R.string.dialog_finish_creating_playlist_message))
                .setNeutralButton(getString(R.string.cansel), null)
                .setPositiveButton(getString(R.string.yes)) { _, _ -> closeFragment() }
                .show()
        }
    }

    protected fun closeFragment() {
        if (isFromActivity) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            findNavController().popBackStack()
        }
    }

    protected abstract fun onSaveClicked()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}