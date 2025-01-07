package com.yunext.angel.light.common

import io.github.aakira.napier.Napier
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureTorchModeOff
import platform.AVFoundation.AVCaptureTorchModeOn
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.hasTorch
import platform.AVFoundation.torchMode

@OptIn(ExperimentalForeignApi::class)
actual fun toggleFlashlight(on: Boolean) {
    try {
        val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
        check(device != null) {
            "device is null"
        }
        if (device.hasTorch) {
            if (device.lockForConfiguration(null as kotlinx.cinterop.CPointer<kotlinx.cinterop.ObjCObjectVar<platform.Foundation.NSError?>>?)) {
                device.torchMode = if (on) AVCaptureTorchModeOn else AVCaptureTorchModeOff
                device.unlockForConfiguration()
            } else {
                Napier.w { "toggleFlashlight lockForConfiguration fail" }
            }
        }
    } catch (e: Exception) {
        Napier.w { "toggleFlashlight error : $e" }
    }
}