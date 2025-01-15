package com.yunext.angel.light

actual class KBuildConfig {

    actual val VERSION_NAME: String
        get() = com.yunext.angel.light.BuildConfig.VERSION_NAME
    actual val VERSION_CODE: Int
        get() = com.yunext.angel.light.BuildConfig.VERSION_CODE
    actual val HOST: String
        get() = com.yunext.angel.light.BuildConfig.HOST
}