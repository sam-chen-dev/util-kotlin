package com.example.utlikotlin

import android.content.Context
import android.net.Uri
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.apply
import kotlin.collections.forEach
import kotlin.let
import kotlin.run

class QrCodeScanner {
    private val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val barcodeScanner: BarcodeScanner = createBarcodeScanner()
    private lateinit var camera: Camera

    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        onResult: (String) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)
        val listener = {
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = createPreview(previewView)
            val imageAnalysis = createImageAnalysis(onResult)

            camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }

        cameraProviderFuture.addListener(listener, executor)
    }

    fun scanLocalImage(context: Context, uri: Uri, onResult: (String?) -> Unit) {
        val inputImage = InputImage.fromFilePath(context, uri)

        barcodeScanner.process(inputImage).apply {
            addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    onResult(null)
                } else {
                    barcodes.forEach { barcode ->
                        onResult(barcode.rawValue)
                    }
                }
            }
        }
    }

    fun shutdown() {
        barcodeScanner.close()
        cameraExecutor.shutdown()
    }

    private fun createBarcodeScanner(): BarcodeScanner {
        val zoomCallback: () -> Boolean = {
            camera.cameraControl.setZoomRatio(5.0f)
            true
        }

        val zoomSuggestionOptions = ZoomSuggestionOptions.Builder { zoomCallback() }.build()

        val barcodeScannerOptions = BarcodeScannerOptions.Builder().run {
            setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            setZoomSuggestionOptions(zoomSuggestionOptions)
            build()
        }

        return BarcodeScanning.getClient(barcodeScannerOptions)
    }

    private fun createPreview(previewView: PreviewView): Preview {
        val resolution = Size(1280, 720)
        val resolutionStrategy = ResolutionStrategy(resolution, ResolutionStrategy.FALLBACK_RULE_NONE)
        val resolutionSelector = ResolutionSelector.Builder().run {
            setResolutionStrategy(resolutionStrategy)
            build()
        }

        val preview = Preview.Builder().run {
            setResolutionSelector(resolutionSelector)
            build()
        }

        preview.surfaceProvider = previewView.surfaceProvider

        return preview
    }

    private fun createImageAnalysis(onResult: (String) -> Unit): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder().run {
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            build()
        }

        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(imageProxy, onResult)
        }

        return imageAnalysis
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy, onResult: (String) -> Unit) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

            barcodeScanner.process(inputImage).apply {
                addOnSuccessListener { barcodes ->
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let {
                            onResult(it)
                        }
                    }
                }

                addOnCompleteListener {
                    imageProxy.close()
                }
            }
        }
    }
}