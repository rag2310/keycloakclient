package com.rago.keycloakclient.utils

import android.content.Context
import android.content.SharedPreferences
import net.openid.appauth.AuthState
import org.json.JSONException

class KeyCloakClientSharedPreferences(
    context: Context
) {

    companion object {
        private const val STORE_NAME = "AuthState"
        private const val KEY_STATE = "state"
    }

    private val mPrefs: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readState(): AuthState {
        val currentState = mPrefs.getString(KEY_STATE, null) ?: return AuthState()
        return try {
            AuthState.jsonDeserialize(currentState)
        } catch (ex: JSONException) {
            AuthState()
        }
    }

    fun writeState(state: AuthState?) {
        val editor: SharedPreferences.Editor = mPrefs.edit()
        if (state == null) {
            editor.remove(KEY_STATE)
        } else {
            editor.putString(KEY_STATE, state.jsonSerializeString())
        }

        if (!editor.commit()) {
            throw IllegalStateException("Failed to write state to shared prefs");
        }
    }
}