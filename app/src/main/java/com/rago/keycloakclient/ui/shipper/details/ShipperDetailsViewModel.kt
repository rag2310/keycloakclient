package com.rago.keycloakclient.ui.shipper.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.db.entities.Shipper
import com.rago.keycloakclient.repositories.auth.AuthRepository
import com.rago.keycloakclient.repositories.shipper.ShipperRepository
import com.rago.keycloakclient.utils.roles.RolesEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ShipperDetailsViewModel @Inject constructor(
    private val shipperRepository: ShipperRepository,
    authRepository: AuthRepository
) : ViewModel() {

    private val _shipperDetailsUIState: MutableStateFlow<ShipperDetailsUIState> =
        MutableStateFlow(ShipperDetailsUIState.NotState)
    val shipperDetailsUIState: StateFlow<ShipperDetailsUIState>
        get() = _shipperDetailsUIState

    init {
        _shipperDetailsUIState.value = ShipperDetailsUIState.Permissions(
            updateShipper = authRepository.permissionToUpdate(RolesEnum.SHIPPER),
            deleteShipper = authRepository.permissionToDelete(RolesEnum.SHIPPER)
        )
    }

    fun update(shipper: Shipper) {
        viewModelScope.launch {
            shipper.title = "Shipper Update ${Date().time / 10000}"
            shipperRepository.update(shipper)
        }
    }

    fun delete(shipper: Shipper) {
        viewModelScope.launch {
            shipperRepository.delete(shipper)
        }
    }
}