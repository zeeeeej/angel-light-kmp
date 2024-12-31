package com.yunext.angel.light.domain

sealed interface FinishReq {
    data class Device(
        val code: String,
        val componentCode: String,
        val productCode: String,
        val result: String,
    ) : FinishReq

    data class PeiJian(
        val componentCode: String,
        val result: String,
    ) : FinishReq
}