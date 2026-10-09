package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiRepository
import com.example.data.database.TravelRepository
import com.example.data.database.VisitedRegionEntity
import com.example.data.model.MapData
import com.example.utils.PdfExporter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TravelViewModel(private val repository: TravelRepository) : ViewModel() {

    // Reactive list of visited regions from database
    val visitedRegions: StateFlow<List<VisitedRegionEntity>> = repository.getAllVisitedRegionsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current selected country
    private val _selectedCountryId = MutableStateFlow<String?>(null)
    val selectedCountryId: StateFlow<String?> = _selectedCountryId.asStateFlow()

    // AI Recommendation Wizard state
    private val _aiRecommendation = MutableStateFlow<String?>(null)
    val aiRecommendation: StateFlow<String?> = _aiRecommendation.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    // Cloud Save Sync Simulator state
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<String?>("Nessun salvataggio cloud effettuato")
    val lastSyncTime: StateFlow<String?> = _lastSyncTime.asStateFlow()

    fun selectCountry(countryId: String?) {
        _selectedCountryId.value = countryId
    }

    fun toggleRegionVisited(countryId: String, regionId: String) {
        viewModelScope.launch {
            val currentList = visitedRegions.value
            val compositeId = "${countryId}_${regionId}"
            val existing = currentList.find { it.id == compositeId || (it.countryId == countryId && it.regionId == regionId) }

            if (existing != null) {
                // If it exists, toggle visited. If they turn it off, we can either delete or set isVisited = false
                if (existing.isVisited) {
                    // Turn off visited, but keep notes if they exist, or delete if empty
                    if (existing.notes.isBlank() && existing.photoUris.isBlank()) {
                        repository.deleteRegion(existing.id)
                    } else {
                        repository.insertOrUpdate(existing.copy(isVisited = false))
                    }
                } else {
                    repository.insertOrUpdate(existing.copy(isVisited = true))
                }
            } else {
                // Create new visited region
                val newEntity = VisitedRegionEntity(
                    id = compositeId,
                    countryId = countryId,
                    regionId = regionId,
                    isVisited = true,
                    notes = "",
                    photoUris = ""
                )
                repository.insertOrUpdate(newEntity)
            }
        }
    }

    fun saveRegionDetails(countryId: String, regionId: String, notes: String, photoUrisList: List<String>, rating: Int? = null) {
        viewModelScope.launch {
            val compositeId = "${countryId}_${regionId}"
            val photoUrisStr = photoUrisList.filter { it.isNotBlank() }.joinToString(",")
            val currentList = visitedRegions.value
            val existing = currentList.find { it.id == compositeId || (it.countryId == countryId && it.regionId == regionId) }

            if (existing != null) {
                repository.insertOrUpdate(
                    existing.copy(
                        notes = notes,
                        photoUris = photoUrisStr,
                        rating = rating ?: existing.rating
                    )
                )
            } else {
                repository.insertOrUpdate(
                    VisitedRegionEntity(
                        id = compositeId,
                        countryId = countryId,
                        regionId = regionId,
                        isVisited = false, // notes can be added before marking as visited
                        notes = notes,
                        photoUris = photoUrisStr,
                        rating = rating ?: 0
                    )
                )
            }
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            // Simulate cloud connection & encryption & sync
            kotlinx.coroutines.delay(1800)
            val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
            _lastSyncTime.value = "Sincronizzato il: " + formatter.format(java.util.Date())
            _isSyncing.value = false
        }
    }

    fun submitAiQuiz(answers: List<String>) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiRecommendation.value = null

            // Construct an exceptionally helpful, localized travel prompt for Gemini
            val travelStyle = answers.getOrNull(0) ?: "Qualsiasi"
            val budget = answers.getOrNull(1) ?: "Qualsiasi"
            val climate = answers.getOrNull(2) ?: "Qualsiasi"
            val companions = answers.getOrNull(3) ?: "Qualsiasi"

            val prompt = """
                Sei un esperto consulente di viaggio ed assistente turistico AI.
                In base alle risposte dell'utente a questo breve quiz, consiglia la destinazione ideale per il suo prossimo viaggio e descrivi un itinerario di 3 giorni consigliato:
                1. Stile di viaggio preferito: $travelStyle
                2. Budget stimato: $budget
                3. Clima ideale: $climate
                4. Compagni di viaggio: $companions

                Fornisci la risposta interamente in lingua italiana, strutturata in modo elegante con sezioni leggibili (Destinazione, Perché andarci, Itinerario di 3 Giorni, Consigli pratici).
                Usa emoji e un tono caloroso ed entusiasmante.
            """.trimIndent()

            val recommendation = GeminiRepository.getTravelRecommendation(prompt)
            _aiRecommendation.value = recommendation
            _isAiLoading.value = false
        }
    }

    fun resetAiQuiz() {
        _aiRecommendation.value = null
        _isAiLoading.value = false
    }

    fun askCustomAiQuestion(question: String) {
        if (question.isBlank()) return
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiRecommendation.value = null

            val prompt = """
                Sei un esperto consulente di viaggio ed assistente turistico personale.
                Rispondi con grande precisione, entusiasmo e consigli pratici alla seguente domanda di viaggio:
                "$question"

                Fornisci la risposta interamente in italiano, strutturata con titoli chiari, consigli pratici, mete imperdibili, specialità culinarie e suggerimenti di viaggio.
                Usa emoji e formatta il testo per una lettura agevole e piacevole su smartphone.
            """.trimIndent()

            val recommendation = GeminiRepository.getTravelRecommendation(prompt)
            _aiRecommendation.value = recommendation
            _isAiLoading.value = false
        }
    }

    fun exportToPdf(context: Context) {
        // Only export visited regions
        val visitedOnly = visitedRegions.value.filter { it.isVisited }
        PdfExporter.exportTravelsToPdf(context, visitedOnly)
    }

    // P2P Data Transfer Methods
    val transferState = com.example.utils.DataTransferManager.transferState
    private var senderJob: Job? = null
    private var receiverJob: Job? = null

    fun startTransferSender(context: Context, onFinished: () -> Unit = {}) {
        senderJob?.cancel()
        senderJob = viewModelScope.launch {
            val regions = repository.getAllVisitedRegions()
            com.example.utils.DataTransferManager.startSenderServer(context, regions, onFinished)
        }
    }

    fun startTransferReceiver(context: Context, ip: String, port: Int) {
        val current = com.example.utils.DataTransferManager.transferState.value
        if (current is com.example.utils.DataTransferManager.TransferState.Connecting ||
            current is com.example.utils.DataTransferManager.TransferState.Transferring) {
            return
        }
        receiverJob?.cancel()
        receiverJob = viewModelScope.launch {
            com.example.utils.DataTransferManager.startReceiverClient(context, ip, port) { receivedRegions ->
                // Save imported data: insert or update
                for (r in receivedRegions) {
                    repository.insertOrUpdate(r)
                }
            }
        }
    }

    fun resetTransferState() {
        senderJob?.cancel()
        receiverJob?.cancel()
        senderJob = null
        receiverJob = null
        com.example.utils.DataTransferManager.resetState()
    }
}

class TravelViewModelFactory(private val repository: TravelRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TravelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TravelViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
