package com.yunext.angel.light.ui.vo

/**
 * 产测结果
 */
sealed class ProductionResult<out T> {
    data object Idle : ProductionResult<Nothing>()
    data class Fail(val msg: String) : ProductionResult<Nothing>()
    data class Success<T>(val data: T) : ProductionResult<T>()
}

val ProductionResult<*>.value: Int
    get() = when (this) {
        is ProductionResult.Fail -> 0
        ProductionResult.Idle -> -1
        is ProductionResult.Success -> 1
    }