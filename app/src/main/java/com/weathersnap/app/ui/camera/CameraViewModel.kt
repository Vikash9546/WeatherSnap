package com.weathersnap.app.ui.camera

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun capturePhoto(imageCapture: ImageCapture) {
        _uiState.value = CameraUiState.Capturing

        val photoFile = createTempFile()
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    viewModelScope.launch {
                        _uiState.value = CameraUiState.Compressing
                        try {
                            val (compressedPath, originalSize, compressedSize) =
                                ImageCompressor.compress(context, photoFile)
                            // Delete original temp file after compression
                            photoFile.delete()
                            _uiState.value = CameraUiState.Success(
                                imagePath = compressedPath,
                                originalSize = originalSize,
                                compressedSize = compressedSize
                            )
                        } catch (e: Exception) {
                            photoFile.delete()
                            _uiState.value = CameraUiState.Error(e.message ?: "Compression failed")
                        }
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    photoFile.delete()
                    _uiState.value = CameraUiState.Error(exception.message ?: "Capture failed")
                }
            }
        )
    }

    private fun createTempFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis())
        return File(context.cacheDir, "WEATHERSNAP_${timeStamp}.jpg")
    }

    fun resetState() {
        _uiState.value = CameraUiState.Idle
    }
}
