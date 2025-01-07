package com.yunext.angel.light

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.yunext.angel.light.util.ToastUtil

class MainActivity : FragmentActivity() {

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

            LaunchedEffect(Unit) {
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
        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //12
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.BLUETOOTH_SCAN
        } else {
            null
        }, run {
            /*if (Build.VERSION.SDK_INT in (Build.VERSION_CODES.Q..<Build.VERSION_CODES.S)) {
                // 10 - 12
                val has = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                Log.d(
                    "MainActivity",
                    "launch requestPermission has ACCESS_BACKGROUND_LOCATION $has "
                )
                if (has) null else Manifest.permission.ACCESS_BACKGROUND_LOCATION
            } else*/ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {//12
                null
            } else {
                val has = ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                if (has) null else Manifest.permission.ACCESS_FINE_LOCATION
            }
        }, run {
            val has = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (has) null else Manifest.permission.CAMERA
        }
    ).also {
        Log.d("MainActivity", "launch requestPermission :${Build.VERSION.SDK_INT} list:$it")
    }


    private fun requestPermission() {
        val permissions = loadPermissionNoGant()
        if (permissions.isNotEmpty()) {
            Log.d("MainActivity", "launch requestPermission :$permissions")
            launcher.launch(permissions.toTypedArray())
        }

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}