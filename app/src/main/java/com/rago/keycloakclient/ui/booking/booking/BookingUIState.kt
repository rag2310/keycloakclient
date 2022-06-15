package com.rago.keycloakclient.ui.booking.booking

sealed class BookingUIState {
    data class Permissions(
        val updateOrDeleteBooking: Boolean,
        val createdBooking: Boolean
    ) : BookingUIState()

    object NotState : BookingUIState()
}