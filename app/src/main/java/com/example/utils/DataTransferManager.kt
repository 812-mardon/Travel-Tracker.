package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import com.example.data.database.VisitedRegionEntity
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.GlobalHistogramBinarizer
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Collections

object DataTransferManager {
    private const val TAG = "DataTransferManager"
    const val DEFAULT_PORT = 8888
    private const val QR_PREFIX = "TRAVEL_TRACKER_P2P:"
    private const val PROTOCOL_MAGIC_V2 = "TRAVEL_TRACKER_TRANSFER_V2"
    private const val PROTOCOL_END_MARKER = "TRANSFER_END_SUCCESS"
    private const val ACK_SUCCESS = "RECEIVER_ACK_SUCCESS"
    private const val ACK_ERR_PREFIX = "RECEIVER_ERR:"

    // UI state for transmission with live progress and file details
    sealed class TransferState {
        object Idle : TransferState()
        data class Listening(val ip: String, val port: Int) : TransferState()
        object Connecting : TransferState()
        data class Transferring(
            val currentFile: Int = 0,
            val totalFiles: Int = 0,
            val currentFileName: String = "",
            val bytesTransferred: Long = 0L,
            val totalBytes: Long = 0L,
            val progress: Float = 0f,
            val isSending: Boolean = true
        ) : TransferState()
        data class Success(
            val message: String,
            val totalRegions: Int = 0,
            val totalMedia: Int = 0
        ) : TransferState()
        data class Error(val error: String) : TransferState()
    }

    private val _transferState = MutableStateFlow<TransferState>(TransferState.Idle)
    val transferState = _transferState.asStateFlow()

    @Volatile
    private var serverSocket: ServerSocket? = null
    @Volatile
    private var clientSocket: Socket? = null

    data class RegionTransferDto(
        val id: String,
        val countryId: String,
        val regionId: String,
        val isVisited: Boolean,
        val notes: String,
        val rating: Int,
        val mediaRelativePaths: List<String>
    )

    data class MediaFileManifest(
        val relativePath: String,
        val fileSize: Long,
        val localFile: File
    )

