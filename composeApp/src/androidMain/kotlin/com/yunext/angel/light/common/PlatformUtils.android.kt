package com.yunext.angel.light.common

import android.content.Context
import android.hardware.camera2.CameraManager
import com.yunext.angel.light.MyApp
import io.github.aakira.napier.Napier

actual fun toggleFlashlight(on: Boolean) {
    try {
        val cameraManager = MyApp.CTX.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull()
        check(cameraId != null) {
            "cameraId is null"
        }
        cameraManager.setTorchMode(cameraId, on)
    } catch (e: Exception) {
        Napier.w { "toggleFlashlight error : $e" }
    }
}