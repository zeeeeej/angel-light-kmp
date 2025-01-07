package com.yunext.angel.light.ui.screen

import ZhongGuoSe
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import color
import com.yunext.angel.light.BuildConfigX
import com.yunext.angel.light.BuildConfigX.IGNORE_CHECK_CODE
import com.yunext.angel.light.domain.DeviceProduct
import com.yunext.angel.light.domain.EmptyDevice
import com.yunext.angel.light.domain.EmptyModel
import com.yunext.angel.light.domain.PeiJianProduct
import com.yunext.angel.light.domain.poly.Product
import com.yunext.angel.light.domain.poly.ProductModel
import com.yunext.angel.light.resources.Res
import com.yunext.angel.light.resources.icon_log_out
import com.yunext.angel.light.ui.common.clickablePure

import com.yunext.angel.light.domain.poly.ProductType
import com.yunext.angel.light.resources.icon_scan
import com.yunext.angel.light.ui.vo.defaultIcon
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ProductTabVo(val text: String)

typealias ProductModelVo = ProductModel
typealias OnProductModelSelected = (ProductModelVo, Product, ProductType) -> Unit
typealias OnProductSelected = (Product) -> Unit

private object HomeScreenDefaults {
    val BgColors by lazy {
        listOf(
            Color(0xffB8E2FF), Color(0xffffffff)
        )
    }

    val tabs by lazy {
        (listOf("无限系列") + (1..10).map {
            "XXX系列"
        }).map {
            ProductTabVo(it)
        }
    }
}

@Preview()
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        Modifier.fillMaxSize(),
        onProductModelSelected = { _, _, _ -> },
        onExit = {},
        onProductSelected = {},
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier,
    products: List<Product> = emptyList(),
    productModels: List<ProductModelVo> = emptyList(),
    onProductModelSelected: OnProductModelSelected,
    onProductSelected: OnProductSelected, onExit: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = HomeScreenDefaults.BgColors
                    )
                )
            }) {
        Box(modifier.safeContentPadding()) {
            Column {
                Title(
                    Modifier
                        .fillMaxWidth()
                        .height(44.dp), onExit = onExit
                )

                Spacer(Modifier.height(4.dp))

                ProductContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    products = products,
                    productModels = productModels,
                    onProductSelected = onProductSelected,
                    onProductModelSelected = onProductModelSelected
                )

                Version(Modifier.align(Alignment.CenterHorizontally).padding(16.dp))

            }
        }
    }
}

