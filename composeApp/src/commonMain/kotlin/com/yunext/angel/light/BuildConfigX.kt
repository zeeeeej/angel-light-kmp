package com.yunext.angel.light

object BuildConfigX {
    private val config = KBuildConfig()
    const val DEBUG_X = true
    const val V2 = true


    /* 是否忽略服务器check */
    var IGNORE_CHECK_CODE = false
    val VERSION_CODE = config.VERSION_CODE

     val VERSION = "${config.VERSION_NAME}(${config.VERSION_CODE})"

    val HOST = config.HOST
}

/**
 * 打包需要改的内容
 *
 * VERSION_NAME ： 2.0.16
 * VERSION_CODE ： 17
 * HOST         ：
 *
 * android 修改：build.gradle.kts
 * ios 修改： Info.plist
 */
expect class KBuildConfig() {
    val VERSION_NAME: String
    val VERSION_CODE: Int
    val HOST: String
}
