package com.example.utlikotlin

import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

object QrCode {
    fun generate(text: String, width: Int, height: Int): Bitmap {
        val encodedText = String(text.toByteArray(Charsets.UTF_8), Charsets.ISO_8859_1)

        return BarcodeEncoder().encodeBitmap(encodedText, BarcodeFormat.QR_CODE, width, height)
    }

    fun scan(context: Context, onResult: (String?) -> Unit) {
        val scannerOptions = GmsBarcodeScannerOptions.Builder().run {
            setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            enableAutoZoom()
            build()
        }

        val scanner = GmsBarcodeScanning.getClient(context, scannerOptions)

        scanner.startScan().addOnSuccessListener { barcode ->
            onResult(barcode.rawValue)
        }
    }
}