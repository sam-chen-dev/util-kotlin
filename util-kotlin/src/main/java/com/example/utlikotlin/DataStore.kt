package com.example.utlikotlin

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class DataStore(context: Context) {
    private val applicationContext = context.applicationContext
    private val Context.settings by preferencesDataStore("settings")

    /*String*/
    suspend fun saveString(key: String, value: String) {
        val key = stringPreferencesKey(key)

        applicationContext.settings.edit {
            it[key] = value
        }
    }

    fun getStringFlow(key: String): Flow<String?> {
        val key = stringPreferencesKey(key)

        return applicationContext.settings.data.map {
            it[key]
        }
    }

    fun getString(key: String): String? {
        val key = stringPreferencesKey(key)

        return runBlocking {
            applicationContext.settings.data.first()[key]
        }
    }

    /*Boolean*/
    suspend fun saveBoolean(key: String, value: Boolean) {
        val key = booleanPreferencesKey(key)

        applicationContext.settings.edit {
            it[key] = value
        }
    }

    fun getBooleanFlow(key: String): Flow<Boolean?> {
        val key = booleanPreferencesKey(key)

        return applicationContext.settings.data.map {
            it[key]
        }
    }

    fun getBoolean(key: String): Boolean? {
        val key = booleanPreferencesKey(key)

        return runBlocking {
            applicationContext.settings.data.first()[key]
        }
    }

    /*Int*/
    suspend fun saveInt(key: String, value: Int) {
        val key = intPreferencesKey(key)

        applicationContext.settings.edit {
            it[key] = value
        }
    }

    fun getIntFlow(key: String): Flow<Int?> {
        val key = intPreferencesKey(key)

        return applicationContext.settings.data.map {
            it[key]
        }
    }

    fun getInt(key: String): Int? {
        val key = intPreferencesKey(key)

        return runBlocking {
            applicationContext.settings.data.first()[key]
        }
    }

    /*Long*/
    suspend fun saveLong(key: String, value: Long) {
        val key = longPreferencesKey(key)

        applicationContext.settings.edit {
            it[key] = value
        }
    }

    fun getLongFlow(key: String): Flow<Long?> {
        val key = longPreferencesKey(key)

        return applicationContext.settings.data.map {
            it[key]
        }
    }

    fun getLong(key: String): Long? {
        val key = longPreferencesKey(key)

        return runBlocking {
            applicationContext.settings.data.first()[key]
        }
    }
}