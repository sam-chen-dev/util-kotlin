package com.example.utlikotlin

import android.content.Context
import android.net.Uri
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import java.util.UUID

fun AmazonS3Client.uploadPhoto(
    context: Context,
    uri: Uri,
    bucketName: String,
    region: Region,
    onUploaded: (String) -> Unit,
    onError: (Exception) -> Unit
) {
    val fileName = "photos/${UUID.randomUUID()}-${System.currentTimeMillis()}.png"
    val fileUrl = "https://$bucketName.s3.${region.name}.amazonaws.com/$fileName"

    val transferUtility = TransferUtility.builder().run {
        context(context)
        defaultBucket(bucketName)
        s3Client(this@uploadPhoto)
        build()
    }

    val observer = transferUtility.upload(
        fileName,
        context.contentResolver.openInputStream(uri)
    )

    observer.setTransferListener(object : TransferListener {
        override fun onStateChanged(id: Int, state: TransferState?) {
            if (state == TransferState.COMPLETED) {
                onUploaded(fileUrl)
            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

        override fun onError(id: Int, e: Exception) {
            onError(e)
        }
    })
}