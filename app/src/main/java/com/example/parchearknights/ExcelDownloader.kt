package com.example.parchearknights

import android.content.Context
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExcelDownloader(private val context: Context) {

    private val client = OkHttpClient()

    fun downloadExcelFile(url: String, callback: (Boolean) -> Unit) {
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(false)
                    return
                }

                response.body?.let { body ->
                    val file = getExcelFile()
                    val fos = FileOutputStream(file)
                    fos.write(body.bytes())
                    fos.close()
                    callback(true)
                } ?: callback(false)
            }
        })
    }

    fun getExcelFile(): File {
        return File(context.filesDir, "translations.xlsx")
    }
}
