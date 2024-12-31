package com.yunext.angel.light.domain.poly

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProductType {
    @SerialName("peijian")
    PeiJian,

    @SerialName("device")
    Device
    ;

    companion object {
        internal const val PEI_JIAN_CODE_LENGTH = 20
    }
}

val ProductType.value: String
    get() = when (this) {
        ProductType.PeiJian -> "component"
        ProductType.Device -> "device"
    }
