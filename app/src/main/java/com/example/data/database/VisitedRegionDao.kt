package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitedRegionDao {
    @Query("SELECT * FROM visited_regions WHERE countryId = :countryId")
    fun getVisitedRegionsForCountry(countryId: String): Flow<List<VisitedRegionEntity>>

    @Query("SELECT * FROM visited_regions")
    fun getAllVisitedRegionsFlow(): Flow<List<VisitedRegionEntity>>

    @Query("SELECT * FROM visited_regions")
    suspend fun getAllVisitedRegions(): List<VisitedRegionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(region: VisitedRegionEntity)

    @Query("DELETE FROM visited_regions WHERE id = :id")
    suspend fun deleteRegion(id: String)

    @Query("DELETE FROM visited_regions")
    suspend fun clearAll()
}
