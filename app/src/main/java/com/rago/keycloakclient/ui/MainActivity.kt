package com.rago.keycloakclient.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.rago.keycloakclient.R
import com.rago.keycloakclient.databinding.ActivityMainBinding
import com.rago.keycloakclient.utils.AuthStateManager
import dagger.hilt.android.AndroidEntryPoint
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationService
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mStateManager: AuthStateManager

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mAuthService: AuthorizationService
    private lateinit var token: String

    /*private val authentication =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult = result.data
            if (intentResult != null) {
                var uriResult = intentResult.data
                val oauth2Redirect = Uri.parse("com.rago.keycloakclient:/oauth2redirect")
                val logoutRedirect = Uri.parse("com.rago.keycloakclient:/logout")
                val indexUri = uriResult.toString().indexOf("?", 0)
                uriResult = Uri.parse(uriResult.toString().substring(0, indexUri))
                if (uriResult != null) {
                    when (uriResult) {
                        oauth2Redirect -> {
                            val authorizationResponse =
                                AuthorizationResponse.fromIntent(intentResult)
                            val authorizationException =
                                AuthorizationException.fromIntent(intentResult)
                            if (authorizationResponse != null) {
                                mAuthService = AuthorizationService(this)
                                mStateManager.updateAfterAuthorization(
                                    authorizationResponse,
                                    authorizationException
                                )
                                mAuthService.performTokenRequest(
                                    authorizationResponse.createTokenExchangeRequest()
                                ) { tokenResponse, exception ->
                                    if (tokenResponse != null) {
                                        mStateManager.updateAfterTokenResponse(
                                            tokenResponse,
                                            exception
                                        )
                                        mBinding.buttonLogin.text = "LogOut"
                                        tokenResponse.accessToken?.let { accessToken ->
                                            token = accessToken
                                            val jwt = JWT(accessToken)
                                            Log.i(
                                                TAG,
                                                "jwt: ${jwt.claims.getValue("email").asString()}"
                                            )
                                        }
                                    } else {
                                        Log.i(TAG, "authentication: not authentication")
                                    }
                                }
                            } else {
                                Log.i(TAG, "authentication: not authentication")
                            }
                        }
                        logoutRedirect -> {
                            mBinding.buttonLogin.text = "LogIn"
                            token = ""
                            signOut()
                        }
                    }
                } else {
                    Log.i(TAG, "registerForActivityResult: uri null")
                }
            } else {
                Log.i(TAG, "registerForActivityResult: intent null")
            }
        }*/

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        /*mAuthService = AuthorizationService(this)

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

                val endIntent = mAuthService.getEndSessionRequestIntent(endSessionRequest)
                authentication.launch(endIntent)
            } else {

                val clientId = "curso-web"
                val authorizationServiceConfiguration =
                    AuthorizationServiceConfiguration(
                        Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/auth"),
                        Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/token"),
                        null,
                        Uri.parse("https://sso-dev.trackchain.io/auth/realms/trackchain/protocol/openid-connect/logout")
                    )
                val redirectUri = Uri.parse("com.rago.keycloakclient:/oauth2redirect")

                val builder = AuthorizationRequest.Builder(
                    authorizationServiceConfiguration,
                    clientId,
                    ResponseTypeValues.CODE,
                    redirectUri
                )
                builder.setScope("openid")

                val authRequest = builder.build()
                val authService = AuthorizationService(this)
                val authIntent = authService.getAuthorizationRequestIntent(authRequest)
                authentication.launch(authIntent)
            }
        }*/
    }

    private fun signOut() {
        val currentState = mStateManager.getInstance()
        val clearedState = AuthState(currentState.authorizationServiceConfiguration!!)

        if (currentState.lastRegistrationResponse != null) {
            clearedState.update(currentState.lastRegistrationResponse)
        }

        mStateManager.replace(clearedState)

        Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
    }
}