package com.weathersnap.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
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

        // Handle rotation based on EXIF data
        val exifInterface = ExifInterface(sourceFile.absolutePath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val rotatedBitmap = rotateBitmapIfRequired(bitmap, orientation)

        // Create compressed output file
        val compressedFile = File(
            context.filesDir,
            "compressed_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(compressedFile).use { out ->
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        
        if (rotatedBitmap != bitmap) {
            rotatedBitmap.recycle()
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

    private fun rotateBitmapIfRequired(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            else -> return bitmap
        }
        return try {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: OutOfMemoryError) {
            bitmap
        }
    }
}
