package com.yunext.angel.light.ui.vo

import com.yunext.angel.light.repository.ble.TslPropertyKey

data class PropertyVo(
    val key: TslPropertyKey,
    val value: String,
    val unit: String,
    val name: String
)