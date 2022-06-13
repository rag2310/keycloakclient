package com.rago.keycloakclient.ui.login

import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _loginUIState: MutableStateFlow<LoginUIState> =
        MutableStateFlow(LoginUIState.NotState)
    val loginUIState: StateFlow<LoginUIState>
        get() = _loginUIState

    fun isAuthorized() {
        viewModelScope.launch {
            _loginUIState.value = LoginUIState.Loading
            if (repository.isAuthorized()) {
                repository.authorized { accessToken, _, exception ->
                    if (exception != null) {
                        repository.signOut()
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
            repository.login(onLaunch)
        }
    }

    fun resultLogin(intent: Intent?) {
        _loginUIState.value = LoginUIState.Loading
        viewModelScope.launch {
            repository.resultLogin(intent) { tokenResponse, exception ->
                if (tokenResponse != null) {
                    repository.updateAfterTokenResponse(
                        tokenResponse,
                        exception
                    )
                    _loginUIState.value = LoginUIState.Authorized
                } else {
                    _loginUIState.value =
                        LoginUIState.Error("authentication: not authentication")
                }
            }
        }
    }

    fun test() {
        viewModelScope.launch {
            Log.i("AuthRepository","test: ${Date()}")
            repository.test()
        }
    }
}