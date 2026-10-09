package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.database.VisitedRegionEntity
import com.example.data.model.MapData
import java.io.File
import java.io.FileOutputStream

object PdfExporter {
    fun exportTravelsToPdf(
        context: Context,
        visitedRegions: List<VisitedRegionEntity>
    ) {
        if (visitedRegions.isEmpty()) {
            Toast.makeText(context, "Nessun viaggio registrato da esportare!", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val pdfDocument = PdfDocument()
            val paint = Paint()
            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            val titlePaint = Paint().apply {
                color = Color.rgb(230, 81, 0) // Deep Orange
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val headerPaint = Paint().apply {
                color = Color.rgb(33, 33, 33)
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            val subPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            }

            // A4 page size is typically 595 x 842 points
            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas: Canvas = page.canvas

            // Title Block
            canvas.drawText("Il Mio Diario di Viaggio ✈️", 40f, 60f, titlePaint)
            canvas.drawText("Esportazione del tuo itinerario personale", 40f, 85f, subPaint)

            // Horizontal Line
            paint.color = Color.rgb(200, 200, 200)
            paint.strokeWidth = 2f
            canvas.drawLine(40f, 100f, 555f, 100f, paint)

            // Statistics
            val totalCountries = visitedRegions.map { it.countryId }.distinct().size
            canvas.drawText("Statistiche Generali:", 40f, 130f, headerPaint)
            canvas.drawText("• Nazioni visitate: $totalCountries", 50f, 155f, textPaint)
            canvas.drawText("• Regioni visitate: ${visitedRegions.size}", 50f, 175f, textPaint)

            paint.color = Color.rgb(240, 240, 240)
            canvas.drawRect(40f, 200f, 555f, 205f, paint) // spacing line

            var yPosition = 230f

            // ------------------ FAVORITES RANKING SECTION ------------------
            val favoriteRegions = visitedRegions.filter { it.rating > 0 }
                .sortedWith(compareByDescending<VisitedRegionEntity> { it.rating }.thenBy { it.regionId })
                .take(10)

            if (favoriteRegions.isNotEmpty()) {
                val favHeaderPaint = Paint().apply {
                    color = Color.rgb(230, 81, 0) // Deep Orange
                    textSize = 14f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("I Miei Luoghi Preferiti ⭐ (Top 10):", 40f, yPosition, favHeaderPaint)
                yPosition += 22f

                for (index in favoriteRegions.indices) {
                    val fav = favoriteRegions[index]
                    val country = MapData.countries.find { it.id == fav.countryId }
                    val region = country?.regions?.find { it.id == fav.regionId }
                    val regionName = region?.name ?: fav.regionId
                    val countryLabel = country?.let { "${it.flag} ${it.name}" } ?: fav.countryId

                    val starsStr = "★".repeat(fav.rating) + "☆".repeat(5 - fav.rating)

                    val favTextPaint = Paint().apply {
                        color = Color.BLACK
                        textSize = 11f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    }

                    canvas.drawText("#${index + 1} $regionName ($countryLabel)", 50f, yPosition, favTextPaint)
                    
                    val starPaint = Paint().apply {
                        color = Color.rgb(245, 127, 23) // Star gold
                        textSize = 11f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText(starsStr, 420f, yPosition, starPaint)
                    yPosition += 16f
                }

                yPosition += 15f
                paint.color = Color.rgb(220, 220, 220)
                canvas.drawRect(40f, yPosition - 5f, 555f, yPosition, paint)
                yPosition += 20f
            }

            val maxPageHeight = 780f

            // Group by country for cleaner presentation
            val groupedByCountry = visitedRegions.groupBy { it.countryId }

            for ((countryId, regions) in groupedByCountry) {
                val countryObj = MapData.countries.find { it.id == countryId }
                val countryName = countryObj?.name ?: countryId.uppercase()
                val flag = countryObj?.flag ?: "🏳️"

                if (yPosition > maxPageHeight - 60f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = 50f
                }

                // Country Header
                val countryHeaderPaint = Paint().apply {
                    color = Color.rgb(13, 71, 161) // Dark Blue
                    textSize = 16f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                canvas.drawText("$flag $countryName", 40f, yPosition, countryHeaderPaint)
                yPosition += 25f

                // Draw solid separator line under country
                paint.color = Color.rgb(13, 71, 161)
                paint.strokeWidth = 1f
                canvas.drawLine(40f, yPosition - 18f, 250f, yPosition - 18f, paint)

                for (regionEntity in regions) {
                    val regionName = countryObj?.regions?.find { it.id == regionEntity.regionId }?.name ?: regionEntity.regionId

                    if (yPosition > maxPageHeight - 80f) {
                        pdfDocument.finishPage(page)
                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPosition = 50f
                    }

                    // Region Title
                    val regionTitlePaint = Paint().apply {
                        color = Color.rgb(230, 81, 0) // Orange Accent
                        textSize = 13f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText("📍 $regionName", 50f, yPosition, regionTitlePaint)
                    yPosition += 18f

                    // Notes title/label
                    val labelPaint = Paint().apply {
                        color = Color.GRAY
                        textSize = 11f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                    canvas.drawText("Note di viaggio:", 65f, yPosition, labelPaint)
                    yPosition += 15f

                    // Region Notes Wrapping
                    val notesText = if (regionEntity.notes.isBlank()) {
                        "Nessuna nota aggiunta."
                    } else {
                        regionEntity.notes
                    }

                    val notesLines = wrapText(notesText, 450)
                    for (line in notesLines) {
                        if (yPosition > maxPageHeight - 30f) {
                            pdfDocument.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            yPosition = 50f
                        }
                        canvas.drawText(line, 65f, yPosition, textPaint)
                        yPosition += 15f
                    }

                    // Photo Info
                    if (regionEntity.photoUris.isNotBlank()) {
                        val count = regionEntity.photoUris.split(",").filter { it.isNotBlank() }.size
                        if (yPosition > maxPageHeight - 30f) {
                            pdfDocument.finishPage(page)
                            pageNumber++
                            pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                            page = pdfDocument.startPage(pageInfo)
                            canvas = page.canvas
                            yPosition = 50f
                        }
                        canvas.drawText("📸 $count foto allegate a questo itinerario", 65f, yPosition, Paint(textPaint).apply { typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC); color = Color.rgb(56, 142, 60) })
                        yPosition += 20f
                    } else {
                        yPosition += 10f
                    }

                    // Add spacing between regions
                    yPosition += 10f
                }
                // Add spacing between countries
                yPosition += 15f
            }

            // Finish the final page
            pdfDocument.finishPage(page)

            // Save document in Cache
            val cachePath = File(context.cacheDir, "travels")
            cachePath.mkdirs()
            val file = File(cachePath, "Diario_di_Viaggio.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.flush()
            outputStream.close()

            // Open share intent
            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "Il Mio Diario di Viaggio - Esportazione PDF")
                putExtra(Intent.EXTRA_TEXT, "Ciao! Ecco l'esportazione PDF del mio diario di viaggio creato con Travel Tracker.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Condividi il tuo PDF"))

        } catch (e: Exception) {
            Toast.makeText(context, "Errore nella generazione del PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Helper method to wrap text into lines based on maximum pixel width
    private fun wrapText(text: String, maxWidthInPoints: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()
        
        // Estimate average char size (very conservative, around 6-7 pts per character for standard 12f font)
        val charWidth = 6.5f

        for (word in words) {
            val projectedLineLength = (currentLine.length + word.length + 1) * charWidth
            if (projectedLineLength > maxWidthInPoints) {
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder()
                }
                currentLine.append(word)
            } else {
                if (currentLine.isNotEmpty()) {
                    currentLine.append(" ")
                }
                currentLine.append(word)
            }
        }
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine.toString())
        }
        return lines
    }
}
