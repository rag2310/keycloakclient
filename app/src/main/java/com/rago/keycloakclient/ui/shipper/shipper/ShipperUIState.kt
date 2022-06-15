package com.rago.keycloakclient.ui.shipper.shipper

sealed class ShipperUIState {
    data class Permissions(
        val deleteOrUpdateShipper: Boolean,
        val createdShipper: Boolean
    ) : ShipperUIState()

    object NotState : ShipperUIState()
}