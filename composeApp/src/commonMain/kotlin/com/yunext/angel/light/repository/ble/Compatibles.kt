package com.yunext.angel.light.repository.ble

import com.yunext.angel.light.BuildConfigX

private const val compatible_code = 5

fun oldApp(now: Int = BuildConfigX.VERSION_CODE) = now < compatible_code