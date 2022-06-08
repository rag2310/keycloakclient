package com.rago.keycloakclient.ui.token

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.rago.keycloakclient.utils.AuthStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import net.openid.appauth.EndSessionRequest
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val authStateManager: AuthStateManager,
    private val authorizationService: AuthorizationService
) : ViewModel() {

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String>
        get() = _email

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name: StateFlow<String>
        get() = _name

    private val _exp: MutableStateFlow<String> = MutableStateFlow("")
    val exp: StateFlow<String>
        get() = _exp

    init {
        setValuesUI()
    }

    fun logOut(onLaunch: (Intent) -> Unit) {
        val authState = authStateManager.getCurrent()
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

    fun signOut() {
        val authState = authStateManager.getCurrent()
        val clearedState = AuthState(authState.authorizationServiceConfiguration!!)
        if (authState.lastRegistrationResponse != null) {
            clearedState.update(authState.lastRegistrationResponse)
        }
        authStateManager.replace(clearedState)
    }

    fun refreshToken() {
        val clientAuthentication = authStateManager.getCurrent().clientAuthentication
        authorizationService.performTokenRequest(
            authStateManager.getCurrent().createTokenRefreshRequest(),
            clientAuthentication
        ) { tokenResponse, authorizationException ->
            if (tokenResponse != null) {
                authStateManager.updateAfterTokenResponse(tokenResponse, authorizationException)
                setValuesUI()
            }
        }
    }

    private fun convertLongToTime(time: Long): String {
        val date = Date(time * 1000)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return format.format(date)
    }

    private fun setValuesUI() {
        val jwt = JWT(authStateManager.getCurrent().accessToken!!)
        jwt.claims["email"]?.let {
            _email.value = it.asString()!!
        }
        jwt.claims["name"]?.let {
            _name.value = it.asString()!!
        }
        jwt.claims["exp"]?.let {
            _exp.value = convertLongToTime(it.asLong()!!)
        }
    }
}