package com.yunext.angel.light

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform