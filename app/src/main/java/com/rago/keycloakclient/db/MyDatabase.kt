package com.rago.keycloakclient.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rago.keycloakclient.db.dao.BookingDao
import com.rago.keycloakclient.db.dao.ShipperDao
import com.rago.keycloakclient.db.entities.Booking
import com.rago.keycloakclient.db.entities.Shipper

@Database(entities = [Shipper::class, Booking::class], version = 1)
abstract class MyDatabase : RoomDatabase() {
    abstract fun shipperDao(): ShipperDao
    abstract fun bookingDao(): BookingDao
}