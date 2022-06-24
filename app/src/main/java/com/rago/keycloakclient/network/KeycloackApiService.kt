package com.rago.keycloakclient.network

import com.rago.keycloakclient.network.model.AuthenticatorProviders
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface KeycloackApiService {
    @GET("admin/realms/trackchain/authentication/authenticator-providers")
    fun get(
        /*@Header("Authorization") token: String*/
    ): Call<List<AuthenticatorProviders>>
}