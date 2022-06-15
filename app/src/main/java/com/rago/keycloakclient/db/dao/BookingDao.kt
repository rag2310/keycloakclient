package com.rago.keycloakclient.db.dao

import androidx.room.*
import com.rago.keycloakclient.db.entities.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Query("SELECT * FROM BOOKING")
    fun getAll(): Flow<List<Booking>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking)

    @Update
    suspend fun update(booking: Booking)

    @Delete
    suspend fun delete(booking: Booking)
}