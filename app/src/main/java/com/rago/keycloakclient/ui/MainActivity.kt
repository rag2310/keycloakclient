package com.rago.keycloakclient.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.rago.keycloakclient.R
import com.rago.keycloakclient.databinding.ActivityMainBinding
import com.rago.keycloakclient.utils.AuthStateManager
import com.rago.keycloakclient.utils.AuthorizationServiceManager
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mStateManager: AuthStateManager

    @Inject
    lateinit var mAuthorizationServiceConfiguration: AuthorizationServiceManager

    companion object {
        private val RC_AUTH = 100
    }

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAuthService: AuthorizationService
    private lateinit var token: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mAuthService = AuthorizationService(this)

        if (mStateManager.getCurrent().isAuthorized) {
            Log.d("Auth", "Done")
            mBinding.buttonLogin.text = "Logout"
            mStateManager.getCurrent().performActionWithFreshTokens(
                mAuthService
            ) { accessToken, idToken, exception ->
                Log.d("Auth", "4 token = $accessToken")
                accessToken?.let {
                    token = it
                }
            }
        }

        mBinding.buttonLogin.setOnClickListener {
            if (mStateManager.getCurrent().isAuthorized) {
                val currentState = mStateManager.getCurrent()
                val config = currentState.authorizationServiceConfiguration
                val endSessionRequest =
                    EndSessionRequest.Builder(config!!)
                        .setPostLogoutRedirectUri(Uri.parse("com.rago.keycloakclient:/logout"))
                        .setAdditionalParameters(
                            mutableMapOf(
                                "client_id" to "curso-web",
                                "refresh_token" to currentState.refreshToken
                            )
                        )
                        .build()

                val intent = mAuthService.getEndSessionRequestIntent(endSessionRequest)
                startActivityForResult(intent, RC_AUTH + 1)

            } else {

                val clientId = "curso-web"
//                val clientId = "account-console"
                val redirectUri = Uri.parse("com.rago.keycloakclient:/oauth2redirect")
                val builder = AuthorizationRequest.Builder(
                    mAuthorizationServiceConfiguration.getAuthorizationServiceConfiguration(),
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
                ) { resp, ex ->
                    if (resp != null) {
                        mStateManager.updateAfterTokenResponse(resp, ex)
                        mBinding.buttonLogin.text = "Logout"
                        Log.d("Auth", "1 tokenAccess = ${resp.accessToken}")
                        resp.accessToken?.let {
                            token = it
                        }
                    } else {
                        Log.d("Auth", "not tokenAccess")
                    }
                }
                Log.d("Auth", "2 tokenAccess = ${resp.accessToken}")
                resp.accessToken?.let {
                    token = it
                }
            } else {
                Log.d("Auth", "not tokenAccess")
            }

            if (mStateManager.getCurrent().isAuthorized) {
                Log.d("Auth", "Done")
                mBinding.buttonLogin.text = "Logout"
                mStateManager.getCurrent().performActionWithFreshTokens(
                    mAuthService
                ) { token, idToken, exception ->
                    Log.d("Auth", "3 tokenAccess $token")
                    token?.let {
                        this.token = it
                    }
                }
            }
        }

        if (requestCode == RC_AUTH + 1) {
            mBinding.buttonLogin.text = "LogIn"
            token = ""
            signOut()
        }
    }

    private fun signOut() {
        val currentState = mStateManager.getCurrent()
        val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)

        if (currentState.lastRegistrationResponse != null) {
            clearedState.update(currentState.lastRegistrationResponse)
        }

        mStateManager.replace(clearedState)

        Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
    }
}