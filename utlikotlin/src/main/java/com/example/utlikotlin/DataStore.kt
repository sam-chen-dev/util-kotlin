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

object DataStore {
    private val Context.settings by preferencesDataStore("settings")

    /*String*/
    suspend fun saveString(context: Context, keyResId: Int, value: String) {
        val key = stringPreferencesKey(context.getString(keyResId))

        context.settings.edit {
            it[key] = value
        }
    }

    fun getStringFlow(context: Context, keyResId: Int): Flow<String?> {
        val key = stringPreferencesKey(context.getString(keyResId))

        return context.settings.data.map {
            it[key]
        }
    }

    fun getString(context: Context, keyResId: Int): String? {
        val key = stringPreferencesKey(context.getString(keyResId))

        return runBlocking {
            context.settings.data.first()[key]
        }
    }

    /*Boolean*/
    suspend fun saveBoolean(context: Context, keyResId: Int, value: Boolean) {
        val key = booleanPreferencesKey(context.getString(keyResId))

        context.settings.edit {
            it[key] = value
        }
    }

    fun getBooleanFlow(context: Context, keyResId: Int): Flow<Boolean?> {
        val key = booleanPreferencesKey(context.getString(keyResId))

        return context.settings.data.map {
            it[key]
        }
    }

    fun getBoolean(context: Context, keyResId: Int): Boolean? {
        val key = booleanPreferencesKey(context.getString(keyResId))

        return runBlocking {
            context.settings.data.first()[key]
        }
    }

    /*Int*/
    suspend fun saveInt(context: Context, keyResId: Int, value: Int) {
        val key = intPreferencesKey(context.getString(keyResId))

        context.settings.edit {
            it[key] = value
        }
    }

    fun getIntFlow(context: Context, keyResId: Int): Flow<Int?> {
        val key = intPreferencesKey(context.getString(keyResId))

        return context.settings.data.map {
            it[key]
        }
    }

    fun getInt(context: Context, keyResId: Int): Int? {
        val key = intPreferencesKey(context.getString(keyResId))

        return runBlocking {
            context.settings.data.first()[key]
        }
    }

    /*Long*/
    suspend fun saveLong(context: Context, keyResId: Int, value: Long) {
        val key = longPreferencesKey(context.getString(keyResId))

        context.settings.edit {
            it[key] = value
        }
    }

    suspend fun saveLong(context: Context, keyResId: Int, valueResId: Int) {
        val key = longPreferencesKey(context.getString(keyResId))
        val value = context.resources.getInteger(valueResId).toLong()

        context.settings.edit {
            it[key] = value
        }
    }

    fun getLongFlow(context: Context, keyResId: Int): Flow<Long?> {
        val key = longPreferencesKey(context.getString(keyResId))

        return context.settings.data.map {
            it[key]
        }
    }

    fun getLong(context: Context, keyResId: Int): Long? {
        val key = longPreferencesKey(context.getString(keyResId))

        return runBlocking {
            context.settings.data.first()[key]
        }
    }
}