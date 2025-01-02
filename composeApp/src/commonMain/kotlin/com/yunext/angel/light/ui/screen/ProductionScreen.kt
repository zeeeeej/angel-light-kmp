package com.yunext.angel.light.ui.screen

import ZhongGuoSe
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import color
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.detail_top_bg
import com.yunext.angel.light.resources.filter_element_flushing_a
import com.yunext.angel.light.resources.filter_element_flushing_n
import com.yunext.angel.light.resources.power_off
import com.yunext.angel.light.resources.power_on
import com.yunext.angel.light.theme.BG
import com.yunext.angel.light.theme.Color333
import com.yunext.angel.light.theme.ColorOrange
import com.yunext.angel.light.ui.common.CommitButton
import com.yunext.angel.light.ui.common.CommitButtonBlack
import com.yunext.angel.light.ui.common.clickablePure
import com.yunext.angel.light.ui.compoent.LoadingIcon
import com.yunext.angel.light.ui.viewmodel.ProductionState
import com.yunext.angel.light.ui.vo.ComposeIcon
import com.yunext.angel.light.ui.vo.ProductionResult
import com.yunext.angel.light.ui.vo.PropertyVo
import com.yunext.angel.light.ui.vo.ScanResultVo
import com.yunext.angel.light.ui.vo.defaultIcon
import com.yunext.angel.light.ui.vo.displayName
import com.yunext.kotlin.kmp.common.domain.doing
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProductionScreen(
    modifier: Modifier,
    productModel: ProductModelVo,
    product: Product,
    state: ProductionState,
    scanResult: ScanResultVo?,
    onBack: () -> Unit,
    onWash: () -> Unit,
    onPower: () -> Unit,
    onProduction: () -> Unit,
    onCommit: () -> Unit,
    onScan: () -> Unit,
    onConnect: () -> Unit,
    onDebug: () -> Unit,
) {
    ProductionMainScreen(
        modifier = modifier,
        scanResult = scanResult,
        onBack = onBack,
        onCommit = onCommit,
        productModel = productModel,
        product = product,
        state = state,
        onWash = onWash,
        onPower = onPower,
        onProduction = onProduction,
        onScan = onScan, onConnect = onConnect,
        onDebug = onDebug,
    )
}

@Composable
private fun ProductionMainScreen(
    modifier: Modifier = Modifier,
    scanResult: ScanResultVo?,
    productModel: ProductModelVo,
    product: Product,
    state: ProductionState,
    onBack: () -> Unit,
    onCommit: () -> Unit,
    onWash: () -> Unit,
    onPower: () -> Unit,
    onProduction: () -> Unit,
    onScan: () -> Unit,
    onConnect: () -> Unit,
    onDebug: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    color = BG
                )
            }) {

        Image(
            painterResource(Res.drawable.detail_top_bg),
            null,
            Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.FillBounds
        )

        Box(modifier.safeContentPadding()) {

            Column {
                val rememberCoroutineScope = rememberCoroutineScope()
                var isLongPressed by remember { mutableStateOf(false) }
                Title(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onLongPress = {
                                onDebug()
                            }) {

                            }
                        }, onBack = {
                        onBack()
                    }

                )

                // 滑动层
                Column(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    //Text("scanResult = $scanResult")
                    Spacer(Modifier.height(26.dp))
                    Top(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        top = TopVo(
                            product.name,
                            productModel.displayName(type = state.productType).ifEmpty {
                                scanResult?.peiJianCode ?: "-"
                            },
                            productModel.defaultIcon(type = state.productType)
                        ),
                        connected = state.connected,
                        onConnect = onConnect
                    )
                    Spacer(Modifier.height(12.dp))

                    val washing by remember(state.washEffect) {
                        derivedStateOf {
                            state.washEffect.doing
                        }
                    }

                    val powering by remember(state.powerEffect) {
                        derivedStateOf {
                            state.powerEffect.doing
                        }
                    }

                    Action(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        power = state.power,
                        wash = state.wash,
                        onWash = onWash,
                        onPower = onPower,
                        powering = powering,
                        washing = washing,
                        washingProductionResult = state.washProductionResult,
                        poweringProductionResult = state.powerProductionResult

                    )

                    Spacer(Modifier.height(12.dp))

                    val productionIng by remember(state.productionEffect) {
                        derivedStateOf {
                            state.productionEffect.doing
                        }
                    }
                    Properties(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        list = state.properties,
                        productionIng,
                        state.productionProductionResult,
                        onProduction = onProduction
                    )
                }
                Spacer(Modifier.height(16.dp))
                // 提交
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {

                    CommitButtonBlack(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        text = "重新扫码",
                        enable = true
                    ) {
                        onScan()
                    }

                    Spacer(Modifier.width(12.dp))


                    val commitEnable by remember(
                        state.productionProductionResult,
                        state.washProductionResult,
                        state.powerProductionResult
                    ) {
                        derivedStateOf {
                            state.productionProductionResult is ProductionResult.Success
                                    && state.washProductionResult is ProductionResult.Success
                                    && state.powerProductionResult is ProductionResult.Success

                        }
                    }
                    CommitButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        text = "恢复出厂设置",
                        enable = commitEnable
                    ) {
                        onCommit()
                    }
                }

            }

        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,

    onBack: () -> Unit
) {
    Box(modifier) {
        Text(
            "产测明细",
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.White
            ),
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 200.dp),
        )

        Image(Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(44.dp)
                .align(Alignment.CenterStart)
                .clickablePure {
                    onBack()
                })


    }
}

