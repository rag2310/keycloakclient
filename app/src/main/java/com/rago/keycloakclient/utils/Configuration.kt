package com.rago.keycloakclient.utils

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.net.Uri
import org.json.JSONObject
import java.lang.ref.WeakReference


class Configuration(private val context: Context) {

    companion object {
        private const val TAG = "Configuration"

        private const val PREFS_NAME = "config"
        private const val KEY_LAST_HASH = "lastHash"

        private val sInstance: WeakReference<Configuration> = WeakReference(null)
    }

    private var mContext: Context? = null
    private var mPrefs: SharedPreferences? = null
    private var mResources: Resources? = null

    private val mConfigJson: JSONObject? = null
    private val mConfigHash: String? = null
    private var mConfigError: String? = null

    private var mClientId: String? = null
    private val mScope: String? = null
    private val mRedirectUri: Uri? = null
    private val mEndSessionRedirectUri: Uri? = null
    private val mDiscoveryUri: Uri? = null
    private val mAuthEndpointUri: Uri? = null
    private val mTokenEndpointUri: Uri? = null
    private val mEndSessionEndpoint: Uri? = null
    private val mRegistrationEndpointUri: Uri? = null
    private val mUserInfoEndpointUri: Uri? = null
    private val mHttpsRequired = false

    init {
        mContext = context
        mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        mResources = context.resources

        try {
            readConfiguration()
        } catch (ex: InvalidConfigurationException) {
            mConfigError = ex.message
        }
    }

    private fun readConfiguration() {
        mClientId = "account"
    }

    inner class InvalidConfigurationException constructor(reason: String) : Exception(reason) {

        constructor(reason: String, cause: Throwable) : this(reason) {
            throw Exception(reason, cause)
        }
    }
}