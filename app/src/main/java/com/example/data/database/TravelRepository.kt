package com.example.data.database

import kotlinx.coroutines.flow.Flow

class TravelRepository(private val dao: VisitedRegionDao) {
    fun getVisitedRegionsForCountry(countryId: String): Flow<List<VisitedRegionEntity>> {
        return dao.getVisitedRegionsForCountry(countryId)
    }

    fun getAllVisitedRegionsFlow(): Flow<List<VisitedRegionEntity>> {
        return dao.getAllVisitedRegionsFlow()
    }

    suspend fun getAllVisitedRegions(): List<VisitedRegionEntity> {
        return dao.getAllVisitedRegions()
    }

    suspend fun insertOrUpdate(region: VisitedRegionEntity) {
        dao.insertOrUpdate(region)
    }

    suspend fun deleteRegion(id: String) {
        dao.deleteRegion(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