typealias TopVo = Triple<String, String, ComposeIcon>

@Composable
private fun Top(
    modifier: Modifier = Modifier,
    top: TopVo,
    connected: Boolean,
    onConnect: () -> Unit
) {
    Row(
        modifier = modifier
            .height(100.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {


        ImageComponent(modifier = Modifier.size(60.dp), top.third)

        Spacer(Modifier.width(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                top.first, style = TextStyle.Default.copy(
                    color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    top.second,
                    style = TextStyle.Default.copy(
                        color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Normal,

                        ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (connected) "已连接" else "已断开",
                    style = TextStyle.Default.copy(
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,

                        ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (connected) Color(0xFF3C6AF0) else Color.Red)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                        .clickablePure(!connected) {
                            onConnect()
                        }
                )
            }

        }
    }
}


@Composable
private fun Action(
    modifier: Modifier = Modifier, power: Boolean, wash: Boolean,
    powering: Boolean,
    washing: Boolean,
    washingProductionResult: ProductionResult<String>,
    poweringProductionResult: ProductionResult<String>,
    onWash: () -> Unit,
    onPower: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 20.dp, horizontal = 16.dp),

        ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .weight(1f), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 0.dp),
                    text = "滤芯冲洗检测",
                    style = TextStyle.Default.copy(
                        color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.width(4.dp))
                if (washing) {
                    LoadingIcon()
                }
            }

            when (washingProductionResult) {
                ProductionResult.Idle -> {}
                is ProductionResult.Success -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                    ) {
                        Image(Icons.Default.Done, null, colorFilter = ColorFilter.tint(Color.Green))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 0.dp),
                            text = washingProductionResult.data.toString(),
                            style = TextStyle.Default.copy(
                                color = Color333,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        )
                    }
                }

                is ProductionResult.Fail -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp).weight(1f),
                    ) {
                        Image(Icons.Default.Close, null, colorFilter = ColorFilter.tint(Color.Red))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 0.dp),
                            text = washingProductionResult.msg,
                            style = TextStyle.Default.copy(
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            ), maxLines = 3
                        )
                    }
                }
            }


            Image(
                painterResource(if (wash) Res.drawable.filter_element_flushing_a else Res.drawable.filter_element_flushing_n),
                null,
                modifier = Modifier
                    .size(32.dp)
                    .clickablePure(enabled = !washing && power) {
                        onWash()
                    }
                    .alpha(if (!washing && power) 1f else .5f)
            )


        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
                .drawBehind {
                    drawLine(
                        color = Color.Black.copy(alpha = .1f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height)
                    )
                })

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .padding(horizontal = 0.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Row(
                    modifier = Modifier, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 0.dp),
                        text = "开关机检测",
                        style = TextStyle.Default.copy(
                            color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.width(4.dp))
                    if (powering) {
                        LoadingIcon()
                    }
                }

                AnimatedVisibility(!power) {
                    Text(
                        modifier = Modifier,
                        text = "设备已关机，请开机后继续",
                        style = TextStyle.Default.copy(
                            color = ColorOrange, fontSize = 13.sp, fontWeight = FontWeight.Normal
                        )
                    )
                }
            }

            when (poweringProductionResult) {
                ProductionResult.Idle -> {}
                is ProductionResult.Success -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                    ) {
                        Image(Icons.Default.Done, null, colorFilter = ColorFilter.tint(Color.Green))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 0.dp),
                            text = poweringProductionResult.data.toString(),
                            style = TextStyle.Default.copy(
                                color = Color333,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            )
                        )
                    }

                }

                is ProductionResult.Fail -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                        ,
                    ) {
                        Image(Icons.Default.Close, null, colorFilter = ColorFilter.tint(Color.Red))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 0.dp),
                            text = poweringProductionResult.msg,
                            style = TextStyle.Default.copy(
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Light
                            ), maxLines = 3
                        )
                    }
                }
            }


            Image(
                painterResource(if (power) Res.drawable.power_on else Res.drawable.power_off),
                null,
                modifier = Modifier
                    .size(32.dp)
                    .clickablePure(enabled = !powering) {
                        onPower()
                    }
                    .alpha(if (!powering) 1f else .5f)
            )
        }
    }
}

