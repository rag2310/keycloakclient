package com.rago.keycloakclient.repositories.petitions

import com.rago.keycloakclient.network.KeycloackApiService
import com.rago.keycloakclient.network.model.AuthenticatorProviders
import com.rago.keycloakclient.utils.AuthStateManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PetitionsRepository @Inject constructor(
    private val keycloackApiService: KeycloackApiService
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    fun get(): Flow<List<AuthenticatorProviders>> = callbackFlow {
        val callback = object : Callback<List<AuthenticatorProviders>> {
            override fun onResponse(
                call: Call<List<AuthenticatorProviders>>,
                response: Response<List<AuthenticatorProviders>>
            ) {
                val list = response.body()
                if (list != null) {
                    trySend(list)
                }
            }

            override fun onFailure(call: Call<List<AuthenticatorProviders>>, t: Throwable) {
                cancel(t.message!!, t)
            }
        }
        keycloackApiService.get().enqueue(callback)

        awaitClose {
        }
    }

}