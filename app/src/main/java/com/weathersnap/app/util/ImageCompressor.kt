package com.weathersnap.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageCompressor {

    /**
     * Compresses a JPEG image to 80% quality on Dispatchers.IO.
     * Returns the compressed file path, original size, and compressed size.
     */
    suspend fun compress(
        context: Context,
        sourceFile: File,
        quality: Int = 80
    ): Triple<String, Long, Long> = withContext(Dispatchers.IO) {
        val originalSize = sourceFile.length()

        // Decode source bitmap
        val bitmap = BitmapFactory.decodeFile(sourceFile.absolutePath)
            ?: throw IllegalArgumentException("Cannot decode file: ${sourceFile.absolutePath}")

        // Create compressed output file
        val compressedFile = File(
            context.filesDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        bitmap.recycle()

        val compressedSize = compressedFile.length()
        Triple(compressedFile.absolutePath, originalSize, compressedSize)
    }

    /**
     * Deletes temporary/orphaned image files safely.
     */
    fun deleteSafely(filePath: String?) {
        filePath?.let {
            try {
                val file = File(it)
                if (file.exists()) file.delete()
            } catch (e: Exception) {
                // Ignore deletion errors
            }
        }
    }
}