    /**
     * Retrieves all candidate local IPv4 addresses, strictly prioritizing Wi-Fi / Hotspot / LAN
     * and filtering out cellular/mobile data interfaces (which are unreachable by local devices).
     */
    fun getLocalIpAddresses(context: Context? = null): List<String> {
        val results = linkedSetOf<String>()

        // 1. If context available, query active Wi-Fi IP via WifiManager
        if (context != null) {
            try {
                val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
                val ipInt = wifiMgr?.connectionInfo?.ipAddress ?: 0
                if (ipInt != 0) {
                    val ipStr = String.format(
                        java.util.Locale.US,
                        "%d.%d.%d.%d",
                        ipInt and 0xff,
                        ipInt shr 8 and 0xff,
                        ipInt shr 16 and 0xff,
                        ipInt shr 24 and 0xff
                    )
                    if (ipStr != "0.0.0.0" && ipStr != "127.0.0.1") {
                        results.add(ipStr)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Impossibile leggere WifiManager: ${e.message}")
            }
        }

        // 2. Scan NetworkInterfaces prioritizing Wi-Fi and Local Hotspot / AP
        try {
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())

            // Sort: wlan*, ap*, softap*, p2p* highest priority (3), eth* (2), others (1), cellular (0)
            val sorted = interfaces.filter { it.isUp && !it.isLoopback }.sortedByDescending { intf ->
                val name = intf.name.lowercase()
                when {
                    name.startsWith("wlan") || name.startsWith("ap") ||
                            name.startsWith("softap") || name.startsWith("p2p") ||
                            name.startsWith("wifi") || name.startsWith("swlan") -> 3
                    name.startsWith("eth") || name.startsWith("en") -> 2
                    name.startsWith("rmnet") || name.startsWith("ccmni") ||
                            name.startsWith("dummy") || name.startsWith("tun") ||
                            name.startsWith("sit") || name.startsWith("pdp") -> 0
                    else -> 1
                }
            }

            for (intf in sorted) {
                val name = intf.name.lowercase()
                val isCellular = name.startsWith("rmnet") || name.startsWith("ccmni") ||
                        name.startsWith("dummy") || name.startsWith("tun") ||
                        name.startsWith("sit") || name.startsWith("pdp")

                if (isCellular) continue // Skip cellular carrier private IPs

                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val host = addr.hostAddress ?: continue
                        val isIPv4 = host.indexOf(':') < 0
                        if (isIPv4 && host != "127.0.0.1") {
                            results.add(host)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Errore scansione interfacce di rete", ex)
        }

        // If no Wi-Fi interface was found, try any non-loopback IPv4
        if (results.isEmpty()) {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    if (!intf.isLoopback && intf.isUp) {
                        for (addr in Collections.list(intf.inetAddresses)) {
                            val host = addr.hostAddress ?: continue
                            if (!addr.isLoopbackAddress && host.indexOf(':') < 0 && host != "127.0.0.1") {
                                results.add(host)
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {}
        }

        if (results.isEmpty()) {
            results.add("127.0.0.1")
        }
        return results.toList()
    }

    /**
     * Returns the primary local IP address to bind and display.
     */
    fun getLocalIpAddress(context: Context? = null): String {
        return getLocalIpAddresses(context).firstOrNull() ?: "127.0.0.1"
    }

    /**
     * Scans regions and maps all referenced local photo and video files for original-quality transfer.
     */
    fun collectMediaAndRegions(
        context: Context,
        regions: List<VisitedRegionEntity>
    ): Pair<List<RegionTransferDto>, List<MediaFileManifest>> {
        val manifestList = mutableListOf<MediaFileManifest>()
        val seenLocalPaths = mutableSetOf<String>()
        val regionDtos = mutableListOf<RegionTransferDto>()

        for (region in regions) {
            val relPathsForRegion = mutableListOf<String>()
            val uris = region.photoUris.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            for (uriStr in uris) {
                val file = when {
                    uriStr.startsWith("file://") -> File(uriStr.removePrefix("file://"))
                    uriStr.startsWith("/") -> File(uriStr)
                    else -> null
                }

                if (file != null && file.exists() && file.isFile) {
                    val relPath = if (file.absolutePath.startsWith(context.filesDir.absolutePath)) {
                        file.relativeTo(context.filesDir).path
                    } else {
                        val safeCountry = region.countryId.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
                        val safeRegion = region.regionId.replace("[^a-zA-Z0-9_-]".toRegex(), "_")
                        "travel_media/$safeCountry/$safeRegion/${file.name}"
                    }

                    relPathsForRegion.add(relPath)
                    if (seenLocalPaths.add(file.absolutePath)) {
                        manifestList.add(MediaFileManifest(relPath, file.length(), file))
                    }
                } else {
                    // Non-local URI (e.g. web url)
                    relPathsForRegion.add(uriStr)
                }
            }

            regionDtos.add(
                RegionTransferDto(
                    id = region.id,
                    countryId = region.countryId,
                    regionId = region.regionId,
                    isVisited = region.isVisited,
                    notes = region.notes,
                    rating = region.rating,
                    mediaRelativePaths = relPathsForRegion
                )
            )
        }

        return Pair(regionDtos, manifestList)
    }

    // Serialize region DTOs to JSON
    fun serializeRegionDtos(regionDtos: List<RegionTransferDto>): String {
        val array = JSONArray()
        for (dto in regionDtos) {
            val obj = JSONObject().apply {
                put("id", dto.id)
                put("countryId", dto.countryId)
                put("regionId", dto.regionId)
                put("isVisited", dto.isVisited)
                put("notes", dto.notes)
                put("rating", dto.rating)
                val mediaArr = JSONArray()
                dto.mediaRelativePaths.forEach { mediaArr.put(it) }
                put("mediaPaths", mediaArr)
            }
            array.put(obj)
        }
        return array.toString()
    }

    // Deserialize JSON to region DTOs
    fun deserializeRegionDtos(jsonStr: String): List<RegionTransferDto> {
        val list = mutableListOf<RegionTransferDto>()
        val array = JSONArray(jsonStr)
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val mediaPathsList = mutableListOf<String>()
            val mediaArr = obj.optJSONArray("mediaPaths")
            if (mediaArr != null) {
                for (j in 0 until mediaArr.length()) {
                    mediaPathsList.add(mediaArr.getString(j))
                }
            } else {
                val oldUris = obj.optString("photoUris", "")
                if (oldUris.isNotBlank()) {
                    mediaPathsList.addAll(oldUris.split(",").filter { it.isNotBlank() })
                }
            }

            list.add(
                RegionTransferDto(
                    id = obj.getString("id"),
                    countryId = obj.getString("countryId"),
                    regionId = obj.getString("regionId"),
                    isVisited = obj.getBoolean("isVisited"),
                    notes = obj.optString("notes", ""),
                    rating = obj.optInt("rating", 0),
                    mediaRelativePaths = mediaPathsList
                )
            )
        }
        return list
    }

    // Generate a QR Code bitmap from text
    fun generateQrCode(text: String, size: Int = 450): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Errore nella generazione del QR Code", e)
            null
        }
    }

    // Format connection parameters as QR payload
    fun buildQrPayload(ip: String, port: Int): String {
        return "$QR_PREFIX$ip:$port"
    }

    // Parse QR payload or manual string to return Pair(IP, Port) or null if invalid
    fun parseQrPayload(payload: String): Pair<String, Int>? {
        var clean = payload.trim()
        if (clean.startsWith(QR_PREFIX)) {
            clean = clean.substring(QR_PREFIX.length).trim()
        }
        clean = clean.removePrefix("http://").removePrefix("https://").trim('/')

        val colonIndex = clean.lastIndexOf(':')
        if (colonIndex > 0 && colonIndex < clean.length - 1) {
            val ipPart = clean.substring(0, colonIndex).trim()
            val portPart = clean.substring(colonIndex + 1).trim()
            val port = portPart.toIntOrNull()
            if (port != null && ipPart.isNotBlank()) {
                return Pair(ipPart, port)
            }
        } else if (clean.isNotBlank()) {
            // Default port fallback
            return Pair(clean, DEFAULT_PORT)
        }
        return null
    }

    /**
     * Start Server Socket (Sender): transmits regions database and all photos/videos
     * at 100% original quality in raw binary format.
     * CRITICAL: Uses two-way handshake; only reports Success when the Receiver confirms
     * full receipt and disk commitment via ACK.
     */
    suspend fun startSenderServer(
        context: Context,
        regions: List<VisitedRegionEntity>,
        onFinished: () -> Unit = {}
    ) {
        withContext(Dispatchers.IO) {
            var client: Socket? = null
            try {
                // Safely close any previous server socket
                try {
                    serverSocket?.close()
                } catch (ignored: Exception) {}
                serverSocket = null

                val ip = getLocalIpAddress(context)

                // Try binding to DEFAULT_PORT first, or fallback ports if already in TIME_WAIT
                var boundPort = DEFAULT_PORT
                var boundSocket: ServerSocket? = null
                val candidatePorts = listOf(DEFAULT_PORT, 8889, 8890, 8891, 0)

                for (port in candidatePorts) {
                    try {
                        val ss = ServerSocket()
                        ss.reuseAddress = true
                        ss.soTimeout = 180000 // 3 minutes waiting for receiver connection
                        ss.bind(InetSocketAddress(port))
                        boundSocket = ss
                        boundPort = ss.localPort
                        break
                    } catch (e: Exception) {
                        Log.w(TAG, "Porta $port non disponibile, provo successiva...")
                    }
                }

                if (boundSocket == null) {
                    throw IOException("Impossibile avviare il server di trasferimento su nessuna porta disponibile.")
                }

                serverSocket = boundSocket
                _transferState.value = TransferState.Listening(ip, boundPort)
                Log.d(TAG, "Server socket in ascolto su $ip:$boundPort")

                // Wait for receiver connection
                val incomingClient = boundSocket.accept()
                client = incomingClient
                incomingClient.soTimeout = 0 // No timeout during active streaming
                incomingClient.tcpNoDelay = true

                _transferState.value = TransferState.Transferring(
                    currentFile = 0,
                    totalFiles = 0,
                    currentFileName = "Preparazione dati in qualità originale...",
                    bytesTransferred = 0L,
                    totalBytes = 0L,
                    progress = 0f,
                    isSending = true
                )

                val (regionDtos, mediaManifest) = collectMediaAndRegions(context, regions)
                val totalBytes = mediaManifest.sumOf { it.fileSize }
                var bytesSentSoFar = 0L

                val outStream = DataOutputStream(BufferedOutputStream(incomingClient.getOutputStream(), 65536))

                // 1. Magic Protocol Header
                outStream.writeUTF(PROTOCOL_MAGIC_V2)

                // 2. Regions Database JSON
                val regionsJson = serializeRegionDtos(regionDtos)
                val jsonBytes = regionsJson.toByteArray(Charsets.UTF_8)
                outStream.writeInt(jsonBytes.size)
                outStream.write(jsonBytes)

                // 3. Media Files Total Count and Size
                outStream.writeInt(mediaManifest.size)
                outStream.writeLong(totalBytes)
                outStream.flush()

                // 4. Stream each Photo and Video bit-by-bit in 100% Original Quality
                val buffer = ByteArray(65536)
                mediaManifest.forEachIndexed { index, mediaItem ->
                    outStream.writeUTF(mediaItem.relativePath)
                    outStream.writeLong(mediaItem.fileSize)

                    val fileIn = FileInputStream(mediaItem.localFile).buffered(65536)
                    var bytesRead: Int
                    try {
                        while (fileIn.read(buffer).also { bytesRead = it } != -1) {
                            outStream.write(buffer, 0, bytesRead)
                            bytesSentSoFar += bytesRead

                            val prog = if (totalBytes > 0) {
                                (bytesSentSoFar.toFloat() / totalBytes).coerceIn(0f, 1f)
                            } else {
                                1f
                            }

                            _transferState.value = TransferState.Transferring(
                                currentFile = index + 1,
                                totalFiles = mediaManifest.size,
                                currentFileName = mediaItem.localFile.name,
                                bytesTransferred = bytesSentSoFar,
                                totalBytes = totalBytes,
                                progress = prog,
                                isSending = true
                            )
                        }
                    } finally {
                        fileIn.close()
                    }
                    outStream.flush()
                }

                // 5. Completion Marker
                outStream.writeUTF(PROTOCOL_END_MARKER)
                outStream.flush()

                _transferState.value = TransferState.Transferring(
                    currentFile = mediaManifest.size,
                    totalFiles = mediaManifest.size,
                    currentFileName = "In attesa di conferma dal ricevente...",
                    bytesTransferred = totalBytes,
                    totalBytes = totalBytes,
                    progress = 1f,
                    isSending = true
                )

                // 6. CRITICAL TWO-WAY HANDSHAKE: Wait for Receiver's Confirmation ACK!
                val inAckStream = DataInputStream(BufferedInputStream(incomingClient.getInputStream(), 4096))
                incomingClient.soTimeout = 45000 // 45s for receiver to write files to disk and DB
                val ackResponse = inAckStream.readUTF()

                if (ackResponse == ACK_SUCCESS) {
                    _transferState.value = TransferState.Success(
                        message = "Trasferiti con successo ${regionDtos.size} luoghi e ${mediaManifest.size} file multimediali a risoluzione originale!",
                        totalRegions = regionDtos.size,
                        totalMedia = mediaManifest.size
                    )
                    onFinished()
                } else {
                    val errDetail = if (ackResponse.startsWith(ACK_ERR_PREFIX)) {
                        ackResponse.substring(ACK_ERR_PREFIX.length)
                    } else {
                        ackResponse
                    }
                    throw IOException("Il ricevente ha riscontrato un errore: $errDetail")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante l'invio dei dati", e)
                _transferState.value = TransferState.Error(
                    "Connessione o trasferimento non riuscito: ${e.localizedMessage ?: "Errore sconosciuto"}"
                )
            } finally {
                try {
                    client?.close()
                } catch (ignored: Exception) {}
                try {
                    serverSocket?.close()
                } catch (ignored: Exception) {}
                serverSocket = null
            }
        }
    }

    /**
     * Start Client Socket (Receiver): receives regions database and saves all photos/videos
     * in local private storage in original full quality.
     * Confirms successful write by sending ACK_SUCCESS back to the sender.
     */
    suspend fun startReceiverClient(
        context: Context,
        ip: String,
        port: Int,
        onDataReceived: suspend (List<VisitedRegionEntity>) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                // Reset any existing client socket
                try {
                    clientSocket?.close()
                } catch (ignored: Exception) {}
                clientSocket = null

                _transferState.value = TransferState.Connecting
                Log.d(TAG, "Tentativo di connessione a $ip:$port")

                val sock = Socket()
                sock.tcpNoDelay = true
                // Connect with 20 seconds timeout
                sock.connect(InetSocketAddress(ip, port), 20000)
                clientSocket = sock
                sock.soTimeout = 0 // No timeout during large file streaming

                _transferState.value = TransferState.Transferring(
                    currentFile = 0,
                    totalFiles = 0,
                    currentFileName = "Connesso. Avvio ricezione dati...",
                    bytesTransferred = 0L,
                    totalBytes = 0L,
                    progress = 0f,
                    isSending = false
                )

                val inStream = DataInputStream(BufferedInputStream(sock.getInputStream(), 65536))
                val outAckStream = DataOutputStream(BufferedOutputStream(sock.getOutputStream(), 4096))

                val header = inStream.readUTF()
                if (header != PROTOCOL_MAGIC_V2) {
                    throw IOException("Versione protocollo non compatibile: $header")
                }

                // 1. Read Regions JSON
                val jsonLength = inStream.readInt()
                val jsonBytes = ByteArray(jsonLength)
                inStream.readFully(jsonBytes)
                val regionsJson = String(jsonBytes, Charsets.UTF_8)
                val regionDtos = deserializeRegionDtos(regionsJson)

                // 2. Read Media Manifest stats
                val mediaCount = inStream.readInt()
                val totalBytes = inStream.readLong()

                var bytesReceivedSoFar = 0L
                val buffer = ByteArray(65536)

                // 3. Receive each photo and video in raw binary format
                for (fileIdx in 0 until mediaCount) {
                    val relativePath = inStream.readUTF()
                    val fileSize = inStream.readLong()

                    val destFile = File(context.filesDir, relativePath)
                    destFile.parentFile?.mkdirs()

                    val fileOut = FileOutputStream(destFile).buffered(65536)
                    var remaining = fileSize
                    try {
                        while (remaining > 0) {
                            val toRead = minOf(buffer.size.toLong(), remaining).toInt()
                            val read = inStream.read(buffer, 0, toRead)
                            if (read == -1) {
                                throw EOFException("Connessione interrotta durante il download di $relativePath")
                            }
                            fileOut.write(buffer, 0, read)
                            remaining -= read
                            bytesReceivedSoFar += read

                            val prog = if (totalBytes > 0) {
                                (bytesReceivedSoFar.toFloat() / totalBytes).coerceIn(0f, 1f)
                            } else {
                                1f
                            }

                            _transferState.value = TransferState.Transferring(
                                currentFile = fileIdx + 1,
                                totalFiles = mediaCount,
                                currentFileName = destFile.name,
                                bytesTransferred = bytesReceivedSoFar,
                                totalBytes = totalBytes,
                                progress = prog,
                                isSending = false
                            )
                        }
                        fileOut.flush()
                    } finally {
                        fileOut.close()
                    }
                }

                // 4. Verify completion marker
                val endMarker = inStream.readUTF()
                if (endMarker != PROTOCOL_END_MARKER) {
                    throw IOException("Conferma di fine trasferimento mancante o non valida: $endMarker")
                }

                // 5. Convert DTOs to database entities with local file paths on receiver
                val entities = regionDtos.map { dto ->
                    val resolvedUris = dto.mediaRelativePaths.map { path ->
                        if (path.startsWith("travel_media") || path.startsWith("travel_photos")) {
                            File(context.filesDir, path).absolutePath
                        } else {
                            path
                        }
                    }

                    VisitedRegionEntity(
                        id = dto.id,
                        countryId = dto.countryId,
                        regionId = dto.regionId,
                        isVisited = dto.isVisited,
                        notes = dto.notes,
                        photoUris = resolvedUris.joinToString(","),
                        rating = dto.rating
                    )
                }

                // Save to database
                onDataReceived(entities)

                // 6. Send positive ACK to the sender
                try {
                    outAckStream.writeUTF(ACK_SUCCESS)
                    outAckStream.flush()
                    // Brief pause to allow OS socket to flush packet
                    Thread.sleep(400)
                } catch (e: Exception) {
                    Log.w(TAG, "Impossibile inviare ACK al mittente: ${e.message}")
                }

                _transferState.value = TransferState.Success(
                    message = "Ricevute ed archiviate ${entities.size} regioni e $mediaCount file multimediali a qualità originale!",
                    totalRegions = entities.size,
                    totalMedia = mediaCount
                )
            } catch (e: Exception) {
                Log.e(TAG, "Errore durante la ricezione dei dati", e)
                // Attempt to notify sender of failure
                try {
                    clientSocket?.let { sock ->
                        if (sock.isConnected && !sock.isClosed) {
                            val outAck = DataOutputStream(sock.getOutputStream())
                            outAck.writeUTF("$ACK_ERR_PREFIX${e.localizedMessage ?: "Errore ricezione"}")
                            outAck.flush()
                        }
                    }
                } catch (ignored: Exception) {}

                _transferState.value = TransferState.Error(
                    "Impossibile connettersi o ricevere i file: ${e.localizedMessage ?: "Verifica che entrambi i dispositivi siano sulla stessa rete Wi-Fi."}"
                )
            } finally {
                try {
                    clientSocket?.close()
                } catch (ignored: Exception) {}
                clientSocket = null
            }
        }
    }

    // Reset Transfer State and close connections
    fun resetState() {
        _transferState.value = TransferState.Idle
        try {
            serverSocket?.close()
        } catch (ignored: Exception) {}
        serverSocket = null

        try {
            clientSocket?.close()
        } catch (ignored: Exception) {}
        clientSocket = null
    }

    /**
     * Decode a QR code from a bitmap with multi-binarizer support, orientation handling,
     * and optimized hint settings for instant QR recognition.
     */
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            // Resize if bitmap is too large (> 720px) to boost performance and reduce memory
            val target = if (bitmap.width > 720 || bitmap.height > 720) {
                val factor = 720f / maxOf(bitmap.width, bitmap.height)
                Bitmap.createScaledBitmap(bitmap, (bitmap.width * factor).toInt(), (bitmap.height * factor).toInt(), true)
            } else {
                bitmap
            }

            val intArray = IntArray(target.width * target.height)
            target.getPixels(intArray, 0, target.width, 0, 0, target.width, target.height)
            val source = RGBLuminanceSource(target.width, target.height, intArray)

            val hints = mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.TRY_HARDER to java.lang.Boolean.TRUE,
                DecodeHintType.CHARACTER_SET to "UTF-8"
            )

            val reader = MultiFormatReader().apply { setHints(hints) }

            // 1. Try HybridBinarizer
            try {
                val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                val res = reader.decodeWithState(binaryBitmap)
                if (res != null && res.text.isNotBlank()) return res.text
            } catch (ignored: Exception) {}

            // 2. Try GlobalHistogramBinarizer
            try {
                reader.reset()
                val binaryBitmap2 = BinaryBitmap(GlobalHistogramBinarizer(source))
                val res2 = reader.decodeWithState(binaryBitmap2)
                if (res2 != null && res2.text.isNotBlank()) return res2.text
            } catch (ignored: Exception) {}

            null
        } catch (e: Exception) {
            null
        }
    }
}
