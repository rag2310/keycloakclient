package com.rago.keycloakclient.ui.petitions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rago.keycloakclient.network.model.AuthenticatorProviders
import com.rago.keycloakclient.repositories.petitions.PetitionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class PetitionsViewModel @Inject constructor(
    private val repository: PetitionsRepository
) : ViewModel() {
    val list: StateFlow<List<AuthenticatorProviders>> = flow {
        emitAll(repository.get())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = listOf()
    )
}