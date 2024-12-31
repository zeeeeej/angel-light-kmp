package com.yunext.angel.light.ui.vo

import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.repo.http.Api
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.device_pic
import com.yunext.angel.light.resources.main

fun ProductModel.displayName(type: ProductType): String {
    return when (type) {
        ProductType.PeiJian -> ""
        ProductType.Device -> this.name
    }
}

fun ProductModel.defaultIcon(type: ProductType): ComposeIcon {
    return when (type) {
        ProductType.PeiJian -> ComposeIcon.Local(Res.drawable.main)
        ProductType.Device -> if (img.isEmpty()) {
            ComposeIcon.Local(
                Res.drawable.device_pic
            )
        } else {
            ComposeIcon.Remote(
                Api.img(img), Res.drawable.device_pic
            )
        }
    }
}