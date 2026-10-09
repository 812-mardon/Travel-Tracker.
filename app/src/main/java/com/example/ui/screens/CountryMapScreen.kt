package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.database.VisitedRegionEntity
import com.example.data.model.Country
import com.example.data.model.Region
import com.example.ui.viewmodel.TravelViewModel
import com.example.utils.ImageStorageHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountryMapScreen(
    country: Country,
    visitedRegions: List<VisitedRegionEntity>,
    viewModel: TravelViewModel
) {
    val context = LocalContext.current
    var selectedRegion by remember { mutableStateOf<Region?>(country.regions.firstOrNull()) }

    // State for viewing enlarged media lightbox with swipe
    var expandedMediaIndex by remember { mutableStateOf<Int?>(null) }

    // Find the entity strictly matching both countryId and regionId (no mixing!)
    val currentRegionEntity = remember(visitedRegions, selectedRegion?.id) {
        visitedRegions.find { it.countryId == country.id && it.regionId == selectedRegion?.id }
    }

    // Editable notes for current region
    var notesText by remember(selectedRegion?.id) {
        mutableStateOf(currentRegionEntity?.notes ?: "")
    }

    // Keep notes in sync with Room database when loaded asynchronously
    LaunchedEffect(selectedRegion?.id, currentRegionEntity?.notes) {
        val dbNotes = currentRegionEntity?.notes ?: ""
        if (notesText.isEmpty() && dbNotes.isNotEmpty()) {
            notesText = dbNotes
        }
    }

    // Region photos and videos list strictly tied to current region
    val photoUris = remember(currentRegionEntity?.photoUris) {
        currentRegionEntity?.photoUris?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    // Rating for current region
    val rating = currentRegionEntity?.rating ?: 0

    // Photo/Video picker launcher allowing multiple selections at once
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                selectedRegion?.let { region ->
                    val savedPaths = uris.mapNotNull { uri ->
                        ImageStorageHelper.saveMediaFromUri(context, uri, country.id, region.id)
                    }
                    if (savedPaths.isNotEmpty()) {
                        val newUris = photoUris + savedPaths
                        viewModel.saveRegionDetails(country.id, region.id, notesText, newUris, rating)
                        val count = savedPaths.size
                        val msg = if (count == 1) "1 elemento aggiunto con successo!" else "$count elementi aggiunti con successo!"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Impossibile salvare i file selezionati", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Upper: Scrollable Regions List Card
        Card(
            modifier = Modifier
                .weight(0.95f)
                .fillMaxWidth()
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
                .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header inside the Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "REGIONI DI ${country.name.uppercase()}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Tocca per scrivere appunti o segnare come visitata",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    val visitedCount = visitedRegions.filter { it.countryId == country.id && it.isVisited }.size
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF27D26), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$visitedCount / ${country.regions.size}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Scrollable List of Regions (isolated strictly to this country)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(country.regions) { region ->
                        val regionEntity = visitedRegions.find { it.countryId == country.id && it.regionId == region.id }
                        val isVisited = regionEntity?.isVisited == true
                        val isSelected = selectedRegion?.id == region.id

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(0xFFF27D26).copy(alpha = 0.12f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFFF27D26) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedRegion = region
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = isVisited,
                                    onCheckedChange = { viewModel.toggleRegionVisited(country.id, region.id) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color(0xFFF27D26),
                                        uncheckedColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Text(
                                    text = region.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = if (isSelected) Color(0xFFF27D26) else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            val hasNotes = regionEntity?.notes?.isNotBlank() == true
                            val count = regionEntity?.photoUris?.split(",")?.filter { it.isNotBlank() }?.size ?: 0

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (hasNotes) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "Appunti presenti",
                                        tint = Color(0xFFF27D26),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "$count 📸",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Text(
                                    text = if (isSelected) "Modifica" else "Seleziona",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFF27D26) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }

        // Lower: Region Details panel with expanded height and scrollability
        selectedRegion?.let { region ->
            val isVisited = currentRegionEntity?.isVisited == true

            Card(
                modifier = Modifier
                    .weight(1.35f)
                    .fillMaxWidth()
                    .testTag("region_detail_card")
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Region name and visit toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = region.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Regione di ${country.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        // Toggle status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (isVisited) Color(0xFFF27D26).copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { viewModel.toggleRegionVisited(country.id, region.id) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (isVisited) Color(0xFFF27D26) else Color.Gray)
                            )
                            Text(
                                text = if (isVisited) "Visitata ✨" else "Non Visitata",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isVisited) Color(0xFFF27D26) else Color.Gray
                            )
                        }
                    }

                    // Interactive Rating Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("region_rating_bar")
                    ) {
                        Text(
                            text = "Valutazione:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (i in 1..5) {
                                val isFilled = i <= rating
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Vota $i Stelle",
                                    tint = if (isFilled) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable {
                                            val newRating = if (rating == i) 0 else i
                                            viewModel.saveRegionDetails(country.id, region.id, notesText, photoUris, newRating)
                                        }
                                        .testTag("star_icon_$i")
                                )
                            }
                        }
                    }

                    // Notes input: vertically longer and comfortably sized for longer travel journals
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = {
                            notesText = it
                            viewModel.saveRegionDetails(country.id, region.id, it, photoUris, rating)
                        },
                        label = { Text("Diario & Appunti di Viaggio") },
                        placeholder = { Text("Scrivi cosa hai visitato, itinerari, ristoranti tipici, hotel consigliati...") },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp, max = 220.dp)
                            .testTag("region_notes_input"),
                        leadingIcon = { Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFFF27D26)) },
                        minLines = 5,
                        maxLines = 14,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            focusedLabelColor = Color(0xFFF27D26),
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.12f),
                            focusedBorderColor = Color(0xFFF27D26),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )

                    // Photos and Videos Row Header and Action Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Media dell'Itinerario (${photoUris.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Upload button for multiple photos and videos
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                                )
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF27D26),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("attach_photo_button")
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Aggiungi Foto / Video", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Photos / Videos List or Empty State
                    if (photoUris.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(76.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nessun media allegato. Tocca 'Aggiungi Foto / Video' per inserire più elementi!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(photoUris) { uriStr ->
                                val isVid = ImageStorageHelper.isVideo(uriStr)
                                val itemIndex = photoUris.indexOf(uriStr)
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
                                        .clickable {
                                            expandedMediaIndex = itemIndex
                                        }
                                        .testTag("photo_thumbnail_box")
                                ) {
                                    if (isVid) {
                                        VideoThumbnailView(
                                            pathOrUri = uriStr,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                        // Play badge overlay for video
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(Color.Black.copy(alpha = 0.25f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.65f),
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = "Video",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 6.dp),
                                            color = Color(0xFFF27D26),
                                            modifier = Modifier.align(Alignment.TopStart)
                                        ) {
                                            Text(
                                                text = "VIDEO",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    } else {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(ImageStorageHelper.getImageModel(uriStr))
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Foto di ${region.name}",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                    // Mini Button to expand photo/video
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(topStart = 8.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .clickable {
                                                expandedMediaIndex = itemIndex
                                            }
                                            .testTag("expand_photo_mini_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ZoomIn,
                                            contentDescription = "Espandi media",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Mini Button to delete photo/video
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(bottomStart = 8.dp))
                                            .background(Color.Red.copy(alpha = 0.85f))
                                            .clickable {
                                                val filtered = photoUris.filter { it != uriStr }
                                                ImageStorageHelper.deleteInternalImage(uriStr)
                                                viewModel.saveRegionDetails(country.id, region.id, notesText, filtered, rating)
                                                Toast.makeText(context, if (isVid) "Video rimosso" else "Foto rimossa", Toast.LENGTH_SHORT).show()
                                            }
                                            .testTag("delete_photo_mini_button"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Rimuovi media",
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

    // Modal Dialog to display enlarged photo/video with swipe and integrated player
    if (expandedMediaIndex != null && photoUris.isNotEmpty()) {
        ExpandedPhotoDialog(
            mediaList = photoUris,
            initialIndex = expandedMediaIndex!!,
            title = selectedRegion?.name ?: "Media del Viaggio",
            subtitle = country.name,
            onDismissRequest = {
                expandedMediaIndex = null
            }
        )
    }
}