@Composable
private fun Properties(
    modifier: Modifier = Modifier,
    list: List<PropertyVo>,
    productionIng: Boolean,
    productionProductionResult: ProductionResult<String>,
    onProduction: () -> Unit
) {
    Column(
        modifier = modifier
            .heightIn(max = 481.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White)

    ) {

        var expend by remember { mutableStateOf(false) }
        Row(
            Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .clickablePure {
                    expend = !expend
                }, verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier, text = "更多属性检测", style = TextStyle.Default.copy(
                    color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
            )
            AnimatedContent(expend, label = "ArrowDropDown") {
                Image(
                    Icons.Default.ArrowDropDown,
                    null,
                    modifier = Modifier.rotate(if (it) -90f else 0f)
                )
            }

        }

        if (expend) {
            LazyVerticalGrid(
                modifier = Modifier.weight(1f),
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),

                ) {
                items(items = list, {
                    it.toString()
                }) {
                    PropertyItem(property = it)
                }


            }
        }


        RealTime(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(enabled = !productionIng) {
                    onProduction()
                }
                .fillMaxWidth()
                .alpha(if (productionIng) .5f else 1f),
            productionIng = productionIng,
            productionProductionResult = productionProductionResult
        )

    }
}

@Composable
private fun RealTime(
    modifier: Modifier = Modifier,
    productionIng: Boolean,
    productionProductionResult: ProductionResult<String>,
) {

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = modifier
                .height(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .drawBehind {
                    drawRect(Color(0xffF6F9FF))
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (productionIng) {
                LoadingIcon()
            }
            Spacer(Modifier.width(4.dp))
            Text(
                "获取实时数据", style = TextStyle.Default.copy(
                    color = Color(0xFF3C6AF0), fontWeight = FontWeight.Bold, fontSize = 14.sp
                )
            )
        }

        Spacer(Modifier.height(8.dp))


//        Text(
//            "请先获取实时数据", style = TextStyle.Default.copy(
//                fontSize = 12.sp, color = Color.Black.copy(.4f)
//            )
//        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            when (productionProductionResult) {
                is ProductionResult.Success -> Image(
                    Icons.Default.Done,
                    null,
                    colorFilter = ColorFilter.tint(Color.Green)
                )

                ProductionResult.Idle -> {}
                is ProductionResult.Fail -> Image(
                    Icons.Default.Close,
                    null,
                    colorFilter = ColorFilter.tint(Color.Red)
                )
            }
            Spacer(Modifier.width(4.dp))
            Text(
                when (productionProductionResult) {
                    ProductionResult.Idle -> {
                        "请先获取实时数据"
                    }

                    is ProductionResult.Success -> {
                        productionProductionResult.data
                    }

                    is ProductionResult.Fail -> {
                        productionProductionResult.msg
                    }
                }, style = TextStyle.Default.copy(
                    fontSize = 12.sp, color =

                    when (productionProductionResult) {
                        is ProductionResult.Fail -> Color.Red
                        ProductionResult.Idle -> Color.Black.copy(.4f)
                        is ProductionResult.Success -> Color.Black.copy(1f)
                    }

                )
            )
        }



        Spacer(Modifier.height(16.dp))
    }

}

@Composable
private fun PropertyItem(modifier: Modifier = Modifier, property: PropertyVo) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .drawBehind {
                drawRect(
                    ZhongGuoSe.浅灰.color
                )
            }

            .padding(top = 16.dp, bottom = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = property.value.ifEmpty { "N/A" }, style = TextStyle.Default.copy(
                color = Color.Black.copy(alpha = .9f),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center
            ), maxLines = 1, modifier = Modifier.padding(horizontal = 4.dp)
        )
        Spacer(Modifier.height(0.dp))
        Text(
            text = property.unit, style = TextStyle.Default.copy(
                color = Color.Black.copy(alpha = .9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        )

        Text(
            text = property.name,
            style = TextStyle.Default.copy(
                color = Color333, fontSize = 14.sp, fontWeight = FontWeight.Bold
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .shadow(4.dp, RoundedCornerShape(14.dp))
                .background(Color.White)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}