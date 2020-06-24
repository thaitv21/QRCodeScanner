package com.nullexcom.qrscanner

import android.content.Context
import android.os.Handler
import android.telecom.Call
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.zxing.Result
import java.util.concurrent.Executors

class QRScannerView : FrameLayout {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context!!, attrs, defStyleAttr, defStyleRes) {}

    private var cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(context)
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val previewView: PreviewView = PreviewView(context)
    private val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

    private var isLocking = false;
    private var delay: Long = 0
    private var callback: Callback? = null
    private val callbackHandler = Handler()

    init {
        previewView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(previewView)
    }

    private fun startCamera(cameraProvider: ProcessCameraProvider, lifecycleOwner: LifecycleOwner) {
        val preview = Preview.Builder().apply {
            setTargetResolution(Size(previewView.width, previewView.height))
        }.build()

        val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(previewView.width, previewView.height))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, QrCodeAnalyzer { qrResult ->
                        previewView.post {
                            if (!isLocking) {
                                isLocking = true
                                callback?.apply(qrResult)
                                callbackHandler.postDelayed({
                                    isLocking = false
                                }, delay);
                            }
//                            Log.d("QRCodeAnalyzerAPP", "Barcode scanned: ${qrResult.text}")
                        }
                    })
                }

        cameraProvider.unbindAll()

        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
    }

    public fun startCamera(lifecycleOwner: LifecycleOwner) {
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            startCamera(cameraProvider, lifecycleOwner)
        }, ContextCompat.getMainExecutor(context))
    }

    public fun stopCamera() {
        cameraProviderFuture.get().unbindAll();
    }

    fun setCallback(delay: Long, callback: Callback?) {
        this.delay = delay
        this.callback = callback
    }
}