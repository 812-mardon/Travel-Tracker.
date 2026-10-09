package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "visited_regions")
data class VisitedRegionEntity(
    @PrimaryKey val id: String, // format: "countryId_regionId"
    val countryId: String,
    val regionId: String,
    val isVisited: Boolean,
    val notes: String = "",
    val photoUris: String = "", // Comma-separated list of image URI paths (e.g., "content://...", "/storage/...")
    val rating: Int = 0 // Rating from 1 to 5 stars, default 0
)
