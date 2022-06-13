package com.rago.keycloakclient.utils.callbacks

import android.util.Log
import androidx.work.ListenableWorker
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.TokenResponse

interface TokenCallback : AuthorizationService.TokenResponseCallback {
    override fun onTokenRequestCompleted(response: TokenResponse?, ex: AuthorizationException?) {
        onNewTokenRequestCompleted(response, ex)
    }

    fun onNewTokenRequestCompleted(
        response: TokenResponse?,
        ex: AuthorizationException?
    ): ListenableWorker.Result
}