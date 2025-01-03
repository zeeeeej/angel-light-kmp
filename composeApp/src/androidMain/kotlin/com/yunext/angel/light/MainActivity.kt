package com.yunext.angel.light

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.yunext.angel.light.util.ToastUtil

class MainActivity : ComponentActivity() {

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        checkPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()

            LaunchedEffect(Unit){
                requestPermission()
            }
        }

    }

    private fun checkPermission() {
        val permissions = loadPermissionNoGant()
        if (permissions.isNotEmpty()) {
            ToastUtil.toast("请检查APP权限，以便正常使用。")
        }
    }

    private fun loadPermissionNoGant() = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.BLUETOOTH_CONNECT
        } else {
            null
        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.BLUETOOTH_SCAN
        } else {
            null
        }, run {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.ACCESS_FINE_LOCATION
        }, run {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.CAMERA
        }
    )


    private fun requestPermission() {
        val permissions = loadPermissionNoGant()
        if (permissions.isNotEmpty()) {
            launcher.launch(permissions.toTypedArray())
        }

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}