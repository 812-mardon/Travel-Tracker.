package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.database.VisitedRegionEntity
import com.example.data.model.Continent
import com.example.data.model.Country
import com.example.data.model.MapData
import com.example.data.model.Point
import com.example.ui.viewmodel.TravelViewModel
import com.example.utils.ImageStorageHelper

// Simple local routing representation
enum class MainTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MAPPA("Mappa", Icons.Default.Map),
    CONTINENTI("Nazioni", Icons.Default.Public),
    CONSIGLI("Consigli AI", Icons.AutoMirrored.Default.HelpOutline)
}

@Composable
fun MainScreen(viewModel: TravelViewModel) {
    val context = LocalContext.current
    val selectedCountryId by viewModel.selectedCountryId.collectAsStateWithLifecycle()
    val visitedRegions by viewModel.visitedRegions.collectAsStateWithLifecycle()
    val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
    val lastSyncTime by viewModel.lastSyncTime.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf(MainTab.MAPPA) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }

    if (showTransferDialog) {
        DataTransferDialog(
            viewModel = viewModel,
            onDismissRequest = { showTransferDialog = false }
        )
    }

    // If a country is selected, we display the detailed map of that country
    val selectedCountry = MapData.countries.find { it.id == selectedCountryId }

    // Beautiful High Density Profile Dialog
    if (showProfileDialog) {
        val totalVisited = visitedRegions.filter { it.isVisited }.size
        val visitedCountries = visitedRegions.filter { it.isVisited }.map { it.countryId }.distinct().size

        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = "Profilo Esploratore 🌍",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Nazioni", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("$visitedCountries", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Regioni", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                Text("$totalVisited", fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    Text(
                        text = "Travel Tracker sincronizza automaticamente il tuo diario di viaggio interattivo nel Cloud per conservare per sempre i tuoi itinerari, appunti e foto.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedButton(
                        onClick = {
                            showProfileDialog = false
                            showTransferDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_p2p_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHorizontalCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trasferisci Dati su Nuovo Telefono",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Chiudi", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                // Elegant layout resembling the HTML Tailwind header with safe status bar insets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedCountry != null) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { viewModel.selectCountry(null) }
                                .testTag("back_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Torna indietro",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = if (selectedCountry != null) "${selectedCountry.flag} ${selectedCountry.name}" else "Travel Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (selectedCountry != null) selectedCountry.continent.displayName.uppercase() else "REGIONI D'ITALIA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            letterSpacing = 1.sp
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cloud Status button
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { viewModel.triggerCloudSync() }
                            .testTag("cloud_sync_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CloudSync,
                                contentDescription = "Sincronizzazione Cloud",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Export PDF
                    val visitedOnly = visitedRegions.filter { it.isVisited }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (visitedOnly.isNotEmpty()) MaterialTheme.colorScheme.surfaceVariant 
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .clickable(enabled = visitedOnly.isNotEmpty()) { viewModel.exportToPdf(context) }
                            .testTag("export_pdf_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Esporta in PDF",
                            tint = if (visitedOnly.isNotEmpty()) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Quick country search pill at the top
            if (selectedCountry == null) {
                QuickCountrySearchPill(
                    visitedRegions = visitedRegions,
                    onCountrySelected = { countryId ->
                        viewModel.selectCountry(countryId)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    },
        bottomBar = {
            if (selectedCountry == null) {
                HighDensityBottomNav(
                    activeTab = activeTab,
                    onTabSelected = { activeTab = it },
                    onCloudClick = {
                        viewModel.triggerCloudSync()
                        Toast.makeText(context, "Sincronizzazione cloud avviata!", Toast.LENGTH_SHORT).show()
                    },
                    onProfileClick = {
                        showProfileDialog = true
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedCountry != null,
                transitionSpec = {
                    if (targetState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "ScreenTransition"
            ) { isDetail ->
                if (isDetail && selectedCountry != null) {
                    CountryMapScreen(
                        country = selectedCountry,
                        visitedRegions = visitedRegions.filter { it.countryId == selectedCountry.id },
                        viewModel = viewModel
                    )
                } else {
                    when (activeTab) {
                        MainTab.MAPPA -> WorldMapScreen(
                            visitedRegions = visitedRegions,
                            onCountrySelected = { countryId ->
                                viewModel.selectCountry(countryId)
                            },
                            onSyncTrigger = { viewModel.triggerCloudSync() },
                            lastSyncTime = lastSyncTime
                        )
                        MainTab.CONTINENTI -> ContinentsScreen(
                            visitedRegions = visitedRegions,
                            onCountrySelected = { countryId ->
                                viewModel.selectCountry(countryId)
                            }
                        )
                        MainTab.CONSIGLI -> AiRecommendationScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

// ------------------- TABS: 1. CONTINENTS LIST SCREEN -------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContinentsScreen(
    visitedRegions: List<VisitedRegionEntity>,
    onCountrySelected: (String) -> Unit
) {
    var expandedContinent by remember { mutableStateOf<Continent?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Esplora le Nazioni",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Seleziona una nazione divisa per continente per visualizzarne la mappa delle regioni",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Continent.values().forEach { continent ->
            val continentCountries = MapData.countries.filter { it.continent == continent }
            val isExpanded = expandedContinent == continent

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("continent_card_${continent.name.lowercase()}"),
                    onClick = {
                        expandedContinent = if (isExpanded) null else continent
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isExpanded) MaterialTheme.colorScheme.secondaryContainer 
                                         else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = continent.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${continentCountries.size} nazioni disponibili",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Riduci" else "Espandi"
                        )
                    }
                }
            }

            if (isExpanded) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        continentCountries.forEach { country ->
                            // Count visited regions
                            val countryVisitedCount = visitedRegions.filter { it.countryId == country.id && it.isVisited }.size
                            val totalRegions = country.regions.size

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCountrySelected(country.id) }
                                    .testTag("country_item_${country.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = country.flag,
                                            fontSize = 28.sp
                                        )
                                        Column {
                                            Text(
                                                text = country.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                text = "$totalRegions regioni amministrative",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Badge with visited count
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (countryVisitedCount > 0) Color(0xFFF27D26) 
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$countryVisitedCount / $totalRegions",
                                            color = if (countryVisitedCount > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
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


// ------------------- TABS: 2. WORLD MAP SCREEN (PLANISFERO) -------------------

data class DiaryMediaItem(
    val mediaList: List<String>,
    val initialIndex: Int,
    val regionName: String,
    val countryName: String
)

@Composable
fun WorldMapScreen(
    visitedRegions: List<VisitedRegionEntity>,
    onCountrySelected: (String) -> Unit,
    onSyncTrigger: () -> Unit,
    lastSyncTime: String?
) {
    val totalVisitedRegions = visitedRegions.filter { it.isVisited }.size
    val visitedCountries = visitedRegions.filter { it.isVisited }.map { it.countryId }.distinct().size

    val visitedOnly = visitedRegions.filter { it.isVisited }
    val visitedByCountry = visitedOnly.groupBy { it.countryId }

    var expandedCountries by remember { mutableStateOf(setOf<String>()) }
    var expandedMediaData by remember { mutableStateOf<DiaryMediaItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Summary Card with cloud status
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "I Tuoi Ricordi di Viaggio 🌍",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    IconButton(onClick = onSyncTrigger) {
                        Icon(
                            imageVector = Icons.Default.CloudQueue,
                            contentDescription = "Cloud Status",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Regioni Visitate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("$totalVisitedRegions", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Nazioni Esplorate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("$visitedCountries", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lastSyncTime ?: "Cloud non sincronizzato",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (visitedByCountry.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🗺️",
                        fontSize = 56.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Nessuna visita registrata",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Inizia il tuo viaggio! Seleziona una nazione dalla scheda 'Nazioni' per segnare le regioni visitate e aggiungere i tuoi ricordi, note e foto.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        } else {
            Text(
                text = "Tocca una nazione per mostrare le regioni visitate, i tuoi appunti e le foto",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val favoriteRegions = visitedRegions.filter { it.rating > 0 }
                    .sortedWith(compareByDescending<VisitedRegionEntity> { it.rating }.thenBy { it.regionId })
                    .take(10)

                if (favoriteRegions.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("favorites_ranking_card"),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "I Tuoi Preferiti ⭐ (Top 10)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    favoriteRegions.forEachIndexed { index, fav ->
                                        val countryObj = MapData.countries.find { it.id == fav.countryId }
                                        val regionObj = countryObj?.regions?.find { it.id == fav.regionId }
                                        val regionName = regionObj?.name ?: fav.regionId
                                        val countryName = countryObj?.let { "${it.flag} ${it.name}" } ?: fav.countryId
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.width(30.dp)
                                                )
                                                
                                                Column {
                                                    Text(
                                                        text = regionName,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Text(
                                                        text = countryName,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                for (s in 1..5) {
                                                    Icon(
                                                        imageVector = Icons.Default.Star,
                                                        contentDescription = null,
                                                        tint = if (s <= fav.rating) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                items(visitedByCountry.keys.toList()) { countryId ->
                    val country = MapData.countries.find { it.id == countryId } ?: return@items
                    val regionsVisited = visitedByCountry[countryId] ?: emptyList()
                    val isExpanded = expandedCountries.contains(countryId)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .testTag("visited_country_card_${countryId}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)
                                             else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedCountries = if (isExpanded) {
                                            expandedCountries - countryId
                                        } else {
                                            expandedCountries + countryId
                                        }
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = country.flag,
                                        fontSize = 32.sp
                                    )
                                    Column {
                                        Text(
                                            text = country.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${regionsVisited.size} ${if (regionsVisited.size == 1) "regione esplorata" else "regioni esplorate"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Circular Button to quickly go to the country map details page
                                    IconButton(
                                        onClick = { onCountrySelected(countryId) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Map,
                                            contentDescription = "Apri mappa nazionale",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f)
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = if (isExpanded) "Riduci" else "Espandi",
                                        modifier = Modifier.rotate(rotation)
                                    )
                                }
                            }

                            if (isExpanded) {
                                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    regionsVisited.forEach { visitedRegion ->
                                        val region = country.regions.find { it.id == visitedRegion.regionId }
                                        val regionName = region?.name ?: visitedRegion.regionId

                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.surface
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Place,
                                                            contentDescription = null,
                                                            tint = Color(0xFFF27D26),
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Text(
                                                            text = regionName,
                                                            fontWeight = FontWeight.Bold,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFF27D26).copy(alpha = 0.1f))
                                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Visitata ✨",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = Color(0xFFF27D26)
                                                        )
                                                    }
                                                }

                                                // Travel notes if they have entered any!
                                                if (visitedRegion.notes.isNotBlank()) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(
                                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                                                RoundedCornerShape(8.dp)
                                                            )
                                                            .padding(8.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                        verticalAlignment = Alignment.Top
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.EditNote,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                        Text(
                                                            text = visitedRegion.notes,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }

                                                // Display photo attachments dynamically if available!
                                                if (visitedRegion.photoUris.isNotBlank()) {
                                                    val uris = visitedRegion.photoUris.split(",").filter { it.isNotBlank() }
                                                    if (uris.isNotEmpty()) {
                                                        LazyRow(
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            items(uris) { uriStr ->
                                                                val isVid = ImageStorageHelper.isVideo(uriStr)
                                                                val itemIndex = uris.indexOf(uriStr).coerceAtLeast(0)
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(74.dp)
                                                                        .clip(RoundedCornerShape(8.dp))
                                                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                                                        .clickable {
                                                                            expandedMediaData = DiaryMediaItem(uris, itemIndex, regionName, country.name)
                                                                        }
                                                                        .testTag("diary_photo_item")
                                                                ) {
                                                                    if (isVid) {
                                                                        VideoThumbnailView(
                                                                            pathOrUri = uriStr,
                                                                            modifier = Modifier.fillMaxSize()
                                                                        )
                                                                        Box(
                                                                            modifier = Modifier
                                                                                .fillMaxSize()
                                                                                .background(Color.Black.copy(alpha = 0.25f)),
                                                                            contentAlignment = Alignment.Center
                                                                        ) {
                                                                            Surface(
                                                                                shape = CircleShape,
                                                                                color = Color.Black.copy(alpha = 0.65f),
                                                                                modifier = Modifier.size(24.dp)
                                                                            ) {
                                                                                Box(contentAlignment = Alignment.Center) {
                                                                                    Icon(
                                                                                        imageVector = Icons.Default.PlayArrow,
                                                                                        contentDescription = "Video",
                                                                                        tint = Color.White,
                                                                                        modifier = Modifier.size(16.dp)
                                                                                    )
                                                                                }
                                                                            }
                                                                        }
                                                                    } else {
                                                                        AsyncImage(
                                                                            model = ImageRequest.Builder(LocalContext.current)
                                                                                .data(ImageStorageHelper.getImageModel(uriStr))
                                                                                .crossfade(true)
                                                                                .build(),
                                                                            contentDescription = "Foto di $regionName",
                                                                            contentScale = ContentScale.Crop,
                                                                            modifier = Modifier.fillMaxSize()
                                                                        )
                                                                    }

                                                                    // Mini expand button
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .align(Alignment.BottomEnd)
                                                                            .size(22.dp)
                                                                            .clip(RoundedCornerShape(topStart = 6.dp))
                                                                            .background(Color.Black.copy(alpha = 0.65f))
                                                                            .clickable {
                                                                                expandedMediaData = DiaryMediaItem(uris, itemIndex, regionName, country.name)
                                                                            }
                                                                            .testTag("expand_diary_photo_button"),
                                                                        contentAlignment = Alignment.Center
                                                                    ) {
                                                                        Icon(
                                                                            imageVector = Icons.Default.ZoomIn,
                                                                            contentDescription = "Ingrandisci media",
                                                                            tint = Color.White,
                                                                            modifier = Modifier.size(14.dp)
                                                                        )
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
                        }
                    }
                }
            }
        }
    }

    // Modal dialog for enlarged photo/video with swipe and video player
    if (expandedMediaData != null) {
        val data = expandedMediaData!!
        ExpandedPhotoDialog(
            mediaList = data.mediaList,
            initialIndex = data.initialIndex,
            title = data.regionName,
            subtitle = data.countryName,
            onDismissRequest = {
                expandedMediaData = null
            }
        )
    }
}

// Ray Casting Algorithm to check if Point lies inside Polygon (normalized space)
fun isPointInPolygon(point: Point, polygon: List<Point>): Boolean {
    if (polygon.isEmpty()) return false
    var intersectCount = 0
    val count = polygon.size
    for (i in 0 until count) {
        val p1 = polygon[i]
        val p2 = polygon[(i + 1) % count]
        if (((p1.y > point.y) != (p2.y > point.y)) &&
            (point.x < (p2.x - p1.x) * (point.y - p1.y) / (p2.y - p1.y) + p1.x)
        ) {
            intersectCount++
        }
    }
    return intersectCount % 2 != 0
}

@Composable
fun HighDensityBottomNav(
    activeTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    onCloudClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        // Apply navigationBarsPadding to lift the buttons completely above the 3 Android system keys
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 1: Mappa
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onTabSelected(MainTab.MAPPA) }
                        .testTag("tab_mappa")
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (activeTab == MainTab.MAPPA) MaterialTheme.colorScheme.secondary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Mappa",
                            tint = if (activeTab == MainTab.MAPPA) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Mappa",
                        fontSize = 11.5.sp,
                        fontWeight = if (activeTab == MainTab.MAPPA) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (activeTab == MainTab.MAPPA) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Tab 2: Viaggi
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onTabSelected(MainTab.CONTINENTI) }
                        .testTag("tab_continenti")
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (activeTab == MainTab.CONTINENTI) MaterialTheme.colorScheme.secondary else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Viaggi",
                            tint = if (activeTab == MainTab.CONTINENTI) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Viaggi",
                        fontSize = 11.5.sp,
                        fontWeight = if (activeTab == MainTab.CONTINENTI) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (activeTab == MainTab.CONTINENTI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Tab 3: Centered Action Button for AI (Chiedi all'AI)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onTabSelected(MainTab.CONSIGLI) }
                        .testTag("tab_consigli")
                        .weight(1.2f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(elevation = 4.dp, shape = CircleShape)
                            .background(
                                color = if (activeTab == MainTab.CONSIGLI) MaterialTheme.colorScheme.primary else Color(0xFF1E232A),
                                shape = CircleShape
                            )
                            .border(
                                width = 2.dp,
                                color = if (activeTab == MainTab.CONSIGLI) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Chiedi all'AI",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Chiedi all'AI",
                        fontSize = 11.sp,
                        fontWeight = if (activeTab == MainTab.CONSIGLI) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (activeTab == MainTab.CONSIGLI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }

                // Tab 4: Cloud Action
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onCloudClick() }
                        .testTag("tab_cloud")
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 32.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = "Sincronizza Cloud",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Cloud",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }

                // Tab 5: Profilo Action
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable { onProfileClick() }
                        .testTag("tab_profilo")
                        .weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 54.dp, height = 32.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Profilo",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        text = "Profilo",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickCountrySearchPill(
    visitedRegions: List<VisitedRegionEntity>,
    onCountrySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            emptyList()
        } else {
            val q = searchQuery.trim().lowercase()
            MapData.countries.filter { country ->
                country.name.lowercase().contains(q) ||
                country.continent.displayName.lowercase().contains(q) ||
                country.id.lowercase().contains(q)
            }
        }
    }

    Column(modifier = modifier) {
        // Pill capsule surface
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca nazione",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                BasicTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        isExpanded = it.isNotBlank()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("country_search_pill_input"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Cerca velocemente una nazione...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                        innerTextField()
                    }
                )

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            isExpanded = false
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancella ricerca",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Live search results dropdown
        if (isExpanded && filteredCountries.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp))
                    .testTag("country_search_results_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    filteredCountries.forEach { country ->
                        val visitedCount = visitedRegions.count { it.countryId == country.id && it.isVisited }
                        val totalRegions = country.regions.size

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = ""
                                    isExpanded = false
                                    onCountrySelected(country.id)
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("search_result_country_${country.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = country.flag,
                                    fontSize = 24.sp
                                )
                                Column {
                                    Text(
                                        text = country.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = country.continent.displayName,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (visitedCount > 0) Color(0xFFF27D26).copy(alpha = 0.15f)
                                           else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = if (visitedCount > 0) "$visitedCount / $totalRegions" else "Non visitata",
                                        color = if (visitedCount > 0) Color(0xFFF27D26) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        } else if (isExpanded && searchQuery.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Nessuna nazione trovata per \"$searchQuery\"",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Forza il riavvio e la reinstallazione dell'APK sull'emulatore per sbloccare lo schermo


