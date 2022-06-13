package com.rago.keycloakclient.utils

import net.openid.appauth.*

class AuthStateManager(
    private val prefs: KeyCloakClientSharedPreferences
) {

    fun getInstance(): AuthState = prefs.readState()

    fun replace(state: AuthState): AuthState {
        prefs.writeState(state)
        return state
    }

    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = getInstance()
        current.update(response, ex)
        return replace(current)
    }

    fun updateAfterTokenResponse(response: TokenResponse, ex: AuthorizationException?): AuthState {
        val current = getInstance()
        current.update(response, ex)
        return replace(current)
    }

    fun updateAfterRegistration(
        response: RegistrationResponse,
        ex: AuthorizationException?
    ): AuthState {
        val current = getInstance()
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }
}