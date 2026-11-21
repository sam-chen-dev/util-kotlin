package com.example.utlikotlin

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.UUID

suspend fun CredentialManager.signInWithGoogle(context: Context, serverClientId: String): GoogleIdTokenCredential {
    val googleIdOption = GetGoogleIdOption.Builder().run {
        setFilterByAuthorizedAccounts(false)
        setServerClientId(serverClientId)
        setNonce(UUID.randomUUID().toString())
        build()
    }

    val credentialRequest = GetCredentialRequest.Builder().run {
        addCredentialOption(googleIdOption)
        build()
    }

    val result = getCredential(context, credentialRequest)
    val credential = result.credential

    return GoogleIdTokenCredential.createFrom(credential.data)
}

suspend fun CredentialManager.signOut() = clearCredentialState(ClearCredentialStateRequest())