package com.rago.keycloakclient.db.dao

import androidx.room.*
import com.rago.keycloakclient.db.entities.Shipper
import kotlinx.coroutines.flow.Flow

@Dao
interface ShipperDao {

    @Query("SELECT * FROM SHIPPER")
    fun getAll(): Flow<List<Shipper>>

    @Delete
    suspend fun delete(shipper: Shipper)

    @Update
    suspend fun update(shipper: Shipper)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shipper: Shipper)
}