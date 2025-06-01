package com.example.utlikotlin

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitHelper {
    private val contentType = "application/json".toMediaType()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getRetrofit(baseUrl: String) = Retrofit.Builder().run {
        addConverterFactory(json.asConverterFactory(contentType))
        baseUrl(baseUrl)
        build()
    }

    fun <T> create(baseUrl: String, apiInterface: Class<T>) = getRetrofit(baseUrl).create(apiInterface)
}