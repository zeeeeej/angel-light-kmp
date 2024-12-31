package com.yunext.angel.light

import androidx.compose.ui.window.ComposeUIViewController
import com.yunext.angel.light.di.KoinInit

fun MainViewController() = ComposeUIViewController {
    KoinInit.init { }
    App()
}