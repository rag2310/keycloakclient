package com.rago.keycloakclient.ui.token

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.rago.keycloakclient.repositories.auth.AuthRepository
import com.rago.keycloakclient.utils.roles.RolesEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val repository: AuthRepository
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

    private val _tokenUIState: MutableStateFlow<TokenUIState> =
        MutableStateFlow(TokenUIState.NotState)
    val tokenUIState: StateFlow<TokenUIState>
        get() = _tokenUIState

    init {
        setValuesUI()
    }

    fun logOut(onLaunch: (Intent) -> Unit) {
        repository.logOut(onLaunch)
    }

    fun signOut() {
        repository.signOut()
    }

    fun refreshToken() {
        repository.refreshToken { tokenResponse, authorizationException ->
            if (tokenResponse != null) {
                repository.updateAfterTokenResponse(tokenResponse, authorizationException)
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
        val jwt = JWT(repository.getToken())
        jwt.claims.forEach { (s, claim) ->
            Log.i(TAG, "setValuesUI: $s: ${claim.asString()}")
        }
        jwt.claims["roles"]?.let { roles ->
            val value = roles.asString()
            val formatRoles = value!!.substring(1, value.length - 1)
            repository.buildRoles(formatRoles.split(','))
        }
        jwt.claims["email"]?.let {
            _email.value = it.asString()!!
        }
        jwt.claims["name"]?.let {
            _name.value = it.asString()!!
        }
        jwt.claims["exp"]?.let {
            _exp.value = convertLongToTime(it.asLong()!!)
        }

        _tokenUIState.value = TokenUIState.Permissions(
            repository.permissionToView(RolesEnum.BOOKING),
            repository.permissionToView(RolesEnum.SHIPPER)
        )
    }



    companion object {
        private const val TAG = "TokenViewModel"
    }
}