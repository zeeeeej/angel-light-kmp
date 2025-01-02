package com.yunext.angel.light.ui.vo

import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ProductType
import kotlinx.serialization.Serializable

@Serializable
data class Packet(
    val product: Product,
    val productModel: ProductModel,
    val type: ProductType
)