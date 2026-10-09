package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.utils.ImageStorageHelper
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Saves photo or video bit-by-bit into the public device Gallery in 100% original quality,
 * preserving full resolution, bitrates, audio tracks, and codecs.
 */
suspend fun saveMediaToPublicGallery(context: Context, mediaItem: String, isVideo: Boolean): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val timestamp = System.currentTimeMillis()
            val rawName = mediaItem.substringAfterLast("/").substringBefore("?").ifBlank {
                "${if (isVideo) "video" else "photo"}_$timestamp"
            }

            val (fileName, mimeType) = if (isVideo) {
                val name = if (rawName.endsWith(".mov", ignoreCase = true)) {
                    rawName
                } else if (!rawName.endsWith(".mp4", ignoreCase = true)) {
                    "$rawName.mp4"
                } else rawName
                val mime = if (name.endsWith(".mov", ignoreCase = true)) "video/quicktime" else "video/mp4"
                Pair(name, mime)
            } else {
                val name = when {
                    rawName.endsWith(".png", ignoreCase = true) -> rawName
                    rawName.endsWith(".webp", ignoreCase = true) -> rawName
                    rawName.endsWith(".jpg", ignoreCase = true) || rawName.endsWith(".jpeg", ignoreCase = true) -> rawName
                    else -> "$rawName.jpg"
                }
                val mime = when {
                    name.endsWith(".png", ignoreCase = true) -> "image/png"
                    name.endsWith(".webp", ignoreCase = true) -> "image/webp"
                    else -> "image/jpeg"
                }
                Pair(name, mime)
            }

            fun openSourceStream(): InputStream? {
                return when {
                    mediaItem.startsWith("content://") -> contentResolver.openInputStream(Uri.parse(mediaItem))
                    mediaItem.startsWith("file://") -> FileInputStream(File(mediaItem.removePrefix("file://")))
                    mediaItem.startsWith("/") -> FileInputStream(File(mediaItem))
                    mediaItem.startsWith("http://") || mediaItem.startsWith("https://") -> java.net.URL(mediaItem).openStream()
                    else -> {
                        val f = File(mediaItem)
                        if (f.exists()) FileInputStream(f) else null
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val directory = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
                val collectionUri = if (isVideo) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                } else {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                }

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "$directory/TravelTracker")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val itemUri = contentResolver.insert(collectionUri, contentValues) ?: return@withContext false

                val sourceStream = openSourceStream() ?: return@withContext false
                sourceStream.use { input ->
                    contentResolver.openOutputStream(itemUri)?.use { output ->
                        input.copyTo(output)
                        output.flush()
                    } ?: return@withContext false
                }

                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(itemUri, contentValues, null, null)

                try {
                    android.media.MediaScannerConnection.scanFile(
                        context,
                        arrayOf(itemUri.toString()),
                        arrayOf(mimeType),
                        null
                    )
                } catch (e: Exception) {
                    // Ignored on modern Android
                }
                true
            } else {
                @Suppress("DEPRECATION")
                val baseDir = if (isVideo) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                } else {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                }
                val appDir = File(baseDir, "TravelTracker").apply { mkdirs() }
                val targetFile = File(appDir, fileName)

                val sourceStream = openSourceStream() ?: return@withContext false
                sourceStream.use { input ->
                    java.io.FileOutputStream(targetFile).use { output ->
                        input.copyTo(output)
                        output.flush()
                    }
                }

                android.media.MediaScannerConnection.scanFile(
                    context,
                    arrayOf(targetFile.absolutePath),
                    arrayOf(mimeType),
                    null
                )
                true
            }
        } catch (e: Exception) {
            Log.e("ExpandedPhotoDialog", "Errore salvataggio media nella Galleria", e)
            false
        }
    }
}

/**
 * Zoomable Photo component supporting two-finger pinch-to-zoom (spreading fingers to zoom in,
 * pinching together to zoom out) and panning around the enlarged photo. Double-tap to quick zoom.
 */
