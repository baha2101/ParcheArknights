package com.example.parchearknights

import android.content.Context
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream

class ExcelParser(private val context: Context) {

    fun parseExcelFile(): Map<String, String> {
        val translations = mutableMapOf<String, String>()
        val file = File(context.filesDir, "translations.xlsx")

        if (!file.exists()) return translations

        val fis = FileInputStream(file)
        val workbook = WorkbookFactory.create(fis)
        val sheet = workbook.getSheetAt(0) // Primera hoja del archivo

        for (row in sheet) {
            val keyCell = row.getCell(1) // Columna B (Inglés)
            val valueCell = row.getCell(2) // Columna C (Español)

            if (keyCell != null && valueCell != null) {
                translations[keyCell.stringCellValue] = valueCell.stringCellValue
            }
        }

        workbook.close()
        fis.close()

        return translations
    }
}
