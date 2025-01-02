package com.yunext.angel.light.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunext.angel.light.di.koinViewModelP1
import com.yunext.angel.light.domain.poly.ScanResultVo
import com.yunext.angel.light.theme.Color333
import com.yunext.angel.light.ui.common.clickablePure
import com.yunext.angel.light.ui.compoent.ScanComponent
import com.yunext.angel.light.ui.compoent.ScanStatus
import com.yunext.angel.light.ui.viewmodel.ScanState
import com.yunext.angel.light.ui.viewmodel.ScanViewModel
import com.yunext.angel.light.ui.vo.Packet
import com.yunext.kotlin.kmp.common.domain.Effect
import com.yunext.kotlin.kmp.common.domain.effectProgress
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    packet: Packet,
    onScanResult: (Packet, ScanResultVo) -> Unit,
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
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(
                    color = Color.Black
                )
            }) {
        ScanComponent(Modifier.fillMaxSize(),
            status = { if (state.effect is Effect.Progress<*, *> /*|| result == null*/) ScanStatus.Checking else ScanStatus.Idle },
            onScanResult = { result ->
                vm.check(result, packet)
            }, onScanFail = {
                coroutineScope.launch {
                    errorMsg = it
                    delay(ScanViewModel.DELAY)
                    errorMsg = ""
                }
            }, type = packet.type, onBack = onBack
        )



        LaunchedEffect(state.effect) {
            Napier.e { "ScanScreen LaunchedEffect ${state.effect}" }
            when (val ef = state.effect) {
                Effect.Completed -> {}
                is Effect.Fail -> {
                    Napier.e { "ScanScreen check 失败 :$errorMsg" }
                    errorMsg = ef.output.message ?: "-"
                    delay(ScanViewModel.DELAY)
                    errorMsg = ""
                }

                Effect.Idle -> {}
                is Effect.Progress<*, *> -> {}
                is Effect.Success -> {
                    Napier.d { "ScanScreen check 成功 :${ef.output}" }
                    val (result, p) = ef.output
                    onScanResult(p, result)
                }
            }
        }

//        LaunchedEffect(state) {
////            XLog.d("Toast errorMsg --${state.effect}")
//            when (val e = state.effect) {
//
//                Effect.Doing -> {}
//                is Effect.Fail -> {
//                    errorMsg = e.msg
//                    delay(ScanViewModel.DELAY)
//                    errorMsg = ""
//                }
//
//                Effect.Idle -> {}
//                Effect.Success -> {}
//            }
//
//        }

        AnimatedVisibility(

            errorMsg.isNotBlank(), modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()

        ) {
            Toast(
                Modifier
                    .wrapContentSize()
                    .align(Alignment.TopCenter), errorMsg
            )
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

sealed interface ToastData {
    data object Nan : ToastData
    data class Show(val msg: String) : ToastData
}

@Composable
private fun Toast(modifier: Modifier = Modifier, msg: String) {
    if (msg.isNotBlank()) {
        Box(
            modifier = modifier
                .padding(top = 120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .padding(16.dp), contentAlignment = Alignment.Center
        ) {
            Text(
                text = msg,
                modifier = Modifier,
                style = TextStyle.Default.copy(
                    color = Color.Red,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
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
            "扫码关联",
            style = TextStyle.Default.copy(
                fontWeight = FontWeight.Bold, fontSize = 17.sp,
            ),
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 200.dp),
        )

        Image(
            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            null,
            modifier = Modifier
                .size(44.dp)
                .align(Alignment.CenterStart)
                .clickablePure {
                    onBack()
                })


    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier, peiJianCode: String,
    wuLiuCode: String
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White),

        ) {

        val row = @Composable { title: String, value: String ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = title, style = TextStyle.Default.copy(
                        color = Color333, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    text = value, style = TextStyle.Default.copy(
                        color = Color.Black.copy(alpha = 0.9f),
                        fontSize = 16.sp, textAlign = TextAlign.End
                    ), maxLines = 1
                )
            }
        }

        row("配件码", peiJianCode)
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
        row("物流码", wuLiuCode)
    }


}