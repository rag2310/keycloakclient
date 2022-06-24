package com.rago.keycloakclient.utils.model

import com.google.gson.annotations.SerializedName

data class ResourceAccess(
    @SerializedName("login-app")
    val loginApp: IdClient?
)