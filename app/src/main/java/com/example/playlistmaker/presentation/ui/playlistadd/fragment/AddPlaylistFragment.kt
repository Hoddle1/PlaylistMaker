package com.example.playlistmaker.presentation.ui.playlistadd.fragment

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
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
import com.example.playlistmaker.presentation.ui.playlistadd.view_model.AddPlaylistViewModel
import com.example.playlistmaker.presentation.util.UiMessageHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class AddPlaylistFragment : Fragment() {
    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException(getString(R.string.binding_is_null))
    private val requester = PermissionRequester.instance()
    private var coverImagePath: Uri? = null
    private var isFromActivity: Boolean = false

    private val viewModel by viewModel<AddPlaylistViewModel>()
    private val uiMessageHelper: UiMessageHelper by inject { parametersOf(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFromActivity = arguments?.getBoolean(ARG_IS_FROM_ACTIVITY) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFromActivity) {
            ViewCompat.setOnApplyWindowInsetsListener(binding.addPlaylistFragment) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, 0, 0, systemBars.bottom)
                insets
            }
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.bPhotoPicker.setImageURI(uri)
                    coverImagePath = uri
                } else {
                    Log.d("PhotoPicker", getString(R.string.no_select_image))
                }
            }

        binding.iBtnBack.setOnClickListener {
            handleBackAction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackAction()
            }
        })

        binding.bPhotoPicker.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                lifecycleScope.launch {
                    requester.request(Manifest.permission.READ_EXTERNAL_STORAGE).collect { result ->
                        when (result) {
                            is PermissionResult.Granted -> {
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }

                            is PermissionResult.Denied -> Toast.makeText(
                                context,
                                getString(R.string.permission_denied),
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Denied.NeedsRationale -> Toast.makeText(
                                context,
                                getString(R.string.permission_rationale),
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Denied.DeniedPermanently -> Toast.makeText(
                                context,
                                getString(R.string.permission_denied_permanently),
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Cancelled -> Toast.makeText(
                                context,
                                getString(R.string.permission_cancelled),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }

        binding.etPlaylistName.doOnTextChanged { s, _, _, _ ->
            binding.bCreate.isEnabled = !s.isNullOrEmpty()
        }

        binding.bCreate.setOnClickListener {
            val playlistName = binding.etPlaylistName.text.toString()
            val playlistDescription =
                binding.etPlaylistDescription.text.toString().takeIf { it.isNotBlank() }

            val coverImage = coverImagePath?.let {
                viewModel.saveCoverImage(it, playlistName)
            }

            viewModel.createPlaylist(
                name = playlistName,
                description = playlistDescription,
                coverImagePath = coverImage?.toString()
            )

            uiMessageHelper.showCustomSnackbar(getString(R.string.playlist_added, playlistName))

            closeFragment()
        }
    }

    private fun handleBackAction() {
        if (!binding.etPlaylistName.text.isNullOrEmpty() ||
            !binding.etPlaylistDescription.text.isNullOrEmpty() ||
            coverImagePath != null
        ) {
            context?.let { ctx ->
                MaterialAlertDialogBuilder(ctx)
                    .setTitle(getString(R.string.dialog_finish_creating_playlist_title))
                    .setMessage(getString(R.string.dialog_finish_creating_playlist_message))
                    .setNeutralButton(getString(R.string.cansel)) { dialog, which -> }
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        closeFragment()
                    }
                    .show()
            }
        } else {
            closeFragment()
        }
    }

    private fun closeFragment() {
        if (isFromActivity) {
            requireActivity().supportFragmentManager.popBackStack()
        } else {
            findNavController().popBackStack()
        }
    }

    companion object {
        private const val ARG_IS_FROM_ACTIVITY = "is_from_activity"

        fun newInstance(): AddPlaylistFragment {
            val fragment =
                AddPlaylistFragment()
            fragment.arguments = Bundle().apply {
                putBoolean(ARG_IS_FROM_ACTIVITY, true)
            }
            return fragment
        }
    }
}