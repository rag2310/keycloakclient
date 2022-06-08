package com.rago.keycloakclient.ui.login

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.utils.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authorizationService: AuthorizationService
) : ViewModel() {

    private val _loginUIState: MutableStateFlow<LoginUIState> =
        MutableStateFlow(LoginUIState.NotState)
    val loginUIState: StateFlow<LoginUIState>
        get() = _loginUIState

    fun isAuthorized() {
        viewModelScope.launch {
            _loginUIState.value = LoginUIState.Loading
            if (authStateManager.getCurrent().isAuthorized) {
                authStateManager.getCurrent().performActionWithFreshTokens(
                    authorizationService
                ) { accessToken, _, exception ->
                    if (exception != null) {
                        _loginUIState.value = LoginUIState.Error(exception.message!!)
                    } else {
                        if (accessToken != null) {
                            _loginUIState.value = LoginUIState.Authorized
                        } else {
                            _loginUIState.value = LoginUIState.NotAuthorized
                        }
                    }
                }
            } else {
                _loginUIState.value = LoginUIState.NotAuthorized
            }
        }
    }

    fun login(onLaunch: (Intent) -> Unit) {
        viewModelScope.launch {
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
    }

    fun resultLogin(intent: Intent?) {
        _loginUIState.value = LoginUIState.Loading
        viewModelScope.launch {
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
                            if (authorizationResponse != null) {
                                authStateManager.updateAfterAuthorization(
                                    authorizationResponse,
                                    authorizationException
                                )
                                authorizationService.performTokenRequest(
                                    authorizationResponse.createTokenExchangeRequest()
                                ) { tokenResponse, exception ->
                                    if (tokenResponse != null) {
                                        authStateManager.updateAfterTokenResponse(
                                            tokenResponse,
                                            exception
                                        )
                                        _loginUIState.value = LoginUIState.Authorized
                                    } else {
                                        _loginUIState.value =
                                            LoginUIState.Error("authentication: not authentication")
                                    }
                                }
                            } else {
                                _loginUIState.value =
                                    LoginUIState.Error("authentication: not authentication")
                            }
                        }
                    }
                } else {
                    _loginUIState.value = LoginUIState.Error("result null")
                }
            } else {
                _loginUIState.value = LoginUIState.Error("intent null")
            }
        }
    }
}