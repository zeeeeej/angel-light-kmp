package com.yunext.angel.light.ui.compoent

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.github.aakira.napier.Napier
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

@Composable
actual fun ScanReal(
    flashLight:Boolean,
    onScanResult: (String) -> Unit ,
    onScanFail: (String) -> Unit ,
) {
    QrScanner(
        modifier = Modifier,
        flashlightOn = false,
        cameraLens = CameraLens.Back,
        openImagePicker = false,
        onCompletion = {
            Napier.d { "ScanScreen onCompletion:$it" }
            onScanResult(it)
        },
        imagePickerHandler = {
            Napier.e { "ScanScreen imagePickerHandler:$it" }
        },
        onFailure = {
            Napier.e { "ScanScreen onFailure:$it" }
            onScanFail(it)
        },
        overlayShape = OverlayShape.Square,
        overlayColor = Color(0x88000000),
        overlayBorderColor = Color.White.copy(.8f),
//            customOverlay = {
//                val (x,y) = this.size
//                drawRoundRect(
//                    brush = Brush.linearGradient(
//                        1f to Color.Red,
//                        1f to Color.Green,
//                        1f to Color.Blue
//                    ),
//                    topLeft = Offset(x/4,y/2),
//                    size = this.size.div(2f),
//                    cornerRadius = CornerRadius(16f, 16f),
//                )
//            },
        permissionDeniedView = {
            Text("没有权限，在设置里打开权限。")
        }
    )


}