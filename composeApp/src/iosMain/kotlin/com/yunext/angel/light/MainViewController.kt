package com.yunext.angel.light

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import androidx.compose.ui.window.ComposeUIViewController
import com.yunext.angel.light.di.KoinInit

fun MainViewController() = ComposeUIViewController(configure = {
    this.delegate = object :ComposeUIViewControllerDelegate{
        override fun viewDidLoad() {
            super.viewDidLoad()
            KoinInit.init { }
        }
    }
}) {
    App()
}