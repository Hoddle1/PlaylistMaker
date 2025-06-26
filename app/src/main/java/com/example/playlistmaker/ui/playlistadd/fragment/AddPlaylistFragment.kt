package com.example.playlistmaker.ui.playlistadd.fragment

import android.Manifest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.example.playlistmaker.ui.playlistadd.view_model.AddPlaylistViewModel
import com.example.playlistmaker.util.UiMessageHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddPlaylistFragment : Fragment() {
    private var _binding: FragmentAddPlaylistBinding? = null
    private val binding get() = _binding!!
    private val requester = PermissionRequester.instance()
    private var coverImagePath: Uri? = null

    private val viewModel by viewModel<AddPlaylistViewModel>()
    private val uiMessageHelper: UiMessageHelper by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    binding.bPhotoPicker.setImageURI(uri)
                    coverImagePath = uri
                } else {
                    Log.d("PhotoPicker", "Не выбрано изображение")
                }
            }

        binding.iBtnBack.setOnClickListener {
            if (!binding.etPlaylistName.text.isNullOrEmpty() ||
                !binding.etPlaylistDescription.text.isNullOrEmpty() ||
                coverImagePath != null
            ) {
                context?.let { ctx ->
                    MaterialAlertDialogBuilder(ctx)
                        .setTitle("Завершить создание плейлиста?")
                        .setMessage("Все несохраненные данные будут потеряны")
                        .setNeutralButton("Отмена") { dialog, which -> }
                        .setPositiveButton("Да") { dialog, which ->
                            findNavController().navigateUp()
                        }
                        .show()
                }
            } else {
                findNavController().navigateUp()
            }
        }

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
                                "Нужно разрешение для выбора фото",
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Denied.NeedsRationale -> Toast.makeText(
                                context,
                                "Объясните, зачем нужно разрешение",
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Denied.DeniedPermanently -> Toast.makeText(
                                context,
                                "Разрешение отклонено навсегда. Откройте настройки",
                                Toast.LENGTH_LONG
                            ).show()

                            is PermissionResult.Cancelled -> Toast.makeText(
                                context,
                                "Запрос отменён",
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

            uiMessageHelper.showCustomSnackbar(
                view = view,
                message = "Плейлист ${playlistName} создан"
            )

            findNavController().navigateUp()
        }
    }
}