package com.yunext.angel.light.ui.compoent

import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import com.google.mlkit.vision.barcode.Barcode
import com.king.mlkit.vision.barcode.ViewfinderView
import com.king.mlkit.vision.barcode.analyze.BarcodeScanningAnalyzer
import com.king.mlkit.vision.camera.AnalyzeResult
import com.king.mlkit.vision.camera.BaseCameraScan
import com.king.mlkit.vision.camera.CameraScan


@Composable
actual fun ScanReal(
    flashLight: Boolean,
    onScanResult: (String) -> Unit,
    onScanFail: (String) -> Unit,
) {
    var cameraScan: CameraScan<*>? by remember() { mutableStateOf(null) }
    val opened: Boolean by remember(flashLight) { mutableStateOf(flashLight) }
    val activity = LocalContext.current as FragmentActivity
    LaunchedEffect(opened, cameraScan) {
        if (opened) {
            cameraScan?.enableTorch(true)
        } else {
            cameraScan?.enableTorch(false)
        }
    }
    val previewViewComposable: @Composable () -> Unit = {
        AndroidView(
            factory = { context ->
                println("ScanComponent -----------------------------")
                println("ScanComponent AndroidView::factory")

                val previewView = PreviewView(activity).apply {
//                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE // FIX:会出现显示不全的情况
                }
                val callback = object : CameraScan.OnScanResultCallback<List<Barcode>> {
                    override fun onScanResultCallback(result: AnalyzeResult<List<Barcode>>) {
                        println("ScanComponent result:$result")
                        try {
                            val data = result.result[0].displayValue ?: return

                            onScanResult.invoke(data)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }

                    override fun onScanResultFailure() {
                        super.onScanResultFailure()
                        println("ScanComponent onScanResultFailure")
                        // onScanFail("scan error")
                    }
                }
                val baseCameraScan = BaseCameraScan<List<Barcode>>(activity, previewView)
                    .setAnalyzer(BarcodeScanningAnalyzer(Barcode.FORMAT_ALL_FORMATS))
                    .setOnScanResultCallback(callback)
                cameraScan = baseCameraScan
                previewView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                println("ScanComponent startScan $cameraScan")
                cameraScan?.startCamera()
                previewView
            },
            modifier = Modifier.fillMaxSize(),
            update = { contentView ->
                println("ScanComponent AndroidView::update ${contentView.hashCode()}")
            },
            onReset = { contentView ->
                println("ScanComponent AndroidView::onReset ${contentView.hashCode()}")
            },
            onRelease = { contentView ->
                println("ScanComponent AndroidView::onRelease ${contentView.hashCode()}")
                cameraScan?.setOnScanResultCallback(null)
                cameraScan?.release()
                cameraScan = null
            })
    }

    val viewfinderViewComposable: @Composable () -> Unit = {
        AndroidView(
            factory = { context ->
                //<editor-fold desc="初始化">
                val p: ViewfinderView = ViewfinderView(activity)
                p.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                p.post {
                    p.requestLayout()
                }
                p
            },
            modifier = Modifier.fillMaxSize(),
            update = { _ ->
            },
            onReset = {
            },
            onRelease = {

            })
    }
    Box(Modifier.fillMaxSize()) {
        previewViewComposable()

        viewfinderViewComposable()
    }
}