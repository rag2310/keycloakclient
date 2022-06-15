package com.rago.keycloakclient.repositories.shipper

import com.rago.keycloakclient.db.dao.ShipperDao
import com.rago.keycloakclient.db.entities.Shipper
import javax.inject.Inject

class ShipperRepository @Inject constructor(
    private val shipperDao: ShipperDao
) {

    fun getAll() = shipperDao.getAll()

    suspend fun insert(shipper: Shipper) {
        shipperDao.insert(shipper)
    }

    suspend fun update(shipper: Shipper) {
        shipperDao.update(shipper)
    }

    suspend fun delete(shipper: Shipper) {
        shipperDao.delete(shipper)
    }
}