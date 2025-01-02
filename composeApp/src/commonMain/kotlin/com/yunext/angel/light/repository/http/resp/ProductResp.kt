package com.yunext.angel.light.repository.http.resp

import kotlinx.serialization.Serializable

@Serializable
data class ProductResp(
    val code:String?,
    val name:String?,
)