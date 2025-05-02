package com.example.parchearknights

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class TranslationManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    // Función para aplicar traducciones dinámicas con marcador "{nombre}"
    fun applyTranslation(text: String, translations: Map<String, String>): String {
        val userName = sharedPreferences.getString("username", "Usuario") ?: "Usuario"
        // Reemplazar "{nombre}" con el nombre del usuario y buscar la traducción correspondiente
        val personalizedText = text.replace("{nombre}", userName)
        return translations[personalizedText] ?: personalizedText
    }

    // Función para actualizar el nombre del usuario en SharedPreferences
    fun setUserName(userName: String) {
        sharedPreferences.edit {
            putString("username", userName)
        }
    }

}
