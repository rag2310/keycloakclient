package com.rago.keycloakclient.ui.token

sealed class TokenUIState {

    data class Permissions(
        val viewBooking: Boolean,
        val viewShipper: Boolean
    ) : TokenUIState()

    object NotState : TokenUIState()
}