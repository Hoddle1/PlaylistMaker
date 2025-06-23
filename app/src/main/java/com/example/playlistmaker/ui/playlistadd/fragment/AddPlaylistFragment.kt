package com.example.playlistmaker.ui.playlistadd.fragment

import android.Manifest
import android.R.layout
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAddPlaylistBinding
import com.example.playlistmaker.ui.playlistadd.view_model.AddPlaylistViewModel
import com.example.playlistmaker.util.UiMessageHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream


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
            if (binding.etPlaylistName.text.isNullOrEmpty() ||
                binding.etPlaylistDescription.text.isNullOrEmpty() ||
                binding.bPhotoPicker.drawable != null
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

            coverImagePath?.let { saveImageToPrivateStorage(it, playlistName) }

            viewModel.createPlaylist(
                name = playlistName,
                description = playlistDescription,
                coverImagePath = coverImagePath.toString()
            )

            uiMessageHelper.showCustomSnackbar(
                view = view,
                message = "Плейлист ${playlistName} создан"
            )

            findNavController().navigateUp()
        }
    }


    private fun saveImageToPrivateStorage(uri: Uri, name: String) {
        val filePath =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${name}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}