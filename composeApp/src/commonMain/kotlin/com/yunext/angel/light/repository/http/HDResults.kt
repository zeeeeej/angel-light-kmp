package com.yunext.angel.light.repository.http

import com.yunext.angel.light.common.HDResult

internal fun <T> httpSuccess(data: T): HDResult<T> = HDResult.Success(data)
internal fun <T> httpFail(e: Throwable, code: Int = -1): HDResult<T> =
    HDResult.Fail(ApiException(code, e.message ?: "", e))

internal fun <T, R> HDResult<T>.map(map: (T) -> R): HDResult<R> {
    return when (this) {
        is HDResult.Fail -> this
        is HDResult.Success -> httpSuccess(map(this.data))
    }
}