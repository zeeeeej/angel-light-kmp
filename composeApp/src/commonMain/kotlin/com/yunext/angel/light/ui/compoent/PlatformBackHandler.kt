package com.yunext.angel.light.ui.compoent

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackGestureHandler(
    isEnabled: Boolean,
    onBack: () -> Unit,
    content: @Composable () -> Unit
)