@Composable
fun ZoomablePhotoView(
    mediaItem: String,
    title: String?,
    modifier: Modifier = Modifier,
    onZoomChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { containerSize = it }
            .pointerInput(mediaItem) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        if (scale > 1.05f) {
                            scale = 1f
                            offset = Offset.Zero
                            onZoomChanged(false)
                        } else {
                            scale = 2.5f
                            val center = Offset(containerSize.width / 2f, containerSize.height / 2f)
                            val targetOffset = (center - tapOffset) * 1.5f
                            val maxOffsetX = (containerSize.width * 1.5f / 2f).coerceAtLeast(0f)
                            val maxOffsetY = (containerSize.height * 1.5f / 2f).coerceAtLeast(0f)
                            offset = Offset(
                                x = targetOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                y = targetOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                            )
                            onZoomChanged(true)
                        }
                    }
                )
            }
            .pointerInput(mediaItem) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pointerCount = event.changes.size

                        if (scale > 1.05f) {
                            // Enlarged photo: pan and zoom
                            val zoomChange = event.calculateZoom()
                            val panChange = event.calculatePan()

                            val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                            scale = newScale

                            if (newScale > 1.02f) {
                                onZoomChanged(true)
                                val maxOffsetX = (containerSize.width * (newScale - 1f) / 2f).coerceAtLeast(0f)
                                val maxOffsetY = (containerSize.height * (newScale - 1f) / 2f).coerceAtLeast(0f)
                                val newOffset = offset + panChange
                                offset = Offset(
                                    x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                                    y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                                )
                                event.changes.forEach { change ->
                                    if (change.positionChanged()) change.consume()
                                }
                            } else {
                                scale = 1f
                                offset = Offset.Zero
                                onZoomChanged(false)
                            }
                        } else if (pointerCount >= 2) {
                            // 2 or more fingers: pinch zoom from 1x
                            val zoomChange = event.calculateZoom()
                            if (zoomChange > 1.01f || zoomChange < 0.99f) {
                                val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                                scale = newScale
                                if (newScale > 1.02f) {
                                    onZoomChanged(true)
                                }
                                event.changes.forEach { change ->
                                    if (change.positionChanged()) change.consume()
                                }
                            }
                        } else {
                            // Single finger drag when scale <= 1.05f:
                            // We do NOT consume the event here!
                            // This allows HorizontalPager to naturally receive horizontal swipe gestures
                            // to move between photos!
                        }
                    } while (event.changes.any { it.pressed })

                    if (scale <= 1.02f) {
                        scale = 1f
                        offset = Offset.Zero
                        onZoomChanged(false)
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(ImageStorageHelper.getImageModel(mediaItem))
                .crossfade(true)
                .build(),
            contentDescription = title ?: "Foto ingrandita",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                },
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Thumbnail component for video files with background frame extraction.
 */
