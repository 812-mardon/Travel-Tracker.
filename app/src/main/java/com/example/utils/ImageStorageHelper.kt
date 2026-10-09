package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageStorageHelper {
    private const val TAG = "ImageStorageHelper"
    private val videoThumbnailCache = LruCache<String, Bitmap>(35)

    /**
     * Checks if a path or URI corresponds to a video file.
     */
    fun isVideo(uriOrPath: String): Boolean {
        val clean = uriOrPath.lowercase().trim()
        return clean.endsWith(".mp4") ||
                clean.endsWith(".mov") ||
                clean.endsWith(".mkv") ||
                clean.endsWith(".3gp") ||
                clean.endsWith(".webm") ||
                clean.contains("/video_") ||
                clean.contains("video_") ||
                clean.contains("video%2f") ||
                clean.contains("/video/")
    }

    /**
     * Extracts a frame thumbnail from a video file, cached in memory.
     */
    fun getVideoThumbnail(context: Context, pathOrUri: String): Bitmap? {
        videoThumbnailCache.get(pathOrUri)?.let { return it }
        return try {
            val retriever = MediaMetadataRetriever()
            if (pathOrUri.startsWith("/")) {
                retriever.setDataSource(pathOrUri)
            } else {
                retriever.setDataSource(context, Uri.parse(pathOrUri))
            }
            val bitmap = retriever.getFrameAtTime(1_000_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime
            retriever.release()
            if (bitmap != null) {
                videoThumbnailCache.put(pathOrUri, bitmap)
            }
            bitmap
        } catch (e: Exception) {
            Log.w(TAG, "Impossibile estrarre anteprima video per: $pathOrUri", e)
            null
        }
    }

    /**
     * Copies a picked image or video from a Content URI into the app's internal private storage.
     * Stored under: filesDir/travel_media/{countryId}/{regionId}/
     * Supports multiple extensions (jpg, png, mp4, mov, etc.).
     * Returns the permanent absolute file path, or null if copy fails.
     */
    fun saveMediaFromUri(
        context: Context,
        uri: Uri,
        countryId: String,
        regionId: String
    ): String? {
        return try {
            val safeCountry = countryId.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            val safeRegion = regionId.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
            val targetDir = File(context.filesDir, "travel_media/$safeCountry/$safeRegion")
            if (!targetDir.exists()) {
                targetDir.mkdirs()
            }

            val mimeType = context.contentResolver.getType(uri)?.lowercase() ?: ""
            val isVid = mimeType.startsWith("video/") ||
                    uri.toString().contains("video", ignoreCase = true) ||
                    isVideo(uri.toString())

            val ext = when {
                mimeType.contains("mp4") -> "mp4"
                mimeType.contains("quicktime") || mimeType.contains("mov") -> "mov"
                mimeType.contains("mkv") -> "mkv"
                mimeType.contains("3gpp") || mimeType.contains("3gp") -> "3gp"
                mimeType.contains("webm") -> "webm"
                isVid -> "mp4"
                mimeType.contains("png") -> "png"
                mimeType.contains("webp") -> "webp"
                mimeType.contains("gif") -> "gif"
                else -> "jpg"
            }

            val prefix = if (isVid) "video" else "photo"
            val fileName = "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.$ext"
            val destFile = File(targetDir, fileName)

            var inputStream: InputStream? = null
            var outputStream: FileOutputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    Log.e(TAG, "Impossibile aprire l'input stream per l'URI: $uri")
                    return null
                }
                outputStream = FileOutputStream(destFile)
                inputStream.copyTo(outputStream)
                outputStream.flush()
                Log.d(TAG, "Media ($prefix) salvato con successo in memoria interna: ${destFile.absolutePath}")
                destFile.absolutePath
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante il salvataggio del media", e)
            null
        }
    }

    /**
     * Preserved for backwards compatibility with existing photo saving calls.
     */
    fun saveImageFromUri(
        context: Context,
        uri: Uri,
        countryId: String,
        regionId: String
    ): String? = saveMediaFromUri(context, uri, countryId, regionId)

    /**
     * Resolves the URI / file path string into an object suitable for Coil (File or String or Uri)
     */
    fun getImageModel(uriOrPath: String): Any {
        return when {
            uriOrPath.startsWith("/") -> File(uriOrPath)
            uriOrPath.startsWith("file://") -> File(uriOrPath.removePrefix("file://"))
            uriOrPath.startsWith("http://") || uriOrPath.startsWith("https://") -> uriOrPath
            else -> try {
                Uri.parse(uriOrPath)
            } catch (e: Exception) {
                uriOrPath
            }
        }
    }

    /**
     * Deletes a local image or video file if stored internally
     */
    fun deleteInternalImage(filePath: String) {
        try {
            if (filePath.startsWith("/")) {
                val file = File(filePath)
                if (file.exists() && file.isFile) {
                    file.delete()
                    Log.d(TAG, "File eliminato: $filePath")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Errore durante l'eliminazione del file: $filePath", e)
        }
    }
}
