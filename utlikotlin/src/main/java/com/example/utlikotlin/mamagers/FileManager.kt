package com.example.utlikotlin.mamagers

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileManager(context: Context) {
    companion object {
        private const val TAG = "FileManager"
    }

    private val applicationContext = context.applicationContext
    private val contentResolver = applicationContext.contentResolver

    suspend fun createFile(
        fileName: String,
        directory: String,
        content: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/$directory/")
            }

            val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                ?: return@withContext Result.failure(Exception("Failed to insert file"))

            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }

            Result.success(uri)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteFile(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val deletedCount = contentResolver.delete(uri, null, null)

            if (deletedCount == 0) {
                return@withContext Result.failure(Exception("Failed to delete file or file not found"))
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun readFile(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val text = contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                ?: return@withContext Result.failure(Exception("Invalid uri"))

            Result.success(text)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun listFileUris(directory: String): Result<List<Uri>> = withContext(Dispatchers.IO) {
        try {
            val externalUri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(MediaStore.MediaColumns._ID)
            val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ?"
            val selectionArgs = arrayOf(Environment.DIRECTORY_DOCUMENTS + "/$directory/")

            contentResolver.query(
                externalUri,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
                val uris = buildList {
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val uri = ContentUris.withAppendedId(externalUri, id)

                        add(uri)
                    }
                }

                Result.success(uris)
            } ?: Result.failure(Exception("Query returned null cursor"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }
}