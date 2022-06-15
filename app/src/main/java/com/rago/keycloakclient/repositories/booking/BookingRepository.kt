package com.rago.keycloakclient.repositories.booking

import com.rago.keycloakclient.db.dao.BookingDao
import com.rago.keycloakclient.db.entities.Booking
import javax.inject.Inject


class BookingRepository @Inject constructor(
    private val bookingDao: BookingDao
) {

    fun getAll() = bookingDao.getAll()

    suspend fun insert(booking: Booking) = bookingDao.insert(booking)
    suspend fun update(booking: Booking) = bookingDao.update(booking)
    suspend fun delete(booking: Booking) = bookingDao.delete(booking)
}