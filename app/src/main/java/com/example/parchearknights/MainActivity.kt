package com.example.parchearknights

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var excelDownloader: ExcelDownloader
    private lateinit var excelParser: ExcelParser
    private lateinit var translationManager: TranslationManager
    private lateinit var ocrService: OCRTranslationService
    private var translations: Map<String, String> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar componentes principales
        sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        excelDownloader = ExcelDownloader(this)
        excelParser = ExcelParser(this)
        translationManager = TranslationManager(this)
        ocrService = OCRTranslationService()

        val nameInput = findViewById<EditText>(R.id.userNameInput)
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        val startServiceButton = findViewById<Button>(R.id.startServiceButton)

        // Cargar nombre guardado
        nameInput.setText(sharedPreferences.getString("username", ""))

        // Guardar el nombre del usuario cuando lo cambia
        nameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val userName = nameInput.text.toString()
                sharedPreferences.edit {
                    putString("username", userName)
                }
                translationManager.setUserName(userName)  // Actualizar el nombre del usuario
                Toast.makeText(this, "Nombre guardado: $userName", Toast.LENGTH_SHORT).show()
            }
        }

        // Descargar y procesar el archivo Excel
        downloadButton.setOnClickListener {
            val downloadUrl = "https://drive.google.com/uc?id=1Fi7eBRttSiWkxArEw4YIVrYUdpJ-k7U5&export=download"  // Enlace directo
            excelDownloader.downloadExcelFile(downloadUrl) { success ->
                runOnUiThread {
                    if (success) {
                        translations = excelParser.parseExcelFile()
                        Toast.makeText(this, "Traducciones actualizadas", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al descargar el archivo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Iniciar el servicio de OCR
        startServiceButton.setOnClickListener {
            val userName = sharedPreferences.getString("username", "Usuario") ?: "Usuario"
            ocrService.startOCRService(this, translations, translationManager, userName)
            Toast.makeText(this, "Servicio de OCR iniciado", Toast.LENGTH_SHORT).show()
        }
    }
}
