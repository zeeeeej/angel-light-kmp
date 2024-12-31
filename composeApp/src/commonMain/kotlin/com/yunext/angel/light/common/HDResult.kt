package com.yunext.angel.light.common

sealed class HDResult<out T> {
    data class Success<T>(val data: T) : HDResult<T>()
    data class Fail(val error: Throwable) : HDResult<Nothing>()
}

fun <T> HDResult<T>.display(display: (T) -> String) = when (this) {
    is HDResult.Fail -> this.error.message
    is HDResult.Success -> display(this.data)
}