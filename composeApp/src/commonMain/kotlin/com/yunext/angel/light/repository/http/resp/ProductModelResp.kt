package com.yunext.angel.light.repository.http.resp

import kotlinx.serialization.Serializable

@Serializable
data class ProductModelResp(
    val id: String?,
    val name: String?,
    val identifier: String?,
    val img: String?,
)