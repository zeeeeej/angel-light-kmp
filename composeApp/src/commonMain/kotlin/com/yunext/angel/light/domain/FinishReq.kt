package com.yunext.angel.light.domain

import kotlinx.serialization.Serializable

@Serializable
sealed interface FinishReq {
    @Serializable
    data class Device(
        val code: String,
        val componentCode: String,
        val productCode: String,
        val result: String,
    ) : FinishReq

    @Serializable
    data class PeiJian(
        val componentCode: String,
        val result: String,
    ) : FinishReq
}