package com.yunext.angel.light.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.BottomSheetValue.Collapsed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import color
import com.yunext.angel.light.di.koinViewModelP2
import com.yunext.angel.light.ui.compoent.CancelableLoadingComponent
import com.yunext.angel.light.ui.compoent.HistoriesInfo
import com.yunext.angel.light.ui.compoent.LoadingComponent
import com.yunext.angel.light.ui.viewmodel.ProductionState
import com.yunext.angel.light.ui.viewmodel.ProductionViewModel
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.angel.light.ui.vo.ScanResultVo
import com.yunext.kotlin.kmp.common.domain.doing
import kotlinx.coroutines.launch


@Composable
fun ProductionScreenWithVm(
    modifier: Modifier = Modifier,
    scanResult: ScanResultVo,
    packet: Packet,
    toScan: (Packet) -> Unit,
    onBack:()->Unit
) {
    val vm: ProductionViewModel = koinViewModelP2(scanResult,packet)
    val state by vm.state.collectAsStateWithLifecycle(ProductionState(product = packet.product, productModel = packet.productModel, productType = packet.type,scanResult=scanResult))
    val coroutineScope = rememberCoroutineScope()
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState( Collapsed)
    )
//                LaunchedEffect(Unit) {
//                    bottomSheetScaffoldState.bottomSheetState.expand()
//                }
    var loggerSupport: Boolean by remember {
        mutableStateOf(false)
    }

    val toast by remember(
        state.toast
    ) {
        derivedStateOf {
            state.toast
        }
    }
    LaunchedEffect(Unit) {
        snapshotFlow {
            toast
        }.collect {
            if (it.isEmpty()) {
            } else {
                            bottomSheetScaffoldState.snackbarHostState.showSnackbar(it)
//                ToastUtil.toast(it)
                vm.clearToast()
            }
        }

    }
    val histories: @Composable () -> Unit = {
        HistoriesInfo(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 320.dp, max = 320.dp), list = state.bleLogs, onClose = {
                coroutineScope.launch {
                    bottomSheetScaffoldState.bottomSheetState.collapse()
                }
            }, onShare = {
                if (it.isNotEmpty()) {
                    coroutineScope.launch {
//                        clipBroad(
//                            MyApp.CONTEXT,
//                            "bleLog",
//                            it.joinToString("\n") { h -> h.display })
//                        ToastUtil.toast("已复制至剪贴板！")
                        // TODO
                    }
                }
            }
        )
    }
    BottomSheetScaffold(modifier = Modifier.fillMaxSize(),
        scaffoldState = bottomSheetScaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            histories()
        }
    ) { _ ->
        Scaffold { innerPadding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(0.dp)
            ) {

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
                            val r = vm.commit()
                            if (r) {
                                toScan(packet)
                            }
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
                        loggerSupport = !loggerSupport
                    })

                val loadingConnect: Boolean by remember {
                    derivedStateOf {
                        state.connectEffect .doing
                    }
                }
                val loadingCommit: Boolean by remember {
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

                val show by remember(bottomSheetScaffoldState.bottomSheetState) {
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
                }
                AnimatedVisibility(
                    show && loggerSupport, modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(vertical = 64.dp, horizontal = 32.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                bottomSheetScaffoldState.bottomSheetState.expand()
                            }
                        }, contentColor = ZhongGuoSe.向日葵黄.color
                    ) {
                        Image(Icons.Default.DateRange, null)
                    }
                }

//                            Text("${state.bleLogs.size}条", fontSize = 44.sp)
            }
        }
    }
}