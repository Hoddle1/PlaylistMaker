package com.example.playlistmaker.data.playlistadd.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.domain.playlistadd.PlaylistImageStorageRepository
import java.io.File
import java.io.FileOutputStream

class PlaylistImageStorageRepositoryImpl(private val context: Context) : PlaylistImageStorageRepository {
    override fun saveCoverImage(uri: Uri, name: String): Uri? {
        return try {
            val filePath =
                File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "covers")
            if (!filePath.exists()) filePath.mkdirs()
            val file = File(filePath, "$name.jpg")
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory.decodeStream(inputStream)
                .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}