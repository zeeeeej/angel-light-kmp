package com.yunext.angel.light.ui.compoent

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.icon_back
import com.yunext.angel.light.resources.icon_flashlight_a
import com.yunext.angel.light.resources.icon_flashlight_n
import com.yunext.angel.light.ui.common.clickablePure
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
expect fun ScanReal(
    flashLight: Boolean,
    onScanResult: (String) -> Unit = {},
    onScanFail: (String) -> Unit = {},
)

@Composable
fun ScanComponent(
    modifier: Modifier = Modifier,
    type: ProductType,
    onScanResult: (String) -> Unit = {},
    onScanFail: (String) -> Unit = {},
    onBack: () -> Unit = {},
) {

    var flashOpened: Boolean by remember() { mutableStateOf(false) }

    val previewViewComposable: @Composable (() -> Boolean) -> Unit = { opened ->
        ScanReal(flashLight = opened(), onScanResult = onScanResult, onScanFail = onScanFail)
    }

    Box(
        modifier = modifier
    ) {

        previewViewComposable { flashOpened }

        Box(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(48.dp)
                    .padding(12.dp)
                    .clickablePure {
                        onBack()
                    }
                    .align(Alignment.CenterStart),
                imageVector = vectorResource(Res.drawable.icon_back),
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = null
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "请扫描",
                style = TextStyle.Default.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 18.sp
            )


        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = when (type) {
                    ProductType.PeiJian -> "扫描配件二维码"
                    ProductType.Device -> "扫描设备二维码"
                },
                style = TextStyle.Default.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Normal
                ),
                fontSize = 14.sp
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Icon(
                    painterResource(if (flashOpened) Res.drawable.icon_flashlight_a else Res.drawable.icon_flashlight_n),
                    null,
                    tint = Color.White, modifier = Modifier.clickablePure {
                        flashOpened = !flashOpened
                    }
                )

                Spacer(Modifier.height(4.dp))
                Text(
                    modifier = Modifier,
                    text = "点击开启闪关灯",
                    style = TextStyle.Default.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    ),
                    fontSize = 14.sp
                )
            }
        }
    }
}
