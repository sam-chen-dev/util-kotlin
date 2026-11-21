package com.example.utlikotlin.mamagers

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class InternalFileManager(context: Context) {
    companion object {
        private const val TAG = "InternalFileManager"
    }

    private val applicationContext = context.applicationContext

    suspend fun createFile(
        fileName: String,
        fileExtension: String,
        directory: String,
        content: String
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val targetDirectory = File(applicationContext.filesDir, directory)

            if (!targetDirectory.exists()) {
                targetDirectory.mkdirs()
            }

            val filePath = "$directory/$fileName.$fileExtension"
            val file = File(applicationContext.filesDir, filePath)

            file.writeText(content)

            Result.success(file)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteFile(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (!file.delete()) {
                return@withContext Result.failure(Exception("File not found or failed to delete file: ${file.path}"))
            }

            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun readFile(file: File): Result<String> = withContext(Dispatchers.IO) {
        try {
            Result.success(file.readText())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun listFiles(directory: String): Result<List<File>> = withContext(Dispatchers.IO) {
        try {
            val targetDirectory = File(applicationContext.filesDir, directory)

            if (!targetDirectory.exists()) {
                return@withContext Result.failure(Exception("Directory not found: ${targetDirectory.path}"))
            }

            if (!targetDirectory.isDirectory) {
                return@withContext Result.failure(Exception("Path is not a directory: ${targetDirectory.path}"))
            }

            val files = targetDirectory.listFiles()
                ?: return@withContext Result.failure(Exception("Failed to read directory contents: ${targetDirectory.path}"))

            Result.success(files.toList())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Result.failure(e)
        }
    }
}