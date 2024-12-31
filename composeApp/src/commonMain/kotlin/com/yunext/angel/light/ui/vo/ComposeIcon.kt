package com.yunext.angel.light.ui.vo

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.DrawableResource

@Stable
sealed interface ComposeIcon {
    data class Local(val res: DrawableResource) : ComposeIcon
    data class Remote(val path: String, val error: DrawableResource) : ComposeIcon
}