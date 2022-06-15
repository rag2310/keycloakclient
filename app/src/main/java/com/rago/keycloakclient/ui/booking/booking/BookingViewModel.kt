package com.rago.keycloakclient.ui.booking.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.db.entities.Booking
import com.rago.keycloakclient.repositories.auth.AuthRepository
import com.rago.keycloakclient.repositories.booking.BookingRepository
import com.rago.keycloakclient.utils.roles.RolesEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    authRepository: AuthRepository
) : ViewModel() {
    private val _bookingUIState: MutableStateFlow<BookingUIState> =
        MutableStateFlow(BookingUIState.NotState)
    val bookingUIState: StateFlow<BookingUIState>
        get() = _bookingUIState

    val allBooking = flow {
        emitAll(bookingRepository.getAll())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    init {
        _bookingUIState.value = BookingUIState.Permissions(
            updateOrDeleteBooking = authRepository.permissionToUpdateOrDelete(RolesEnum.BOOKING),
            createdBooking = authRepository.permissionToCreated(RolesEnum.BOOKING)
        )
    }

    fun insert() {
        viewModelScope.launch {
            val booking = Booking(Date().time, "Booking : ${Date().time / 1000}")
            bookingRepository.insert(booking)
        }
    }
}