package org.tensorflow.lite.examples.detection.list.presentation

import android.graphics.Bitmap

data class EmployeeData (
    val firstName: String,
    val lastName: String,
    val photo: Bitmap?,
    val timestamp: Long = 0
)