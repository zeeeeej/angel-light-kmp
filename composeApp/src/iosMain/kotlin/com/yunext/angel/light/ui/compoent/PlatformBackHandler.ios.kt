package com.yunext.angel.light.ui.compoent

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackGestureHandler(
    isEnabled: Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    IosBackGestureHandler(isEnabled,onBack,content)
}