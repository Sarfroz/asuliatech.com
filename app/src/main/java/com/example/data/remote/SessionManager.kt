package com.example.data.remote

import android.content.Context
import android.content.SharedPreferences

/**
 * SessionManager handles persistent storage for JWT token and user session data.
 */
object SessionManager {

    private const val PREF_NAME = "asulia_parent_session"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_SELECTED_CARD_ID = "selected_card_id"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val token = prefs?.getString(KEY_AUTH_TOKEN, null)
            if (!token.isNullOrEmpty()) {
                ApiConfig.authToken = token
            }
        }
    }

    fun saveAuthToken(token: String) {
        ApiConfig.authToken = token
        prefs?.edit()?.putString(KEY_AUTH_TOKEN, token)?.apply()
    }

    fun getAuthToken(): String? {
        val token = prefs?.getString(KEY_AUTH_TOKEN, null)
        if (token != null && ApiConfig.authToken == null) {
            ApiConfig.authToken = token
        }
        return token
    }

    fun saveSelectedCardId(cardId: String) {
        prefs?.edit()?.putString(KEY_SELECTED_CARD_ID, cardId)?.apply()
    }

    fun getSelectedCardId(): String? {
        return prefs?.getString(KEY_SELECTED_CARD_ID, null)
    }

    fun clearSession() {
        ApiConfig.authToken = null
        prefs?.edit()?.clear()?.apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }
}
