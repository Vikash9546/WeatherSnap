package com.weathersnap.app.domain.model

data class CameraResult(
    val compressedImagePath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long
)
