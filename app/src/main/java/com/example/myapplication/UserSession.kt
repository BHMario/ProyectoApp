package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

/**
 * Stores the active session in SharedPreferences (who is logged in).
 * All user/order data lives in SQLite via DatabaseHelper.
 */
object UserSession {
    private const val PREFS_NAME    = "mangup_session"
    private const val KEY_USER_ID   = "user_id"
    private const val KEY_NAME      = "user_name"
    private const val KEY_EMAIL     = "user_email"
    private const val KEY_PHONE     = "user_phone"
    private const val KEY_IS_ADMIN  = "is_admin"
    private const val KEY_LOGGED_IN = "is_logged_in"
    private const val KEY_PHOTO_URI = "photo_uri"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun startSession(context: Context, user: User) {
        prefs(context).edit()
            .putInt(KEY_USER_ID, user.id)
            .putString(KEY_NAME, user.name)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PHONE, user.phone)
            .putBoolean(KEY_IS_ADMIN, user.isAdmin)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun logout(context: Context) {
        prefs(context).edit().clear().apply()
    }

    /** Returns true only if a valid DB-backed session exists (userId != -1). */
    fun isLoggedIn(context: Context): Boolean =
        prefs(context).getBoolean(KEY_LOGGED_IN, false) &&
        prefs(context).getInt(KEY_USER_ID, -1) != -1

    fun getUserId(context: Context): Int =
        prefs(context).getInt(KEY_USER_ID, -1)

    fun getName(context: Context): String =
        prefs(context).getString(KEY_NAME, "Usuario") ?: "Usuario"

    fun getEmail(context: Context): String =
        prefs(context).getString(KEY_EMAIL, "") ?: ""

    fun getPhone(context: Context): String =
        prefs(context).getString(KEY_PHONE, "") ?: ""

    fun isAdmin(context: Context): Boolean =
        prefs(context).getBoolean(KEY_IS_ADMIN, false)

    fun savePhotoUri(context: Context, uri: String) {
        prefs(context).edit().putString(KEY_PHOTO_URI, uri).apply()
    }

    fun getPhotoUri(context: Context): String =
        prefs(context).getString(KEY_PHOTO_URI, "") ?: ""

    fun updateName(context: Context, newName: String) {
        prefs(context).edit().putString(KEY_NAME, newName).apply()
    }

    fun updatePhone(context: Context, newPhone: String) {
        prefs(context).edit().putString(KEY_PHONE, newPhone).apply()
    }
}

