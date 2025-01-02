package com.yunext.angel.light.repository.http

class TokenException : RuntimeException(ApiException(103))
object BizException : RuntimeException()

class ApiException(val code: Int = -1, msg: String = "", cause: Throwable? = null) :
    RuntimeException(msg, cause)


