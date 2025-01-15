package com.yunext.angel.light

import platform.Foundation.NSBundle

actual class KBuildConfig {
    actual val VERSION_NAME: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String

            ?: "2.0"
    actual val VERSION_CODE: Int
        get() = (NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String)?.toInt()
            ?: 17

    /**
     * HOST_HD_DEV
     * HOST_ANGEL_DEV
     * HOST_ANGEL
     */
    actual val HOST: String
        get() = NSBundle.mainBundle.infoDictionary?.get("HOST_HD_DEV") as? String
            ?: ""


}