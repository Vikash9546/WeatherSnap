package com.weathersnap.app.ui.camera

sealed interface CameraUiState {
    object Idle : CameraUiState
    object Capturing : CameraUiState
    object Compressing : CameraUiState
    data class Success(
        val imagePath: String,
        val originalSize: Long,
        val compressedSize: Long
    ) : CameraUiState
    data class Error(val message: String) : CameraUiState
}
