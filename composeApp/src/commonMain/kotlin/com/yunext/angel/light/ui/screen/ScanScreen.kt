package com.yunext.angel.light.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunext.angel.light.di.koinViewModelP1
import com.yunext.angel.light.domain.poly.ScanResult
import com.yunext.angel.light.ui.compoent.PlatformBackGestureHandler
import com.yunext.angel.light.ui.compoent.ScanComponent
import com.yunext.angel.light.ui.compoent.Toast
import com.yunext.angel.light.ui.viewmodel.ScanState
import com.yunext.angel.light.ui.viewmodel.ScanViewModel
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.kotlin.kmp.common.domain.Effect
import io.github.aakira.napier.Napier

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    packet: Packet,
    onScanResult: (Packet, ScanResult) -> Unit,
    onBack: () -> Unit
) {
    val vm: ScanViewModel = koinViewModelP1(packet)
    val coroutineScope = rememberCoroutineScope()
    val state by vm.state.collectAsStateWithLifecycle(ScanState(packet = packet))
//    val result by remember(state) {
//        derivedStateOf {
//            val remoteResult = state.scanResult
//            val remotePacket = state.packet
//            if (remotePacket != null && remoteResult != null) remotePacket to remoteResult else null
//        }
//    }


//    LaunchedEffect(result) {
//        XLog.d("Toast LaunchedEffect result  --${result}")
//        val r = result ?: return@LaunchedEffect
//        onScanResult(r.first, r.second)
//    }
    var errorMsg: String by remember {
        mutableStateOf("")
    }
    PlatformBackGestureHandler(true, onBack = onBack) {
        Box(
            Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        color = Color.Black
                    )
                }) {
//        if (state.effect !is Effect.Success) {
            ScanComponent(Modifier.fillMaxSize(),
                //status = { if (state.effect is Effect.Progress<*, *> /*|| result == null*/) ScanStatus.Checking else ScanStatus.Idle },
                onScanResult = { result ->
                    vm.check(result, packet)
                }, onScanFail = {
                    errorMsg = it
                }, type = packet.type, onBack = onBack
            )
//        }

            LaunchedEffect(state.effect) {
                Napier.e { "ScanScreen LaunchedEffect ${state.effect}" }
                when (val ef = state.effect) {
                    Effect.Completed -> {}
                    is Effect.Fail -> {
                        Napier.e { "ScanScreen check 失败 :$errorMsg" }
                        errorMsg = ef.output.message ?: "-"
                    }

                    Effect.Idle -> {}
                    is Effect.Progress<*, *> -> {}
                    is Effect.Success -> {
                        Napier.d { "ScanScreen check 成功 :${ef.output}" }
                        val (result, p) = ef.output
//                    coroutineScope.launch {
//                    delay(100)
                        onScanResult(p, result)
//                    }
                    }
                }
            }


            Toast(
                Modifier
                    .padding(horizontal = 32.dp, vertical = 120.dp)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter), errorMsg
            ) {
                errorMsg = ""
            }

//        val loading: Boolean by remember {
//            derivedStateOf {
//                state.effect == Effect.Doing
//            }
//        }
//        if (loading) {
//            BasicAlertDialog(
//                onDismissRequest = {
//
//                },
//                properties = DialogProperties(
//                    dismissOnBackPress = false,
//                    dismissOnClickOutside = false
//                )
//            ) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    LoadingComponent(modifier = Modifier.align(Alignment.Center))
//                }
//
//            }
//        }

        }
    }
}