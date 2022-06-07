package com.rago.keycloakclient.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.AnyThread
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import net.openid.appauth.*
import org.json.JSONException
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock


class AuthStateManager(private val context: Context) {
    companion object {
        private const val TAG: String = "AuthStateManager"
        private const val STORE_NAME = "AuthState"
        private const val KEY_STATE = "state"
    }

    private var INSTANCE_REF = AtomicReference(WeakReference(null))

    private var mPrefs: SharedPreferences? = null
    private var mPrefsLock: ReentrantLock? = null
    private var mCurrentAuthState: AtomicReference<AuthState>? = null

    init {
        mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
        mPrefsLock = ReentrantLock()
        mCurrentAuthState = AtomicReference()
    }

    /*@AnyThread
    fun getInstance(): AuthStateManager {
        var manager: AuthStateManager? = INSTANCE_REF.get().get()
        if (manager == null) {
            manager = AuthStateManager(context.applicationContext)
        }
        return manager
    }*/

    @AnyThread
    @NonNull
    fun getCurrent(): AuthState {
        if (mCurrentAuthState!!.get() != null) {
            return mCurrentAuthState!!.get()
        }

        val state: AuthState = readState()
        return if (mCurrentAuthState!!.compareAndSet(null, state)) {
            state
        } else {
            mCurrentAuthState!!.get()
        }
    }

    @AnyThread
    @NonNull
    fun replace(@NonNull state: AuthState): AuthState {
        writeState(state)
        mCurrentAuthState?.set(state)
        return state
    }

    @AnyThread
    @NonNull
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = getCurrent()
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    @NonNull
    fun updateAfterTokenResponse(response: TokenResponse, ex: AuthorizationException?): AuthState {
        val current = getCurrent()
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    @NonNull
    fun updateAfterRegistration(
        response: RegistrationResponse,
        ex: AuthorizationException?
    ): AuthState {
        val current = getCurrent()
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }

    @AnyThread
    @NonNull
    private fun readState(): AuthState {
        mPrefsLock?.lock()
        try {
            val currentState = mPrefs?.getString(KEY_STATE, null) ?: return AuthState()
            try {
                return AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                Log.w(TAG, "Failed to deserialize stored auth state - discarding")
                return AuthState()
            }
        } finally {
            mPrefsLock?.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        mPrefsLock?.lock()
        try {
            val editor: SharedPreferences.Editor = mPrefs!!.edit()
            if (state == null) {
                editor.remove(KEY_STATE)
            } else {
                editor.putString(KEY_STATE, state.jsonSerializeString())
            }

            if (!editor.commit()) {
                throw IllegalStateException("Failed to write state to shared prefs");
            }
        } finally {
            mPrefsLock?.unlock()
        }
    }
}