package com.example.plutoaccessories.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = prefs.edit()
    }

    fun createLoginSession(idUser: Int, nama: String?, email: String?, role: String?, alamat: String?, noHp: String?) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_ID, idUser)
        editor.putString(KEY_NAMA, nama)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_ALAMAT, alamat)
        editor.putString(KEY_NO_HP, noHp)
        editor.commit()
    }

    val isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    val role: String
        get() = prefs.getString(KEY_ROLE, "user")!!

    val userId: Int
        get() = prefs.getInt(KEY_ID, -1)

    val userDetails: HashMap<String, String?>
        get() {
            val user = HashMap<String, String?>()
            user[KEY_NAMA] = prefs.getString(KEY_NAMA, null)
            user[KEY_EMAIL] = prefs.getString(KEY_EMAIL, null)
            user[KEY_ALAMAT] = prefs.getString(KEY_ALAMAT, null)
            user[KEY_NO_HP] = prefs.getString(KEY_NO_HP, null)
            return user
        }

    fun logout() {
        editor.clear()
        editor.commit()
    }

    companion object {
        private const val PREF_NAME = "pluto_session"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_ID = "id_user"
        const val KEY_NAMA = "nama"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
        const val KEY_ALAMAT = "alamat"
        const val KEY_NO_HP = "no_hp"
    }
}