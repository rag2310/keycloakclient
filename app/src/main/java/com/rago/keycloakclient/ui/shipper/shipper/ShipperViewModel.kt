package com.rago.keycloakclient.ui.shipper.shipper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.db.entities.Shipper
import com.rago.keycloakclient.repositories.auth.AuthRepository
import com.rago.keycloakclient.repositories.shipper.ShipperRepository
import com.rago.keycloakclient.utils.roles.RolesEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ShipperViewModel @Inject constructor(
    private val shipperRepository: ShipperRepository,
    authRepository: AuthRepository
) :
    ViewModel() {

    private val _shipperUIState: MutableStateFlow<ShipperUIState> =
        MutableStateFlow(ShipperUIState.NotState)
    val shipperUIState: StateFlow<ShipperUIState>
        get() = _shipperUIState

    init {
        _shipperUIState.value = ShipperUIState.Permissions(
            authRepository.permissionToUpdateOrDelete(RolesEnum.SHIPPER),
            authRepository.permissionToCreated(RolesEnum.SHIPPER)
        )
    }

    val allShipper: StateFlow<List<Shipper>> = flow {
        emitAll(shipperRepository.getAll())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )

    fun insert() {
        viewModelScope.launch {
            val shipper = Shipper(Date().time, "Shipper : ${Date().time / 1000}")
            shipperRepository.insert(shipper)
        }
    }
}