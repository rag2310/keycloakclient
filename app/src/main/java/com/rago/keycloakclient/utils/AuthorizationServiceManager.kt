package com.rago.keycloakclient.utils

import android.net.Uri
import net.openid.appauth.AuthorizationServiceConfiguration

class AuthorizationServiceManager {
    private val authorizationServiceConfiguration: AuthorizationServiceConfiguration =
        AuthorizationServiceConfiguration(
            Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/auth"),
            Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/token"),
            null,
            Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/logout")
        )

    fun getAuthorizationServiceConfiguration(): AuthorizationServiceConfiguration {
        return authorizationServiceConfiguration
    }
}