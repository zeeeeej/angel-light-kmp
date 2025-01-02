package com.yunext.angel.light.ui.vo

/**
 * 设置结果
 */
sealed interface ActionResult<out T, out Error> {
    data class Success<T>(val data: T) : ActionResult<T, Nothing>
    data class Fail<Nothing, Error>(val msg: Error) : ActionResult<Nothing, Error>
    data object TimeOut : ActionResult<Nothing, Nothing>
}