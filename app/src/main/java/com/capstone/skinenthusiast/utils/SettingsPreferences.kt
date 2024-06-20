package com.capstone.skinenthusiast.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SettingsPreferences.PREFERENCES_NAME)

class SettingsPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getToken() =
        dataStore.data.map {
            it[stringPreferencesKey(TOKEN_SETTINGS_PREFERENCES)] ?: PREFERENCE_DEFAULT_VALUE
        }

    fun getName() =
        dataStore.data.map {
            it[stringPreferencesKey(NAME_SETTINGS_PREFERENCES)] ?: PREFERENCE_DEFAULT_VALUE
        }

    fun getEmail() =
        dataStore.data.map {
            it[stringPreferencesKey(EMAIL_SETTINGS_PREFERENCES)] ?: PREFERENCE_DEFAULT_VALUE
        }

    fun getAvatar() =
        dataStore.data.map {
            it[stringPreferencesKey(AVATAR_SETTINGS_PREFERENCES)] ?: PREFERENCE_DEFAULT_VALUE
        }

    fun getGender() =
        dataStore.data.map {
            it[stringPreferencesKey(GENDER_SETTINGS_PREFERENCES)] ?: PREFERENCE_DEFAULT_VALUE
        }

    suspend fun saveAccountData(
        name: String,
        email: String,
        gender: String,
        avatar: String,
    ) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(NAME_SETTINGS_PREFERENCES)] = name
            prefs[stringPreferencesKey(EMAIL_SETTINGS_PREFERENCES)] = email
            prefs[stringPreferencesKey(GENDER_SETTINGS_PREFERENCES)] = gender
            prefs[stringPreferencesKey(AVATAR_SETTINGS_PREFERENCES)] = avatar
        }
    }

    suspend fun saveToken(
        token: String
    ) {
        dataStore.edit { prefs ->
            prefs[stringPreferencesKey(TOKEN_SETTINGS_PREFERENCES)] = token
        }
    }

    suspend fun clearPreferences() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        private const val TOKEN_SETTINGS_PREFERENCES = "token_preferences"

        private const val NAME_SETTINGS_PREFERENCES = "name_preferences"
        private const val EMAIL_SETTINGS_PREFERENCES = "email_preferences"
        private const val GENDER_SETTINGS_PREFERENCES = "gender_preferences"
        private const val AVATAR_SETTINGS_PREFERENCES = "avatar_preferences"

        const val PREFERENCES_NAME = "settings_preferences"
        const val PREFERENCE_DEFAULT_VALUE = "preference_default_value"

        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            val instance = SettingsPreferences(dataStore)
            INSTANCE = instance
            instance
        }
    }
}