package com.rago.keycloakclient.ui.shipper.details

sealed class ShipperDetailsUIState {
    data class Permissions(
        val deleteShipper: Boolean,
        val updateShipper: Boolean
    ) : ShipperDetailsUIState()

    object NotState : ShipperDetailsUIState()
}