@Composable
fun VideoThumbnailView(
    pathOrUri: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember(pathOrUri) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pathOrUri) {
        withContext(Dispatchers.IO) {
            val bmp = ImageStorageHelper.getVideoThumbnail(context, pathOrUri)
            withContext(Dispatchers.Main) {
                bitmap = bmp
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Anteprima Video",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFF1E293B)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = "Video",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Integrated native Video Player with audible audio, audio focus, volume mute toggle,
 * seekbar, and explicit play/pause controls.
 */
@Composable
fun IntegratedVideoPlayer(
    videoPathOrUri: String,
    isPlayingActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var isEnded by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var currentPositionMs by remember { mutableStateOf(0) }
    var durationMs by remember { mutableStateOf(0) }
    var showControls by remember { mutableStateOf(true) }

    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var mediaPlayerRef by remember { mutableStateOf<MediaPlayer?>(null) }

    // Periodically update current playback position and duration
    LaunchedEffect(isPlaying, isPrepared) {
        while (isActive && isPlaying) {
            videoViewRef?.let { vv ->
                currentPositionMs = vv.currentPosition
                if (durationMs <= 0) {
                    durationMs = vv.duration
                }
            }
            delay(250)
        }
    }

    // Auto-hide controls after 3.5s while playing
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(3500)
            showControls = false
        }
    }

    // Pause video when page is swiped away
    LaunchedEffect(isPlayingActive) {
        if (!isPlayingActive) {
            videoViewRef?.pause()
            isPlaying = false
            showControls = true
        }
    }

    DisposableEffect(videoPathOrUri) {
        onDispose {
            try {
                videoViewRef?.stopPlayback()
            } catch (e: Exception) {
                // safely ignore
            }
        }
    }

    fun formatTime(ms: Int): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val minutes = totalSec / 60
        val seconds = totalSec % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            },
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    val uri = if (videoPathOrUri.startsWith("/")) {
                        Uri.fromFile(File(videoPathOrUri))
                    } else {
                        Uri.parse(videoPathOrUri)
                    }

                    // Set modern AudioAttributes for high quality sound
                    val audioAttributes = android.media.AudioAttributes.Builder()
                        .setUsage(android.media.AudioAttributes.USAGE_MEDIA)
                        .setContentType(android.media.AudioAttributes.CONTENT_TYPE_MOVIE)
                        .build()
                    setAudioAttributes(audioAttributes)
                    setVideoURI(uri)

                    // Request audio focus so audio is clearly audible
                    val audioManager = ctx.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        val req = android.media.AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(audioAttributes)
                            .build()
                        audioManager?.requestAudioFocus(req)
                    } else {
                        @Suppress("DEPRECATION")
                        audioManager?.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                    }

                    setOnPreparedListener { mp ->
                        mediaPlayerRef = mp
                        try {
                            mp.setAudioAttributes(audioAttributes)
                        } catch (e: Exception) {
                            @Suppress("DEPRECATION")
                            mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
                        }
                        val vol = if (isMuted) 0f else 1.0f
                        mp.setVolume(vol, vol)
                        mp.isLooping = false

                        isPrepared = true
                        durationMs = mp.duration

                        if (isPlayingActive) {
                            start()
                            isPlaying = true
                        }
                    }

                    setOnCompletionListener {
                        isPlaying = false
                        isEnded = true
                        showControls = true
                        currentPositionMs = durationMs
                    }

                    setOnErrorListener { _, _, _ ->
                        isPrepared = false
                        true
                    }

                    videoViewRef = this
                }
            },
            update = { view ->
                videoViewRef = view
            }
        )

        // Loading spinner while buffering/preparing
        if (!isPrepared) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
        }

        // Overlay controls (Play/Pause, Timeline, Mute, Rewind/Forward)
        AnimatedVisibility(
            visible = showControls || !isPlaying || isEnded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            ) {
                // Central Big Play / Pause / Replay Button
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                        .testTag("video_central_play_pause_button")
                        .clickable {
                            videoViewRef?.let { vv ->
                                if (isEnded) {
                                    vv.seekTo(0)
                                    isEnded = false
                                    vv.start()
                                    isPlaying = true
                                } else if (isPlaying || vv.isPlaying) {
                                    vv.pause()
                                    isPlaying = false
                                    showControls = true
                                } else {
                                    vv.start()
                                    isPlaying = true
                                }
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = when {
                                isEnded -> Icons.Default.Replay
                                isPlaying -> Icons.Default.Pause
                                else -> Icons.Default.PlayArrow
                            },
                            contentDescription = if (isPlaying) "Pausa" else "Riproduci",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                // Top Audio Mute/Unmute Indicator & Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Unmute Button
                    Surface(
                        shape = CircleShape,
                        color = if (isMuted) Color(0xFFEF4444).copy(alpha = 0.85f) else Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                                isMuted = !isMuted
                                val vol = if (isMuted) 0f else 1.0f
                                mediaPlayerRef?.setVolume(vol, vol)
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = if (isMuted) "Riattiva audio" else "Disattiva audio",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bottom Timeline and Playback Action Bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Seekbar / Slider
                    if (durationMs > 0) {
                        Slider(
                            value = currentPositionMs.toFloat().coerceIn(0f, durationMs.toFloat()),
                            onValueChange = { newPos ->
                                currentPositionMs = newPos.toInt()
                                videoViewRef?.seekTo(newPos.toInt())
                            },
                            valueRange = 0f..durationMs.toFloat(),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFF27D26),
                                activeTrackColor = Color(0xFFF27D26),
                                inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Transport Bar (Rewind, Play/Pause, Fast Forward, Time Display)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Play / Pause mini button
                            IconButton(
                                onClick = {
                                    videoViewRef?.let { vv ->
                                        if (isEnded) {
                                            vv.seekTo(0)
                                            isEnded = false
                                            vv.start()
                                            isPlaying = true
                                        } else if (isPlaying || vv.isPlaying) {
                                            vv.pause()
                                            isPlaying = false
                                            showControls = true
                                        } else {
                                            vv.start()
                                            isPlaying = true
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("video_mini_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isPlaying) "Pausa" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Rewind 10s
                            IconButton(
                                onClick = {
                                    videoViewRef?.let { vv ->
                                        val target = (vv.currentPosition - 10000).coerceAtLeast(0)
                                        vv.seekTo(target)
                                        currentPositionMs = target
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastRewind,
                                    contentDescription = "Indietro 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Forward 10s
                            IconButton(
                                onClick = {
                                    videoViewRef?.let { vv ->
                                        val target = (vv.currentPosition + 10000).coerceAtMost(durationMs)
                                        vv.seekTo(target)
                                        currentPositionMs = target
                                    }
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FastForward,
                                    contentDescription = "Avanti 10s",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        // Time display
                        Text(
                            text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen Media Viewer with swipe gesture (HorizontalPager), video playback,
 * previous/next navigation arrows, and mini close button.
 */
@Composable
fun ExpandedPhotoDialog(
    mediaList: List<String>,
    initialIndex: Int = 0,
    title: String? = null,
    subtitle: String? = null,
    onDismissRequest: () -> Unit
) {
    if (mediaList.isEmpty()) {
        onDismissRequest()
        return
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val validInitialIndex = initialIndex.coerceIn(0, mediaList.size - 1)
    val pagerState = rememberPagerState(initialPage = validInitialIndex) { mediaList.size }
    var isPhotoZoomed by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        isPhotoZoomed = false
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = !isPhotoZoomed,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        val view = LocalView.current
        SideEffect {
            val window = (view.parent as? DialogWindowProvider)?.window
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
            }
        }
        val navBarBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        val safeBottomPadding = maxOf(navBarBottom, 32.dp) + 16.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.96f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        if (!isPhotoZoomed) {
                            onDismissRequest()
                        }
                    }
                )
                .testTag("expanded_photo_dialog_backdrop")
        ) {
            // Main Swipeable Pager - disable pager swiping when photo is zoomed in
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = !isPhotoZoomed,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} // Consume click so it doesn't dismiss dialog accidentally
                    )
            ) { page ->
                val mediaItem = mediaList[page]
                val isVideo = ImageStorageHelper.isVideo(mediaItem)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 76.dp, bottom = safeBottomPadding + 64.dp, start = 8.dp, end = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isVideo) {
                        // Videos do not have pinch-to-zoom (as per request: "Vale solo per le foto, non per i video")
                        IntegratedVideoPlayer(
                            videoPathOrUri = mediaItem,
                            isPlayingActive = pagerState.currentPage == page,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    } else {
                        // Photos have full two-finger pinch-to-zoom and pan gestures
                        ZoomablePhotoView(
                            mediaItem = mediaItem,
                            title = title,
                            onZoomChanged = { zoomed ->
                                if (pagerState.currentPage == page) {
                                    isPhotoZoomed = zoomed
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }

            // Top Header: Title, Subtitle, Download, Counter & Mini Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (!title.isNullOrBlank()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val currentMedia = mediaList[pagerState.currentPage]
                    val isCurrentVid = ImageStorageHelper.isVideo(currentMedia)

                    // Download to Gallery button in Top Bar
                    FilledTonalIconButton(
                        onClick = {
                            coroutineScope.launch {
                                val ok = saveMediaToPublicGallery(context, currentMedia, isCurrentVid)
                                if (ok) {
                                    Toast.makeText(
                                        context,
                                        if (isCurrentVid) "Video salvato nella Galleria! 🎬" else "Foto salvata nella Galleria! 📸",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Errore durante il salvataggio", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("download_media_top_button"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Scarica in Galleria",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Counter Tag
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isCurrentVid) Icons.Default.Videocam else Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "${pagerState.currentPage + 1} / ${mediaList.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mini Close Button ("X")
                    FilledTonalIconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("close_expanded_photo_button"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi foto",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Navigation Chevron Buttons (hidden while zoomed to prevent accidental clicks)
            if (mediaList.size > 1 && !isPhotoZoomed) {
                // Previous button
                if (pagerState.currentPage > 0) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Media precedente",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Next button
                if (pagerState.currentPage < mediaList.size - 1) {
                    Surface(
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Media successivo",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            // Bottom Bar with "Salva in Galleria" Button, "Chiudi" Button and Pinch/Swipe Hints
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = safeBottomPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentMedia = mediaList[pagerState.currentPage]
                val isCurrentVid = ImageStorageHelper.isVideo(currentMedia)

                if (!isCurrentVid) {
                    Text(
                        text = if (isPhotoZoomed) "Doppio tocco o pizzica per tornare normale" else "Pizzica o scorri orizzontalmente per cambiare foto",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                } else if (mediaList.size > 1) {
                    Text(
                        text = "Scorri a destra o sinistra per sfogliare i media",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                    shadowElevation = 8.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Prominent Download to Gallery Button
                        FilledTonalButton(
                            onClick = {
                                coroutineScope.launch {
                                    val ok = saveMediaToPublicGallery(context, currentMedia, isCurrentVid)
                                    if (ok) {
                                        Toast.makeText(
                                            context,
                                            if (isCurrentVid) "Video salvato nella Galleria! 🎬" else "Foto salvata nella Galleria! 📸",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, "Errore durante il salvataggio", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFF27D26),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(22.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("download_media_bottom_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCurrentVid) "Salva Video" else "Salva Foto",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Close Button
                        Button(
                            onClick = onDismissRequest,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White.copy(alpha = 0.3f),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(22.dp),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("close_photo_viewer_bottom_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Chiudi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Overload for backward compatibility when calling with a single photoUri.
 */
@Composable
fun ExpandedPhotoDialog(
    photoUri: String,
    title: String? = null,
    subtitle: String? = null,
    onDismissRequest: () -> Unit
) = ExpandedPhotoDialog(
    mediaList = listOf(photoUri),
    initialIndex = 0,
    title = title,
    subtitle = subtitle,
    onDismissRequest = onDismissRequest
)
