package com.yunext.angel.light.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.rememberModalBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import color
import com.yunext.angel.light.common.clipBroad
import com.yunext.angel.light.di.koinViewModelP2
import com.yunext.angel.light.ui.compoent.CancelableLoadingComponent
import com.yunext.angel.light.ui.compoent.HistoriesInfo
import com.yunext.angel.light.ui.compoent.LoadingComponent
import com.yunext.angel.light.ui.compoent.PlatformBackGestureHandler
import com.yunext.angel.light.ui.compoent.Toast
import com.yunext.angel.light.ui.compoent.ToastData
import com.yunext.angel.light.ui.compoent.display
import com.yunext.angel.light.ui.viewmodel.ProductionState
import com.yunext.angel.light.ui.viewmodel.ProductionViewModel
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.angel.light.ui.vo.ScanResultVo
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.doing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ProductionScreenWithVm(
    modifier: Modifier = Modifier,
    scanResult: ScanResultVo,
    packet: Packet,
    toScan: (Packet) -> Unit,
    onBack: () -> Unit
) {
    val vm: ProductionViewModel = koinViewModelP2(scanResult, packet)
    val state by vm.state.collectAsStateWithLifecycle(
        ProductionState(
            product = packet.product,
            productModel = packet.productModel,
            productType = packet.type,
            scanResult = scanResult
        )
    )
    val coroutineScope = rememberCoroutineScope()
//    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
//        bottomSheetState = rememberBottomSheetState(Collapsed)
//    )

    val rememberModalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
//                LaunchedEffect(Unit) {
//                    bottomSheetScaffoldState.bottomSheetState.expand()
//                }
//    var loggerSupport: Boolean by remember {
//        mutableStateOf(false)
//    }

    val toast by remember(
        state.toast
    ) {
        derivedStateOf {
            state.toast
        }
    }
//    LaunchedEffect(Unit) {
//        snapshotFlow {
//            toast
//        }.collect {
//            if (it.isEmpty()) {
//            } else {
//                bottomSheetScaffoldState.snackbarHostState.showSnackbar(it)
////                ToastUtil.toast(it)
//                vm.clearToast()
//            }
//        }
//
//    }
    val histories: @Composable () -> Unit = {
        HistoriesInfo(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 320.dp, max = 320.dp), list = state.bleLogs, onClose = {
                coroutineScope.launch {
//                    bottomSheetScaffoldState.bottomSheetState.collapse()
                    rememberModalBottomSheetState.hide()
                }
            }, onShare = {
                if (it.isNotEmpty()) {
                    coroutineScope.launch {
                        clipBroad(
                            "bleLog",
                            it.joinToString("\n") { h -> h.display })
                        rememberModalBottomSheetState.hide()
                        vm.toast("已复制至剪贴板！")
                    }
                }
            }
        )
    }

    val content: @Composable () -> Unit = {
        PlatformBackGestureHandler(true, onBack = onBack) {
            Scaffold { innerPadding ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(0.dp)
                ) {

                    LaunchedEffect(state.commitEffect) {
                        if (state.commitEffect is Effect.Success<*, *>) {
                            vm.toast("产测成功！")
                            delay(1000)
                            toScan(packet)
                        }
                    }
                    ProductionScreen(Modifier.padding(innerPadding),
                        scanResult = scanResult,
                        productModel = packet.productModel,
                        product = packet.product,
                        state = state,
                        onBack = {
                            onBack()//toScan(packet)
                        },
                        onCommit = {
                            coroutineScope.launch {
                                vm.reset()
                            }
                        }, onPower = {
                            vm.power(!state.power)
                        }, onWash = {
                            vm.wash(!state.wash)
                        }, onProduction = {
                            vm.production()
                        }, onScan = {
                            toScan(packet)
                        }, onConnect = {
                            vm.connect()
                        }, onDebug = {
                            vm.debug(!state.debug)
                        })

                    val loadingConnect: Boolean by remember(state.connectEffect) {
                        derivedStateOf {
                            state.connectEffect.doing
                        }
                    }
                    val loadingCommit: Boolean by remember(state.commitEffect) {
                        derivedStateOf {
                            state.commitEffect.doing
                        }
                    }
                    if (loadingConnect) {
                        Dialog(
                            onDismissRequest = {

                            },
                            properties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CancelableLoadingComponent(
                                    modifier = Modifier.align(
                                        Alignment.Center
                                    )
                                ) {
                                    vm.stop()
                                }
                            }

                        }

                    }

                    if (loadingCommit) {
                        Dialog(
                            onDismissRequest = {

                            },
                            properties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                LoadingComponent(modifier = Modifier.align(Alignment.Center))
                            }

                        }

                    }

                    /*val show by remember(bottomSheetScaffoldState.bottomSheetState) {
                    derivedStateOf {
//                                    Log.d("[show]",
//                                        bottomSheetScaffoldState.bottomSheetState.run {
//                                            """
//                                                show
//                                                currentValue:${this.currentValue}
//                                                isVisible:${this.isVisible}
//                                                hasExpandedState:${this.hasExpandedState}
//                                                hasPartiallyExpandedState:${this.hasPartiallyExpandedState}
//                                            """.trimIndent()
//                                        }
//
//                                    )
                        bottomSheetScaffoldState.bottomSheetState.currentValue != BottomSheetValue.Expanded
                    }
                }*/

                    val show by remember(rememberModalBottomSheetState.currentValue) {
                        derivedStateOf {
//                                    Log.d("[show]",
//                                        bottomSheetScaffoldState.bottomSheetState.run {
//                                            """
//                                                show
//                                                currentValue:${this.currentValue}
//                                                isVisible:${this.isVisible}
//                                                hasExpandedState:${this.hasExpandedState}
//                                                hasPartiallyExpandedState:${this.hasPartiallyExpandedState}
//                                            """.trimIndent()
//                                        }
//
//                                    )
//                        bottomSheetScaffoldState.bottomSheetState.currentValue != BottomSheetValue.Expanded
                            !rememberModalBottomSheetState.isVisible
                        }
                    }
                    AnimatedVisibility(
                        show && state.debug, modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 64.dp, horizontal = 32.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
//                                bottomSheetScaffoldState.bottomSheetState.expand()

                                    rememberModalBottomSheetState.show()
                                }
                            }, contentColor = ZhongGuoSe.向日葵黄.color
                        ) {
                            Image(Icons.Default.DateRange, null)
                        }
                    }

                    Toast(
                        Modifier
                            .padding(horizontal = 32.dp, vertical = 120.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter), black = true, msg = toast, content = {
                            if (it is ToastData.Show) {
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .shadow(
                                            8.dp,
//                                            ambientColor = Color.Red,
//                                            spotColor = Color.Green
                                        )
                                        .background(Color.White)
                                        .padding(16.dp), contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = it.msg,
                                        modifier = Modifier,
                                        style = TextStyle.Default.copy(
                                            color = Color.Black,//中国色.大红.color,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    )
                                }
                            }
                        }
                    ) {
                        vm.clearToast()
                    }
                }
            }
        }
    }
//    BottomSheetScaffold(modifier = Modifier.fillMaxSize(),
//        scaffoldState = bottomSheetScaffoldState,
//        sheetPeekHeight = 0.dp,
//        sheetContent = {
//            histories()
//        }
//    ) { _ ->
//        content()
//    }


    ModalBottomSheetLayout(
        sheetContent = {
            histories()
        },
        modifier = Modifier.fillMaxSize(),
        sheetState = rememberModalBottomSheetState,//rememberModalBottomSheetState(ModalBottomSheetValue.Hidden),
        sheetGesturesEnabled = false
    ) {
        content()
    }
}