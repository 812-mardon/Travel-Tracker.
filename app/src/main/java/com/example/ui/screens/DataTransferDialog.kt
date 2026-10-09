package com.example.ui.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.TravelViewModel
import com.example.utils.DataTransferManager
import com.example.utils.DataTransferManager.TransferState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DataTransferDialog(
    viewModel: TravelViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val transferState by viewModel.transferState.collectAsStateWithLifecycle()
    val visitedRegions by viewModel.visitedRegions.collectAsStateWithLifecycle()

    var selectedMode by remember { mutableStateOf<TransferMode?>(null) }
    var manualIp by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Reactively generate and update QR bitmap whenever listening state is emitted with real IP and bound port
    LaunchedEffect(transferState) {
        val current = transferState
        if (current is TransferState.Listening) {
            val qrPayload = DataTransferManager.buildQrPayload(current.ip, current.port)
            qrBitmap = DataTransferManager.generateQrCode(qrPayload, 450)
        }
    }

    // Reset transfer state on open / close
    LaunchedEffect(Unit) {
        viewModel.resetTransferState()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetTransferState()
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("transfer_dialog_surface"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapHorizontalCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Trasferimento Dati",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Copia dati tra dispositivi via Wi-Fi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_transfer_dialog_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi")
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Main body with Animated Transition
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (selectedMode == null) {
                        // MODE SELECTOR VIEW
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Vuoi inviare o ricevere il tuo diario di viaggio?",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            // 1. Send Data Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMode = TransferMode.SENDER
                                        val ip = DataTransferManager.getLocalIpAddress(context)
                                        val qrPayload = DataTransferManager.buildQrPayload(ip, DataTransferManager.DEFAULT_PORT)
                                        qrBitmap = DataTransferManager.generateQrCode(qrPayload, 450)
                                        viewModel.startTransferSender(context)
                                    }
                                    .testTag("send_data_card"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode2,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "INVIA DATI",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "Mostra un QR Code per far copiare i tuoi viaggi, appunti e foto a un altro telefono.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            // 2. Receive Data Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMode = TransferMode.RECEIVER
                                    }
                                    .testTag("receive_data_card"),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.secondary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCodeScanner,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "RICEVI DATI",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Text(
                                            text = "Scansiona il QR Code del vecchio telefono per importare e sincronizzare istantaneamente il diario.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "Nota: Entrambi i telefoni devono essere connessi alla stessa rete Wi-Fi o connessi direttamente tramite Hotspot.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    } else {
                        // MODE ACTIVE VIEW (Sender or Receiver)
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Back button to choose another mode
                            TextButton(
                                onClick = {
                                    selectedMode = null
                                    viewModel.resetTransferState()
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.testTag("back_to_modes_button")
                            ) {
                                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Indietro")
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (selectedMode == TransferMode.SENDER) {
                                // SENDER FLOW
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Invia Diario di Viaggio 📡",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    // Display State Details
                                    when (val state = transferState) {
                                        is TransferState.Listening -> {
                                            Text(
                                                text = "Inquadra questo QR code con l'altro telefono per avviare il trasferimento",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )

                                            Spacer(modifier = Modifier.height(8.dp))

                                            qrBitmap?.let {
                                                Card(
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                                    shape = RoundedCornerShape(12.dp),
                                                    modifier = Modifier.padding(8.dp)
                                                ) {
                                                    Image(
                                                        bitmap = it.asImageBitmap(),
                                                        contentDescription = "QR Code di Trasferimento",
                                                        modifier = Modifier
                                                            .size(240.dp)
                                                            .padding(16.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Manual Details
                                            Card(
                                                colors = CardDefaults.cardColors(
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(
                                                        text = "CODICE DI ACCOPPIAMENTO MANUALE",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Black,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        Text(
                                                            text = "${state.ip}:${state.port}",
                                                            style = MaterialTheme.typography.titleMedium,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            letterSpacing = 1.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        IconButton(
                                                            onClick = {
                                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                                val clip = ClipData.newPlainText("Travel Tracker IP", "${state.ip}:${state.port}")
                                                                clipboard?.setPrimaryClip(clip)
                                                                Toast.makeText(context, "Indirizzo copiato!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.ContentCopy,
                                                                contentDescription = "Copia indirizzo",
                                                                modifier = Modifier.size(18.dp),
                                                                tint = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                                Text(
                                                    text = "In attesa che il ricevente si connetta...",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = Color(0xFFE65100)
                                                )
                                            }
                                        }
                                        is TransferState.Transferring -> {
                                            TransferringAnimationView(
                                                state = state,
                                                isSender = true,
                                                onCancel = { viewModel.resetTransferState() }
                                            )
                                        }
                                        is TransferState.Success -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.padding(vertical = 24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Text(
                                                    text = "Trasferimento Riuscito! 🎉",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                                Text(
                                                    text = state.message,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                if (state.totalMedia > 0 || state.totalRegions > 0) {
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                                        shape = RoundedCornerShape(12.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                                            horizontalArrangement = Arrangement.SpaceAround
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text("Luoghi Inviati", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                                Text("${state.totalRegions}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                                            }
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text("Media HD Inviati", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                                Text("${state.totalMedia}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                                                            }
                                                        }
                                                    }
                                                }

                                                Button(
                                                    onClick = onDismissRequest,
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                                ) {
                                                    Text("Ottimo!", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        is TransferState.Error -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.padding(vertical = 24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Error,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Text(
                                                    text = "Trasferimento Fallito",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                                Text(
                                                    text = state.error,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Button(
                                                    onClick = {
                                                        viewModel.startTransferSender(context)
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Riprova", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        else -> {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                            } else {
                                // RECEIVER FLOW
                                val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

                                // Auto-request camera permission if not granted
                                LaunchedEffect(Unit) {
                                    if (!cameraPermissionState.status.isGranted) {
                                        cameraPermissionState.launchPermissionRequest()
                                    }
                                }

                                val qrImagePickerLauncher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.PickVisualMedia(),
                                    onResult = { uri ->
                                        if (uri != null) {
                                            try {
                                                val stream = context.contentResolver.openInputStream(uri)
                                                val bitmap = BitmapFactory.decodeStream(stream)
                                                stream?.close()
                                                if (bitmap != null) {
                                                    val scannedText = DataTransferManager.decodeQrFromBitmap(bitmap)
                                                    val hostPort = scannedText?.let { DataTransferManager.parseQrPayload(it) }
                                                    if (hostPort != null) {
                                                        Toast.makeText(context, "QR code rilevato dall'immagine!", Toast.LENGTH_SHORT).show()
                                                        viewModel.startTransferReceiver(context, hostPort.first, hostPort.second)
                                                    } else {
                                                        Toast.makeText(context, "Nessun QR valido trovato nell'immagine", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Errore nella lettura dell'immagine: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Ricevi Diario di Viaggio 📥",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    when (val state = transferState) {
                                        is TransferState.Idle -> {
                                            // Options: Scan QR, pick from gallery, or manual entry
                                            Text(
                                                text = "Inquadra il QR code del mittente o inserisci l'indirizzo IP indicato sull'altro telefono",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )

                                            if (cameraPermissionState.status.isGranted) {
                                                // Real Camera Scanner Preview
                                                Box(
                                                    modifier = Modifier
                                                        .size(240.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                                                ) {
                                                    CameraPreviewWithScanner { scannedText ->
                                                        val hostPort = DataTransferManager.parseQrPayload(scannedText)
                                                        if (hostPort != null) {
                                                            viewModel.startTransferReceiver(context, hostPort.first, hostPort.second)
                                                        } else {
                                                            Log.d("SCANNER", "QR code non valido per il trasferimento: $scannedText")
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Permission Request Card
                                                Card(
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f)),
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(16.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                                        Text(
                                                            text = "Abilita Fotocamera per Scansione QR",
                                                            fontWeight = FontWeight.Bold,
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Button(
                                                            onClick = { cameraPermissionState.launchPermissionRequest() }
                                                        ) {
                                                            Text("Consenti Accesso")
                                                        }
                                                    }
                                                }
                                            }

                                            // Option to pick QR code from gallery
                                            OutlinedButton(
                                                onClick = {
                                                    qrImagePickerLauncher.launch(
                                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                    )
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.PhotoLibrary,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Carica QR da Galleria / Screenshot", fontSize = 13.sp)
                                            }

                                            Text(
                                                text = "OPPURE INSERISCI CODICE MANUALE",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Black,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )

                                            OutlinedTextField(
                                                value = manualIp,
                                                onValueChange = { manualIp = it },
                                                placeholder = { Text("Esempio: 192.168.1.104:8888") },
                                                label = { Text("Indirizzo IP : Porta") },
                                                singleLine = true,
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                                                trailingIcon = {
                                                    IconButton(
                                                        onClick = {
                                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                            val clipText = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                                            if (!clipText.isNullOrBlank()) {
                                                                manualIp = clipText.trim()
                                                                Toast.makeText(context, "Incollato!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.ContentPaste,
                                                            contentDescription = "Incolla dagli appunti"
                                                        )
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("manual_ip_input")
                                            )

                                            Button(
                                                onClick = {
                                                    val hostPort = DataTransferManager.parseQrPayload(manualIp)
                                                    if (hostPort != null) {
                                                        viewModel.startTransferReceiver(context, hostPort.first, hostPort.second)
                                                    } else {
                                                        Toast.makeText(context, "Inserisci un indirizzo IP valido (es. 192.168.1.5:8888)", Toast.LENGTH_LONG).show()
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .testTag("connect_manual_button"),
                                                enabled = manualIp.isNotBlank()
                                            ) {
                                                Icon(imageVector = Icons.Default.Cable, contentDescription = null)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Connetti e Ricevi")
                                            }
                                        }
                                        is TransferState.Connecting -> {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(260.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                                    CircularProgressIndicator(modifier = Modifier.size(60.dp))
                                                    Text(
                                                        text = "Connessione in corso...",
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = "Connessione al telefono mittente...",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                        is TransferState.Transferring -> {
                                            TransferringAnimationView(
                                                state = state,
                                                isSender = false,
                                                onCancel = { viewModel.resetTransferState() }
                                            )
                                        }
                                        is TransferState.Success -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.padding(vertical = 24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Text(
                                                    text = "Ricezione Completata! 🎉",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF2E7D32)
                                                )
                                                Text(
                                                    text = state.message,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                if (state.totalMedia > 0 || state.totalRegions > 0) {
                                                    Card(
                                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                                        shape = RoundedCornerShape(12.dp),
                                                        modifier = Modifier.fillMaxWidth()
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                                                            horizontalArrangement = Arrangement.SpaceAround
                                                        ) {
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text("Luoghi Ricevuti", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                                Text("${state.totalRegions}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                                            }
                                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                Text("Media HD Ricevuti", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                                                Text("${state.totalMedia}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                                                            }
                                                        }
                                                    }
                                                }

                                                Button(
                                                    onClick = onDismissRequest,
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                                ) {
                                                    Text("Fantastico!", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        is TransferState.Error -> {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                                modifier = Modifier.padding(vertical = 24.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Error,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.size(80.dp)
                                                )
                                                Text(
                                                    text = "Errore Ricezione",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                                Text(
                                                    text = state.error,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    textAlign = TextAlign.Center,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                Button(
                                                    onClick = {
                                                        viewModel.resetTransferState()
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                                ) {
                                                    Text("Riprova", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        else -> {
                                            // Handle cases like TransferState.Listening if they occur unexpectedly
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class TransferMode {
    SENDER,
    RECEIVER
}

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val kb = bytes / 1024.0
    val mb = kb / 1024.0
    val gb = mb / 1024.0
    return when {
        gb >= 1.0 -> String.format(java.util.Locale.US, "%.2f GB", gb)
        mb >= 1.0 -> String.format(java.util.Locale.US, "%.1f MB", mb)
        kb >= 1.0 -> String.format(java.util.Locale.US, "%.0f KB", kb)
        else -> "$bytes B"
    }
}

/**
 * Animated view displaying live P2P transfer between two phones with wave effects,
 * bit-by-bit original quality progress indicator, file counts and byte counters.
 */
@Composable
fun TransferringAnimationView(
    state: TransferState.Transferring,
    isSender: Boolean,
    onCancel: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "p2p_transfer_pulse")
    val dotOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "transfer_flow_dot"
    )
    val phonePulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "transfer_phone_pulse"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = state.progress,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "transfer_progress_smooth"
    )

    val isVideoFile = state.currentFileName.contains(".mp4", ignoreCase = true) ||
            state.currentFileName.contains(".mov", ignoreCase = true) ||
            state.currentFileName.contains(".mkv", ignoreCase = true) ||
            state.currentFileName.contains("video", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High Quality Tag Badge
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.12f),
            border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HighQuality,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Qualità Originale al 100% (Senza Compressione)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }

        // Two Devices with Animated Stream in between
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Device A (Sender)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSender) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (isSender) MaterialTheme.colorScheme.primary else Color.Transparent
                ),
                modifier = Modifier
                    .width(96.dp)
                    .then(if (isSender) Modifier.scale(phonePulse) else Modifier)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = if (isSender) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = if (isSender) "Questo Tel." else "Telefono",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSender) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "INVIO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSender) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Animated Transfer Wave Between the Devices
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Moving particles beam
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val phase = (dotOffset + index * 0.2f) % 1f
                        val alpha = (1f - kotlin.math.abs(phase - 0.5f) * 2f).coerceIn(0.2f, 1f)
                        Box(
                            modifier = Modifier
                                .size((6 + phase * 6).dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF27D26).copy(alpha = alpha))
                        )
                    }
                }

                // Central Floating Icon representing media in flight
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isVideoFile) Icons.Default.Videocam else Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Device B (Receiver)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (!isSender) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = BorderStroke(
                    1.5.dp,
                    if (!isSender) MaterialTheme.colorScheme.secondary else Color.Transparent
                ),
                modifier = Modifier
                    .width(96.dp)
                    .then(if (!isSender) Modifier.scale(phonePulse) else Modifier)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = if (!isSender) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = if (!isSender) "Questo Tel." else "Altro Tel.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (!isSender) MaterialTheme.colorScheme.secondary else Color.Gray.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "RICEZIONE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (!isSender) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Live Transfer Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isVideoFile) Color(0xFFF97316).copy(alpha = 0.2f)
                                    else Color(0xFF0284C7).copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isVideoFile) Icons.Default.Videocam else Icons.Default.PhotoCamera,
                                contentDescription = null,
                                tint = if (isVideoFile) Color(0xFFEA580C) else Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.currentFileName.isNotBlank()) state.currentFileName else "Preparazione file...",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isVideoFile) "Video Originale HD" else if (state.totalFiles > 0) "Foto / Documento" else "Database di viaggio",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.totalFiles > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${state.currentFile} / ${state.totalFiles}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Smooth Progress Bar
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
                )

                // Detailed Progress Numbers
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${formatBytes(state.bytesTransferred)} di ${formatBytes(state.totalBytes)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(state.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Informational Note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE65100).copy(alpha = 0.08f))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "Tutti i video e le foto vengono trasferiti bit per bit senza compressione. Mantieni l'app aperta finché l'operazione non è terminata.",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(0xFFE65100)
            )
        }

        // Cancel / Abort Button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Interrompi Trasferimento", fontWeight = FontWeight.SemiBold)
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun CameraPreviewWithScanner(
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) {
        var ctx = context
        while (ctx is android.content.ContextWrapper) {
            if (ctx is androidx.activity.ComponentActivity) return@remember ctx
            ctx = ctx.baseContext
        }
        null
    }
    val lifecycleOwner = activity ?: LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var hasDetected by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            try {
                cameraProviderFuture.get()?.unbindAll()
            } catch (ignored: Exception) {}
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }

                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().apply {
                            setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                            .build()
                            .also { analysis ->
                                analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                    if (hasDetected) {
                                        imageProxy.close()
                                        return@setAnalyzer
                                    }
                                    processImageProxy(imageProxy) { scannedText ->
                                        if (!hasDetected) {
                                            hasDetected = true
                                            ContextCompat.getMainExecutor(context).execute {
                                                onQrCodeScanned(scannedText)
                                            }
                                        }
                                    }
                                }
                            }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Impossibile avviare CameraX", e)
                    }
                }, ContextCompat.getMainExecutor(context))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Animated Scanning Laser Bar
        val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
        val laserProgress by infiniteTransition.animateFloat(
            initialValue = 0.05f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "laser_progress"
        )

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val h = maxHeight
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .offset(y = h * laserProgress)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF00E676),
                                MaterialTheme.colorScheme.primary,
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}

// Extract ZXing logic from imageProxy frames safely with correct orientation and format
fun processImageProxy(image: ImageProxy, onQrCodeScanned: (String) -> Unit) {
    try {
        val bitmap = image.toBitmap()
        val rotationDegrees = image.imageInfo.rotationDegrees
        val oriented = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }

        val qrText = DataTransferManager.decodeQrFromBitmap(oriented)
        if (!qrText.isNullOrBlank()) {
            onQrCodeScanned(qrText)
        }
    } catch (e: Exception) {
        // Ignored frame exceptions
    } finally {
        image.close()
    }
}
