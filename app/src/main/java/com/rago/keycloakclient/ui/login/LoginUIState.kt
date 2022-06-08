package com.rago.keycloakclient.ui.login

sealed class LoginUIState {
    object Loading : LoginUIState()
    object NotAuthorized : LoginUIState()
    object Authorized : LoginUIState()
    data class Error(val msg: String) : LoginUIState()
    object NotState : LoginUIState()
}