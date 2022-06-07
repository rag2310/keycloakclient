package com.rago.keycloakclient

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.rago.keycloakclient.databinding.ActivityMainBinding
import com.rago.keycloakclient.utils.AuthStateManager
import com.rago.keycloakclient.utils.Configuration
import net.openid.appauth.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        private val RC_AUTH = 100
    }

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mAuthService: AuthorizationService
    private lateinit var mStateManager: AuthStateManager

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mStateManager = AuthStateManager(this).getInstance()
        mAuthService = AuthorizationService(this)

        if (mStateManager.getCurrent().isAuthorized) {
            Log.d("Auth", "Done")
            mBinding.buttonLogin.text = "Logout"
            mStateManager.getCurrent().performActionWithFreshTokens(
                mAuthService
            ) { accessToken, idToken, exception ->
                Log.d("Auth", "token = $accessToken")
            }
        }

        mBinding.buttonLogin.setOnClickListener {
            if (mStateManager.getCurrent().isAuthorized) {

            } else {
                val serviceConfig = AuthorizationServiceConfiguration(
                    Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/auth"),
                    Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/token")
                )

                val clientId = "account-console"
                val redirectUri = Uri.parse("com.rago.keycloakclient:/oauth2callback")
                val builder = AuthorizationRequest.Builder(
                    serviceConfig,
                    clientId,
                    ResponseTypeValues.CODE,
                    redirectUri
                )
                builder.setScope("openid")

                val authRequest = builder.build()
                val authService = AuthorizationService(this)
                val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                startActivityForResult(authIntent, RC_AUTH)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            val resp = AuthorizationResponse.fromIntent(data!!)
            val ex = AuthorizationException.fromIntent(data)

            if (resp != null) {
                mAuthService = AuthorizationService(this)
                mStateManager.updateAfterAuthorization(resp, ex)

                mAuthService.performTokenRequest(
                    resp.createTokenExchangeRequest()
                ) { _resp, _ex ->
                    if (_resp != null) {
                        mStateManager.updateAfterTokenResponse(_resp, _ex)
                        mBinding.buttonLogin.text = "Logout"
                        Log.d("Auth", "tokenAccess = ${resp.accessToken}")
                    } else {
                        Log.d("Auth", "not tokenAccess")
                    }
                }
                Log.d("Auth", "tokenAccess = ${resp.accessToken}")
            } else {
                Log.d("Auth", "not tokenAccess")
            }

            if (mStateManager.getCurrent().isAuthorized) {
                Log.d("Auth", "Done")
                mBinding.buttonLogin.text = "Logout"
                mStateManager.getCurrent().performActionWithFreshTokens(
                    mAuthService
                ) { token, idToken, exception ->
                    Log.d("Auth", "tokenAccess $token")

                }
            }
        }
    }
}