@Composable
private fun Version(modifier: Modifier = Modifier, version: String = "v" + BuildConfigX.VERSION) {
    Box(modifier) {
        Text(
            version,
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Light,
                fontSize = 11.sp,
                color = if (IGNORE_CHECK_CODE) ZhongGuoSe.向日葵黄.color else Color.Black
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
        )
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    onExit: () -> Unit
) {
    var clickCount: Int by remember {
        mutableStateOf(0)
    }
    LaunchedEffect(clickCount) {
        launch {
            delay(1000)
            clickCount = 0
        }
        if (clickCount > 20) {
            clickCount = 0
            IGNORE_CHECK_CODE = !IGNORE_CHECK_CODE
            //ToastUtil.toast("[${if (IGNORE_CHECK_CODE) "跳过" else "开启"}服务器检测模式]") // todo
        }
    }

    Box(modifier) {
        Text(
            "轻智能产测",
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = if (IGNORE_CHECK_CODE) ZhongGuoSe.向日葵黄.color else Color.Black
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 200.dp)
                .clickablePure {
                    clickCount += 1
                },
        )

        Image(painterResource(Res.drawable.icon_log_out),
            null,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterEnd)
                .clickablePure {
                    onExit()
                })


    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProductContent(
    modifier: Modifier = Modifier,
    products: List<Product>,
    productModels: List<ProductModelVo>,
    onProductSelected: OnProductSelected,
    onProductModelSelected: OnProductModelSelected
) {
    val coroutineScope = rememberCoroutineScope()
    val list: List<ProductTabVo> by remember(products) {
        mutableStateOf(buildList<ProductTabVo> {
            addAll(products.map {
                ProductTabVo(it.name)
            })
        })

    }
    Box(modifier = modifier) {
        val pagerState = rememberPagerState {
            list.size
        }

        LaunchedEffect(products) {
            println("currentPage LaunchedEffect $products")
            snapshotFlow {
                pagerState.currentPage
            }.collectLatest {
                println("currentPage $it $products")

                if (products.isNotEmpty()) {
                    if (list[it].text == Product.PeiJianProduct.name) {
                        // ignore
                    } else {
                        onProductSelected(products[it])
                    }

                }
            }
        }


        Column(Modifier.fillMaxSize()) {
            ScrollableTabRow(selectedTabIndex = pagerState.currentPage,
                contentColor = contentColorFor(Color.Transparent),
                backgroundColor = Color.Transparent,
                indicator = {},
                edgePadding = 14.5.dp,
                divider = {}) {
                list.forEachIndexed { index, cur ->
                    TabItem(
                        modifier = Modifier,
                        tab = cur,
                        selected = index == pagerState.currentPage,
                        onSelected = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        })


                }
            }

            val scan: @Composable (Product, ProductType) -> Unit = { p, type ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(bottom = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            when (type) {
                                ProductType.PeiJian -> "扫描配件二维码"
                                ProductType.Device -> "扫描设备二维码"
                            },
                            style = TextStyle.Default.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        Image(
                            imageVector = vectorResource(Res.drawable.icon_scan),
                            contentDescription = "网络图片",
                            modifier = Modifier
                                .size(96.dp)
                                .clickablePure {
                                    onProductModelSelected.invoke(
                                        when (type) {
                                            ProductType.PeiJian -> ProductModel.EmptyModel
                                            ProductType.Device -> ProductModel.EmptyDevice
                                        }, p, type
                                    )
                                },
                        )

                    }
                }
            }
            HorizontalPager(
                state = pagerState, modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { index ->
                when (list[index].text) {
                    Product.DeviceProduct.name -> {
                        scan(products[index], ProductType.Device)
                    }

                    Product.PeiJianProduct.name -> {
                        scan(products[index], ProductType.PeiJian)
                    }

                    else -> PageItem(
                        modifier = Modifier.fillMaxSize(),
                        type = ProductType.Device,
                        index = index,
                        list = productModels, onProductModelSelected = {
                            onProductModelSelected.invoke(it, products[index], ProductType.Device)
                        }
                    )
                }
            }
        }

    }
}

@Composable
private fun PageItem(
    modifier: Modifier = Modifier,
    type: ProductType,
    index: Int,
    list: List<ProductModelVo>,
    onProductModelSelected: (ProductModelVo) -> Unit
) {
    ////

    Box(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(list, { model ->
                model.name
            }) { model ->
                ProductModelItem(productModel = model, type = type, onClick = {
                    onProductModelSelected.invoke(model)
                })
            }
        }
    }
}

@Composable
private fun ProductModelItem(productModel: ProductModelVo, type: ProductType, onClick: () -> Unit) {

    Column(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                onClick()
            }
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(vertical = 24.dp, horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ImageComponent(Modifier.size(60.dp), productModel.defaultIcon(type))

        Spacer(Modifier.height(8.dp))
        Text(
            productModel.name,
            style = TextStyle.Default.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            ), maxLines = 2, overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TabItem(
    modifier: Modifier,
    tab: ProductTabVo,
    selected: Boolean,
    onSelected: () -> Unit
) {

    Box(
        modifier
            .width(72.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(18.dp))
            .run {
                if (selected) {
                    this
                        .background(Color.White)
                } else this
            }
            .clickable {
                onSelected()

            }
            .padding(horizontal = 8.dp, vertical = 6.dp), contentAlignment = Alignment.Center

    ) {
        Text(
            tab.text,
            style = TextStyle.Default.copy(
                fontSize = 14.sp, fontWeight =
                if (selected)
                    FontWeight.Bold else FontWeight.Normal, color = Color.Black
            ),
            maxLines = 1
        )

    }
}