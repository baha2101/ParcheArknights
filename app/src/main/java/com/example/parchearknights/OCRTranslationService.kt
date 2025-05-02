package com.example.parchearknights

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import androidx.core.graphics.createBitmap

class OCRTranslationService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: OverlayView
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private lateinit var translationManager: TranslationManager
    private var translations: Map<String, String> = emptyMap()
    private lateinit var userName: String // Almacena el nombre del usuario
    private val handler = Handler(Looper.getMainLooper())
    private val captureInterval = 2000L // Capturar cada 2 segundos

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    fun startOCRService(
        context: Context,
        translationsMap: Map<String, String>,
        manager: TranslationManager,
        userNameInput: String
    ) {
        translationManager = manager
        translations = translationsMap
        userName = userNameInput // Asigna el nombre del usuario recibido

        windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = OverlayView(context)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        windowManager.addView(overlayView, params)

        startOCRLoop()
    }

    private fun startOCRLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                captureScreenAndProcess()
                handler.postDelayed(this, captureInterval)
            }
        }, captureInterval)
    }

    private fun captureScreenAndProcess() {
        try {
            val screenshot = takeScreenshot() ?: return
            val image = InputImage.fromBitmap(screenshot, 0)
            processImage(image)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun processImage(image: InputImage) {
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val detectedText = visionText.text

                // Aplica traducción personalizada, incluyendo reemplazo de {nombre}
                val translatedText = translationManager.applyTranslation(detectedText, translations)
                overlayView.updateText(translatedText) // Muestra el texto traducido

            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    inner class OverlayView(context: Context) : View(context) {
        private var textToDisplay: String = ""
        private val textPaint = Paint().apply {
            color = Color.YELLOW
            textSize = 50f
            typeface = Typeface.DEFAULT_BOLD
        }

        fun updateText(newText: String) {
            textToDisplay = newText
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            canvas.drawText(textToDisplay, 100f, 200f, textPaint) // Ajustar la posición si es necesario
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Detener OCR al cerrar servicio
        windowManager.removeView(overlayView)
    }

    private fun takeScreenshot(): Bitmap? {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        return try {
            val screenshot = createBitmap(width, height)
            val canvas = Canvas(screenshot)
            val rootView = View(this).rootView
            rootView.draw(canvas)
            screenshot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
