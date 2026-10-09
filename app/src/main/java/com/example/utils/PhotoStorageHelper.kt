package com.example.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PhotoStorageHelper {
    private const val TAG = "PhotoStorageHelper"
    private const val PHOTOS_DIR = "travel_photos"

    /**
     * Safely copies an image from an incoming Uri (e.g. from the Android Photo Picker)
     * into the application's private internal storage directory.
     * This ensures the image file persists across app restarts, reboots, and has
     * permanent read permissions that never expire.
     */
    fun saveImageLocally(context: Context, sourceUri: Uri): String {
        return try {
            val photosFolder = File(context.filesDir, PHOTOS_DIR).apply {
                if (!exists()) mkdirs()
            }
            val destinationFile = File(
                photosFolder,
                "photo_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
            )

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            if (destinationFile.exists() && destinationFile.length() > 0) {
                Log.d(TAG, "Foto salvata localmente con successo: ${destinationFile.absolutePath}")
                destinationFile.absolutePath
            } else {
                Log.w(TAG, "File vuoto dopo il salvataggio, utilizzo URI originale")
                sourceUri.toString()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante il salvataggio locale della foto da $sourceUri", e)
            sourceUri.toString()
        }
    }

    /**
     * Resolves a photo path/uri string to a suitable model for Coil.
     * If it's a file path, returns the File object.
     * If it's a content URI, tries to make a permanent local copy if possible, or returns Uri.
     * If it's a web URL, returns the URL string.
     */
    fun getPhotoModel(context: Context, uriStr: String): Any {
        val trimmed = uriStr.trim()
        if (trimmed.isBlank()) return ""

        return when {
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> {
                trimmed
            }
            trimmed.startsWith("file://") -> {
                val path = trimmed.substring(7)
                val file = File(path)
                if (file.exists()) file else Uri.parse(trimmed)
            }
            trimmed.startsWith("content://") -> {
                // If it's a content URI, attempt to copy it to local storage if not already copied
                try {
                    val uri = Uri.parse(trimmed)
                    val localSaved = saveImageLocally(context, uri)
                    val file = File(localSaved)
                    if (file.exists() && file.length() > 0) file else uri
                } catch (e: Exception) {
                    Uri.parse(trimmed)
                }
            }
            else -> {
                // Check if it's a plain absolute file path (e.g. /data/user/0/...)
                val file = File(trimmed)
                if (file.exists()) {
                    file
                } else {
                    trimmed
                }
            }
        }
    }
}
