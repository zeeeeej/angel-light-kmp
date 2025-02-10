package com.yunext.angel.light.common

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.camera2.CameraManager
import com.yunext.angel.light.MyApp
import io.github.aakira.napier.Napier
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

actual suspend fun clipBroad(label: String, text: String) {
    return suspendCoroutine {
        val ctx = MyApp.CTX.applicationContext
        val clipboard: ClipboardManager =
            ctx.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        it.resume(Unit)
    }
}