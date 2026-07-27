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
    private const val KEY_CACHED_STUDENTS = "cached_students_json"
    private const val KEY_CACHED_USER = "cached_user_json"

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

    fun saveCachedStudents(json: String) {
        prefs?.edit()?.putString(KEY_CACHED_STUDENTS, json)?.apply()
    }

    fun getCachedStudents(): String? {
        return prefs?.getString(KEY_CACHED_STUDENTS, null)
    }

    fun saveCachedUser(json: String) {
        prefs?.edit()?.putString(KEY_CACHED_USER, json)?.apply()
    }

    fun getCachedUser(): String? {
        return prefs?.getString(KEY_CACHED_USER, null)
    }

    fun clearSession() {
        ApiConfig.authToken = null
        prefs?.edit()?.clear()?.apply()
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrEmpty()
    }
}
