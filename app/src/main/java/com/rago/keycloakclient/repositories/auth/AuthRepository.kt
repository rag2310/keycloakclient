package com.rago.keycloakclient.repositories.auth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.work.ListenableWorker
import com.rago.keycloakclient.ui.login.LoginUIState
import com.rago.keycloakclient.utils.AuthStateManager
import com.rago.keycloakclient.utils.callbacks.TokenCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import net.openid.appauth.*
import java.util.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authorizationService: AuthorizationService
) {
    fun getToken() = authStateManager.getInstance().accessToken!!
    fun getTokenRefresh() = authStateManager.getInstance().refreshToken

    @OptIn(ExperimentalCoroutinesApi::class)
    fun workerRefresh() = callbackFlow {
        val callback = AuthorizationService.TokenResponseCallback { response, ex ->
            if (ex != null) cancel(ex.message!!, ex)

            if (response != null) {
                updateAfterTokenResponse(tokenResponse = response, exception = ex)
                Log.i("AuthRepository", "workerrefresh success: ${Date()}")
                channel.trySend(response)
            } else {
                Log.i("AuthRepository", "workerrefresh fail: ${Date()}")
                cancel(CancellationException("Response is null"))
            }
        }

        val clientAuthentication = authStateManager.getInstance().clientAuthentication
        authorizationService.performTokenRequest(
            authStateManager.getInstance().createTokenRefreshRequest(),
            clientAuthentication, callback
        )

        awaitClose {
            Log.i("AuthRepository", "close")
        }
    }

    fun refreshToken(callback: AuthorizationService.TokenResponseCallback) {
        val clientAuthentication = authStateManager.getInstance().clientAuthentication
        authorizationService.performTokenRequest(
            authStateManager.getInstance().createTokenRefreshRequest(),
            clientAuthentication,
            callback
        )
    }

    fun signOut() {
        val authState = authStateManager.getInstance()
        val clearedState = AuthState(authState.authorizationServiceConfiguration!!)
        if (authState.lastRegistrationResponse != null) {
            clearedState.update(authState.lastRegistrationResponse)
        }
        authStateManager.replace(clearedState)
    }

    fun logOut(onLaunch: (Intent) -> Unit) {
        val authState = authStateManager.getInstance()
        val authorizationServiceConfiguration = authState.authorizationServiceConfiguration
        val endSessionRequest = EndSessionRequest.Builder(authorizationServiceConfiguration!!)
            .setPostLogoutRedirectUri(Uri.parse("com.rago.keycloakclient:/logout"))
            .setAdditionalParameters(
                mutableMapOf(
                    "client_id" to "curso-web",
                    "refresh_token" to authState.refreshToken
                )
            )
            .build()
        val endIntent = authorizationService.getEndSessionRequestIntent(endSessionRequest)
        onLaunch(endIntent)
    }

    fun isAuthorized() = authStateManager.getInstance().isAuthorized

    fun authorized(callback: AuthState.AuthStateAction) {
        authStateManager.getInstance().performActionWithFreshTokens(
            authorizationService,
            callback
        )
    }

    fun updateAfterTokenResponse(tokenResponse: TokenResponse, exception: AuthorizationException?) {
        authStateManager.updateAfterTokenResponse(
            tokenResponse,
            exception
        )
    }

    fun login(onLaunch: (Intent) -> Unit) {
        val clientId = "curso-web"
        val authorizationServiceConfiguration =
            AuthorizationServiceConfiguration(
                Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/auth"),
                Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/token"),
                null,
                Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/logout")
            )
        val redirectUri = Uri.parse("com.rago.keycloakclient:/oauth2redirect")

        val builder = AuthorizationRequest.Builder(
            authorizationServiceConfiguration,
            clientId,
            ResponseTypeValues.CODE,
            redirectUri
        )
        builder.setScope("openid")

        val authorizationRequest = builder.build()
        val authIntent =
            authorizationService.getAuthorizationRequestIntent(authorizationRequest)
        onLaunch(authIntent)
    }

    fun resultLogin(
        intent: Intent?,
        callback: AuthorizationService.TokenResponseCallback
    ): LoginUIState {
        if (intent != null) {
            var uriResult = intent.data
            val oauth2Redirect = Uri.parse("com.rago.keycloakclient:/oauth2redirect")
            val indexUri = uriResult.toString().indexOf("?", 0)
            uriResult = Uri.parse(uriResult.toString().substring(0, indexUri))
            if (uriResult != null) {
                when (uriResult) {
                    oauth2Redirect -> {
                        val authorizationResponse =
                            AuthorizationResponse.fromIntent(intent)
                        val authorizationException =
                            AuthorizationException.fromIntent(intent)
                        return if (authorizationResponse != null) {
                            authStateManager.updateAfterAuthorization(
                                authorizationResponse,
                                authorizationException
                            )
                            authorizationService.performTokenRequest(
                                authorizationResponse.createTokenExchangeRequest(),
                                callback
                            )
                            LoginUIState.NotState
                        } else {
                            LoginUIState.Error("authentication: not authentication")
                        }
                    }
                    else -> {
                        LoginUIState.Error("not deeplink")
                    }
                }
            } else {
                return LoginUIState.Error("result null")
            }
        } else {
            return LoginUIState.Error("intent null")
        }
        return LoginUIState.NotState
    }

    suspend fun test() {
        try {
            val value = workerRefresh().first()
            Log.i("AuthRepository", "value ${value!!.accessToken} ${Date()}")
        } catch (ex: CancellationException) {
            Log.i("AuthRepository", "CancellationException ${ex.message} ${Date()}")
        } catch (e: Exception) {
            Log.i("AuthRepository", "Exception ${e.message} ${Date()}")
        }
    }

    private fun flowTest(): Flow<String> = flow {
        delay(2000)
        emit("test")
    }
}